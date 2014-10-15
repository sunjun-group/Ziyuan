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
import java.util.List;
import java.util.Locale;
import java.util.Set;

import sav.common.core.SavException;
import sav.common.core.iface.IPrintStream;
import sav.common.core.utils.Assert;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.BreakPoint;
import sav.strategies.slicing.ISlicer;
import sav.strategies.vm.VMConfiguration;
import de.unisb.cs.st.javaslicer.common.classRepresentation.Instruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.ReadClass;
import de.unisb.cs.st.javaslicer.slicing.SliceInstructionsCollector;
import de.unisb.cs.st.javaslicer.slicing.Slicer;
import de.unisb.cs.st.javaslicer.slicing.SlicingCriterion;
import de.unisb.cs.st.javaslicer.traceResult.ThreadId;
import de.unisb.cs.st.javaslicer.traceResult.TraceResult;

/**
 * @author LLT
 * 
 */
public class JavaSlicer implements ISlicer {
	private JavaSlicerVmRunner vmRunner;
	private VMConfiguration vmConfig;
	private List<String> analyzedClasses;
	private IPrintStream vmRunnerPrintStream;

	public void setTracerJarPath(String tracerJarPath) {
		vmRunner = new JavaSlicerVmRunner(tracerJarPath);
	}
	
	public void setVmConfig(VMConfiguration vmConfig) {
		this.vmConfig = vmConfig;
	}
	
	public void setVmRunnerPrintStream(IPrintStream vmRunnerPrintStream) {
		this.vmRunnerPrintStream = vmRunnerPrintStream;
	}
	
	@Override
	public void setAnalyzedClasses(List<String> analyzedClasses) {
		this.analyzedClasses = analyzedClasses;
	}
	
	/**
	 * @param vmConfig:
	 * requires: javaHome, classpaths
	 */
	public List<BreakPoint> slice(List<BreakPoint> bkps, List<String> junitClassNames)
			throws SavException, IOException, InterruptedException {
		String tempFileName = createTraceFile(junitClassNames);
		
		/* do slicing */
		return slice(tempFileName, bkps);
	}

	public String createTraceFile(List<String> junitClassNames)
			throws IOException, SavException, InterruptedException {
		File tempFile = File.createTempFile(getTraceFileName(), ".trace");
		String tempFileName = tempFile.getAbsolutePath();
		/* run program and create trace file */
		vmConfig.setLaunchClass(TestcasesRunner.class.getCanonicalName())
				.addProgramArgs(TestcasesRunner.toArgs(junitClassNames));
		vmRunner.setTraceFilePath(tempFileName);
		vmRunner.setOut(vmRunnerPrintStream);
		vmRunner.startAndWaitUntilStop(vmConfig);

		return tempFileName;
	}
	
	public List<BreakPoint> slice(String traceFilePath, List<BreakPoint> bkps) throws InterruptedException {
		File traceFile = new File(traceFilePath);
		TraceResult trace;
		try {
			trace = TraceResult.readFrom(traceFile);
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<BreakPoint>();
		}

		List<SlicingCriterion> criteria = new ArrayList<SlicingCriterion>(bkps.size());
		for (BreakPoint bkp : bkps) {
			SlicingCriterion criterion = SlicingCriterion.parse(
					buildSlicingCriterionStr(bkp), trace.getReadClasses());
			criteria.add(criterion);
		}

		List<ThreadId> threads = trace.getThreads();
		if (threads.size() == 0) {
			//TODO
			System.err
					.println("The trace file contains no tracing information.");
			System.exit(-1);
		}

		ThreadId tracing = null;
		for (ThreadId t : threads) {
			if ("main".equals(t.getThreadName())
					&& (tracing == null || t.getJavaThreadId() < tracing
							.getJavaThreadId()))
				tracing = t;
		}

		if (tracing == null) {
			System.err.println("Couldn't find the main thread.");
			return new ArrayList<BreakPoint>();
		}

		long startTime = System.nanoTime();
		Slicer slicer = new Slicer(trace);
		SliceInstructionsCollector collector = new SliceInstructionsCollector();
		slicer.addSliceVisitor(collector);
		slicer.process(tracing, criteria, false);
		Set<Instruction> slice = collector.getDynamicSlice();
		long endTime = System.nanoTime();
		List<BreakPoint> result = new ArrayList<BreakPoint>();
		for (Instruction inst : slice) {
			ReadClass clazz = inst.getMethod().getReadClass();
			if (appClass(clazz)) {
				BreakPoint bkp = new BreakPoint(clazz.getName(), 
						inst.getMethod().getName(), inst.getLineNumber());
				CollectionUtils.addIfNotNullNotExist(result, bkp);
			}
		}
		
		System.out.format((Locale) null, "Computation took %.2f seconds.%n",
				1e-9 * (endTime - startTime));
		return result;
	}

	private boolean appClass(ReadClass clazz) {
		Assert.assertTrue(analyzedClasses != null);
		if (analyzedClasses.contains(clazz.getName())) {
			return true;
		}
		return false;
	}

	private String buildSlicingCriterionStr(BreakPoint bkp) {
		return String.format("%s.%s:%s:*", bkp.getClassCanonicalName(),
				bkp.getMethodName(), bkp.getLineNo());
	}

	private String getTraceFileName() {
		return "javaSlicer";
	}
}
