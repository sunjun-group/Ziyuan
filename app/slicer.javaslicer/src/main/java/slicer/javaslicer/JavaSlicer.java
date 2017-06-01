/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
import sav.common.core.SystemVariables;
import sav.common.core.utils.BreakpointUtils;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StopTimer;
import sav.strategies.common.VarInheritCustomizer;
import sav.strategies.common.VarInheritCustomizer.InheritType;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;
import sav.strategies.junit.JunitRunner;
import sav.strategies.junit.JunitRunner.JunitRunnerProgramArgBuilder;
import sav.strategies.junit.SavJunitRunner;
import sav.strategies.slicing.ISlicer;
import sav.strategies.vm.VMConfiguration;
import slicer.javaslicer.instruction.variable.tree.InstructionContext;
import de.unisb.cs.st.javaslicer.slicing.Slicer;
import de.unisb.cs.st.javaslicer.slicing.SlicingCriterion;
import de.unisb.cs.st.javaslicer.traceResult.ThreadId;
import de.unisb.cs.st.javaslicer.traceResult.TraceResult;

/**
 * @author LLT
 * 
 */
public class JavaSlicer implements ISlicer {
	private Logger log = LoggerFactory.getLogger(JavaSlicer.class);
	private JavaSlicerVmRunner vmRunner;
	private VMConfiguration vmConfig;
	private SliceBreakpointCollector sliceCollector;
	private StopTimer timer;
	
	public JavaSlicer() {
		vmRunner = new JavaSlicerVmRunner();
		timer = new StopTimer("Slicing");
	}
	
	public void init(AppJavaClassPath appClasspath) {
		if (sliceCollector == null) {
			sliceCollector = new SliceBreakpointCollector();
		}
		sliceCollector.reset();
		if (appClasspath.getPreferences().getBoolean(
				SystemVariables.SLICE_COLLECT_VAR)) {
			sliceCollector.setVariableCollectorContext(new InstructionContext());
		}
		VarInheritCustomizer.InheritType varInheritType = InheritType
										.of(appClasspath.getPreferences().getString(
												SystemVariables.SLICE_BKP_VAR_INHERIT));
		if (varInheritType != null) {
			sliceCollector.setBkpCustomizer(new VarInheritCustomizer(varInheritType));
		}
		timer.start();
		vmConfig = SavJunitRunner.createVmConfig(appClasspath);
	}

	/**
	 * requires: javaHome, classpaths
	 */
	public List<BreakPoint> slice(AppJavaClassPath appClassPath, List<BreakPoint> bkps,
			List<String> junitClassMethods) throws SavException, IOException,
			InterruptedException, ClassNotFoundException {
		init(appClassPath);
		if (CollectionUtils.isEmpty(bkps)) {
			log.warn("List of breakpoints to slice is empty");
			return new ArrayList<BreakPoint>();
		}
		timer.newPoint("create Trace file");
		String traceFilePath = createTraceFile(junitClassMethods);
		
		/* do slicing */
		timer.newPoint("slice");
		List<BreakPoint> result = sliceFromTraceFile(traceFilePath,
				new HashSet<BreakPoint>(bkps), junitClassMethods);
		timer.logResults(log);
		return result;
	}

	/**
	 * we start jvm to execute this cmd:
	 * 
	 * java -javaagent:[/tracer.jar]=tracefile:[temfile.trace] 
	 * 		-cp [project classpath + path of tzuyuSlicer.jar] sav.strategies.junit.JunitRunner
	 * 		-methods [classMethods]
	 */
	public String createTraceFile(List<String> junitClassMethods)
			throws IOException, SavException, InterruptedException,
			ClassNotFoundException {
		log.debug("Slicing-creating trace file...");
		File tempFile = File.createTempFile(getTraceFileName(), ".trace");
//		log.info(tempFile.toString());
		String tempFileName = tempFile.getAbsolutePath();
		/* run program and create trace file */
		vmConfig.setLaunchClass(JunitRunner.class.getName());
		List<String> arguments = new JunitRunnerProgramArgBuilder().methods(
				junitClassMethods).build();
		vmRunner.setProgramArgs(arguments);
		/**/
		vmRunner.setTraceFilePath(tempFileName);
		if (!vmRunner.startAndWaitUntilStop(vmConfig)) {
			throw new SavException(ModuleEnum.SLICING, vmRunner.getProccessError());
		}

		return tempFileName;
	}
	
	public TraceResult readTraceFile(String traceFilePath) {
		log.debug("Slicing-slicing...");
		log.debug("traceFilePath=", traceFilePath);
		File traceFile = new File(traceFilePath);
		
		TraceResult trace;
		try {
			timer.newPoint("read trace file");
			trace = TraceResult.readFrom(traceFile);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return trace;
	}
	
	public List<BreakPoint> sliceFromTraceResult(TraceResult trace, Collection<BreakPoint> bkps,
			List<String> junitClassMethods)
			throws InterruptedException, SavException {
		List<SlicingCriterion> criteria = new ArrayList<SlicingCriterion>(bkps.size());
		for (BreakPoint bkp : bkps) {
			try {
				SlicingCriterion criterion = SlicingCriterion.parse(
						buildSlicingCriterionStr(bkp), trace.getReadClasses());
				criteria.add(criterion);
			} catch (IllegalArgumentException e) {
				String classMethodStr = ClassUtils.toClassMethodStr(bkp.getClassCanonicalName(), bkp.getMethodName());
				if (!junitClassMethods.contains(classMethodStr)) {
					throw e;
				} 
				// ignore
			}
		}

		List<ThreadId> threads = trace.getThreads();
		if (threads.size() == 0) {
			throw new SavException(ModuleEnum.SLICING, "trace.threads.size=0");
		}

		ThreadId tracing = null;
		for (ThreadId t : threads) {
			if ("main".equals(t.getThreadName())
					&& (tracing == null || t.getJavaThreadId() < tracing
							.getJavaThreadId()))
				tracing = t;
		}

		if (tracing == null) {
			log.error("Couldn't find the main thread.");
			return new ArrayList<BreakPoint>();
		}
		Slicer slicer = new Slicer(trace);
		slicer.addSliceVisitor(sliceCollector);
		slicer.process(tracing, criteria, true);
		log.debug("Read Slicing Result:");
		List<BreakPoint> dynamicSlice = sliceCollector.getDynamicSlice();
		if (log.isDebugEnabled()) {
			log.debug("slicing-result:");
			for (BreakPoint bkp : dynamicSlice) {
				log.debug(bkp.getId());
			}
		}
		return dynamicSlice;
	}
	
	public List<BreakPoint> sliceFromTraceFile(String traceFilePath, Collection<BreakPoint> bkps,
			List<String> junitClassMethods)
			throws InterruptedException, SavException {
		log.debug("Slicing-slicing...");
		log.debug("traceFilePath=", traceFilePath);
		log.debug("entry points=", BreakpointUtils.getPrintStr(bkps));
		File traceFile = new File(traceFilePath);
		TraceResult trace;
		try {
			timer.newPoint("read trace file");
			trace = TraceResult.readFrom(traceFile);
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<BreakPoint>();
		}

		List<SlicingCriterion> criteria = new ArrayList<SlicingCriterion>(bkps.size());
		for (BreakPoint bkp : bkps) {
			try {
				SlicingCriterion criterion = SlicingCriterion.parse(
						buildSlicingCriterionStr(bkp), trace.getReadClasses());
				criteria.add(criterion);
			} catch (IllegalArgumentException e) {
				String classMethodStr = ClassUtils.toClassMethodStr(bkp.getClassCanonicalName(), bkp.getMethodName());
				if (!junitClassMethods.contains(classMethodStr)) {
					throw e;
				} 
				// ignore
			}
		}

		List<ThreadId> threads = trace.getThreads();
		if (threads.size() == 0) {
			throw new SavException(ModuleEnum.SLICING, "trace.threads.size=0");
		}

		ThreadId tracing = null;
		for (ThreadId t : threads) {
			if ("main".equals(t.getThreadName())
					&& (tracing == null || t.getJavaThreadId() < tracing
							.getJavaThreadId()))
				tracing = t;
		}

		if (tracing == null) {
			log.error("Couldn't find the main thread.");
			return new ArrayList<BreakPoint>();
		}
		Slicer slicer = new Slicer(trace);
		slicer.addSliceVisitor(sliceCollector);
		slicer.process(tracing, criteria, true);
		log.debug("Read Slicing Result:");
		List<BreakPoint> dynamicSlice = sliceCollector.getDynamicSlice();
		if (log.isDebugEnabled()) {
			log.debug("slicing-result:");
			for (BreakPoint bkp : dynamicSlice) {
				log.debug(bkp.getId());
			}
		}
		return dynamicSlice;
	}

	private String buildSlicingCriterionStr(BreakPoint bkp) {
		return String.format("%s.%s:%s:*", bkp.getClassCanonicalName(),
				bkp.getMethodName(), bkp.getLineNo());
	}

	private String getTraceFileName() {
		return "javaSlicer";
	}

	@Override
	public void setFiltering(List<String> analyzedClasses,
			List<String> analyzedPackages) {
		if (CollectionUtils.isNotEmpty(analyzedPackages)) {
			sliceCollector = new ClassPkgFilterSliceCollector(analyzedClasses, analyzedPackages); 
		} else {
			sliceCollector = new ClassFilterSliceCollector(analyzedClasses);
		}
	}
}
