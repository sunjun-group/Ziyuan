/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import icsetlv.common.dto.BkpInvariantResult;
import icsetlv.common.exception.IcsetlvException;
import icsetlv.variable.VariableNameCollector;
import japa.parser.ast.CompilationUnit;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import main.FaultLocalization;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.SystemVariables;
import sav.common.core.utils.BreakpointUtils;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StopTimer;
import sav.common.core.utils.StringUtils;
import sav.strategies.IApplicationContext;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.ClassLocation;
import sav.strategies.dto.DebugLine;
import sav.strategies.mutanbug.DebugLineInsertionResult;
import sav.strategies.vm.VMConfiguration;
import tzuyu.core.machinelearning.LearnInvariants;
import tzuyu.core.mutantbug.MutanBug;
import tzuyu.core.mutantbug.Recompiler;
import faultLocalization.FaultLocalizationReport;
import faultLocalization.LineCoverageInfo;
import gentest.builder.FixTraceGentestBuilder;
import gentest.core.data.Sequence;
import gentest.junit.FileCompilationUnitPrinter;
import gentest.junit.ICompilationUnitPrinter;
import gentest.junit.TestsPrinter;


/**
 * @author LLT
 *
 */
public class TzuyuCore {
	protected static Logger log = LoggerFactory.getLogger(TzuyuCore.class);
	protected IApplicationContext appContext;
	protected MutanBug mutanbug;
	
	public TzuyuCore(IApplicationContext appContext) {
		this.appContext = appContext;
	}

	public FaultLocalizationReport faultLocalization(
			List<String> testingClassNames, List<String> testingPackages,
			List<String> junitClassNames) throws Exception {
		FaultLocalization analyzer = new FaultLocalization(appContext);
		FaultLocalizationReport report = analyzer.analyse(
				testingClassNames, testingPackages, junitClassNames);
		// mutation(report, junitClassNames);
		return report;
	}
	
	protected FaultLocalizationReport computeSuspiciousness(FaultLocateParams params) throws Exception {
		log.info("Compute suspiciousness: ");
		appContext.getAppData().getPreferences()
						.setBoolean(SystemVariables.FAULT_LOCATE_USE_SLICE,
						params.isSlicerEnable() && !params.getTestingPkgs().isEmpty());
		FaultLocalizationReport report = faultLocalization(params.getTestingClassNames(), params.getTestingPkgs(),
				params.getJunitClassNames());
		log.info(StringUtils.toStringNullToEmpty(report));
		return report;
	}

	public void ziyuan(FaultLocateParams params) throws Exception {
		StopTimer timer = new StopTimer("FaultLocate");
		timer.newPoint("computing suspiciousness");
		FaultLocalizationReport report = computeSuspiciousness(params);
		if (params.isMutationEnable()) {
			timer.newPoint("mutation");
			mutation(report, params.getJunitClassNames(), params.getRankToExamine());
		}
		if (params.isMachineLearningEnable()) {
			timer.newPoint("machine learning");
			machineLearning(report, params);
		}
		timer.logResults(log);
	}

	private void mutation(FaultLocalizationReport report,
			List<String> junitClassNames, int rankToExamine) throws Exception {
		log.info("Running Mutation");
		MutanBug mutanbug = new MutanBug();
		mutanbug.setAppData(appContext.getAppData());
		mutanbug.setMutator(appContext.getMutator());
		mutanbug.mutateAndRunTests(report, rankToExamine, junitClassNames);
		log.info(StringUtils.toStringNullToEmpty(report));
	}
	
	protected void machineLearning(FaultLocalizationReport report,
			FaultLocateParams params) throws ClassNotFoundException,
			SavException, IcsetlvException, Exception {
		log.info("Running Machine Learning");
		List<String> junitClassNames = new ArrayList<String>(params.getJunitClassNames());
		if (params.isGenTestEnable()) {
			while (true) {
				try {
					List<String> randomTests = generateNewTests(
							params.getTestingClassName(),
							params.getMethodName(), 
							params.getVerificationMethod(),
							params.getNumberOfTestCases());
					junitClassNames.addAll(randomTests);
					break;
				} catch (Throwable exception) {

				}
			}
		}

		final List<LineCoverageInfo> suspectLocations = report.getFirstRanks(params
				.getRankToExamine());
		
		if (CollectionUtils.isEmpty(suspectLocations)) {
			log.warn("No suspect line to learn. SVM will not run.");
		} else {
			AppJavaClassPath appClasspath = appContext.getAppData();
			filter(suspectLocations, appClasspath.getSrc());
			if (log.isDebugEnabled()) {
				log.debug("before grouping: ");
				log.debug(StringUtils.join(suspectLocations, "\n"));
			}
			// Select from suspectLocations to monitor
			List<LineCoverageInfo> selectedLocations = suspectLocations;
			if (params.isGroupLines()) {
				selectedLocations = selectLinesByGrouping(suspectLocations);
			}

			/* compute variables appearing at each breakpoint */
			VariableNameCollector nameCollector = new VariableNameCollector(
					params.getVarNameCollectionMode(), appClasspath.getSrc());
			LocatedLines locatedLines = new LocatedLines(selectedLocations);
			nameCollector.updateVariables(locatedLines.getLocatedLines());
			/*
			 * add new debug line if needed in order to collect data of
			 * variables at a certain line after that line is executed
			 */
			List<DebugLine> debugLines = getDebugLines(locatedLines.getLocatedLines());
			DebugLinePreProcessor preProcessor = new DebugLinePreProcessor();
			debugLines = preProcessor.preProcess(debugLines);
			if (log.isDebugEnabled()) {
				log.debug("after grouping & processing: ");
				log.debug(StringUtils.join(debugLines, "\n"));
			}
			
			LearnInvariants learnInvariant = new LearnInvariants(appClasspath, params);
			List<BkpInvariantResult> invariants = learnInvariant.learn(new ArrayList<BreakPoint>(debugLines), 
										junitClassNames, appClasspath.getSrc());
			
			locatedLines.updateInvariantResult(invariants);
			
			log.info("----------------FINISHED--------------------");
			log.info(locatedLines.getDisplayResult());
			/* clean up mutanbug */
			if (mutanbug != null) {
				mutanbug.restoreFiles();
			}
		}
	}

	private List<LineCoverageInfo> selectLinesByGrouping(
			final List<LineCoverageInfo> suspectLocations) {
		if (suspectLocations == null || suspectLocations.size() == 0) {
			return new ArrayList<LineCoverageInfo>(0);
		}

		List<LineCoverageInfo> lines = new ArrayList<LineCoverageInfo>(suspectLocations.size());

		// Sort the lines based on its location in a class
		Collections.sort(suspectLocations, new Comparator<LineCoverageInfo>() {
			@Override
			public int compare(LineCoverageInfo l1, LineCoverageInfo l2) {
				final ClassLocation location1 = l1.getLocation();
				final ClassLocation location2 = l2.getLocation();
				return new CompareToBuilder()
						.append(location1.getClassCanonicalName(),
								location2.getClassCanonicalName())
						.append(location1.getLineNo(), location2.getLineNo()).toComparison();
			}
		});

		LineCoverageInfo groupStart = null;
		LineCoverageInfo groupEnd = null;
		LineCoverageInfo lastLine = null;
		final Iterator<LineCoverageInfo> iterator = suspectLocations.iterator();

		while (iterator.hasNext()) {
			final LineCoverageInfo line = iterator.next();
			if (groupStart == null) {
				groupStart = line;
				groupEnd = line;
			} else {
				final ClassLocation lastLocation = lastLine.getLocation();
				final ClassLocation lineLocation = line.getLocation();
				if (lastLocation.getClassCanonicalName().equals(
						lineLocation.getClassCanonicalName())
						&& lineLocation.getLineNo() - lastLocation.getLineNo() == 1) {
					groupEnd = line;
				} else {
					addLineGroup(lines, groupStart, groupEnd);
					groupStart = line;
					groupEnd = line;
				}
			}
			lastLine = line;
		}
		addLineGroup(lines, groupStart, groupEnd);

		return lines;
	}

	private void addLineGroup(List<LineCoverageInfo> lines, LineCoverageInfo startLine,
			LineCoverageInfo endLine) {
		lines.add(startLine);
		if (!startLine.equals(endLine)) {
			lines.add(endLine);
		}
	}
	
	protected List<String> generateNewTests(String testingClassName,
			String methodName, String verificationMethod, int numberOfTestCases)
			throws ClassNotFoundException, SavException {
		Class<?> targetClass = Class.forName(testingClassName);
		return generateNewTests(testingClassName, methodName, verificationMethod, numberOfTestCases, targetClass.getSimpleName());
	}

	protected List<String> generateNewTests(String testingClassName,
			String methodName, String verificationMethod, int numberOfTestCases, String classPrefix)
			throws ClassNotFoundException, SavException {
		AppJavaClassPath appClasspath = appContext.getAppData();
		Class<?> targetClass = Class.forName(testingClassName);
		
		FixTraceGentestBuilder builder = new FixTraceGentestBuilder(numberOfTestCases );
		
		String methodAlias = "methodName";
		builder.forClass(targetClass).method(methodName, methodAlias);
		if (verificationMethod != null) {
			builder.evaluationMethod(Class.forName(testingClassName), verificationMethod,
					methodAlias).paramAutofill();
		}
		Pair<List<Sequence>, List<Sequence>> testcases = builder.generate();
		final FileCompilationUnitPrinter cuPrinter = new FileCompilationUnitPrinter(
				appClasspath.getSrc());
		final List<String> junitClassNames = new ArrayList<String>();
		TestsPrinter printer = new TestsPrinter("test", null, "test",
				classPrefix, new ICompilationUnitPrinter() {
					
					@Override
					public void print(List<CompilationUnit> compilationUnits) {
						for (CompilationUnit cu : compilationUnits) {
							junitClassNames.add(ClassUtils.getCanonicalName(cu
									.getPackage().getName().getName(), cu
									.getTypes().get(0).getName()));
						}
						cuPrinter.print(compilationUnits);
					}
				});
		printer.printTests(testcases);
		List<File> generatedFiles = cuPrinter.getGeneratedFiles();
		
		Recompiler recompiler = new Recompiler(new VMConfiguration(appClasspath));
		recompiler.recompileJFile(appClasspath.getTestTarget(), generatedFiles);
		
		return junitClassNames;
	}

	protected List<DebugLine> getDebugLines(List<BreakPoint> locatedLines) throws SavException {
		mutanbug = getMutanbug();
		mutanbug.setAppData(appContext.getAppData());
		mutanbug.setMutator(appContext.getMutator());
		Map<String, List<BreakPoint>> brkpsMap = BreakpointUtils.initBrkpsMap(locatedLines);
		Map<String, DebugLineInsertionResult> mutationInfo = mutanbug.mutateForMachineLearning(brkpsMap);
		List<DebugLine> debugLines = collectDebugLines(brkpsMap, mutationInfo);
		return debugLines;
	}
	
	private void filter(List<LineCoverageInfo> lineInfos, String appSrc) {
		Map<String, Boolean> fileExistance = new HashMap<String, Boolean>();
		for (Iterator<LineCoverageInfo> it = lineInfos.iterator(); it.hasNext(); ) {
			LineCoverageInfo lineInfo = it.next();
			String className = lineInfo.getLocation().getClassCanonicalName();
			Boolean exist = fileExistance.get(className);
			if (exist == null) {
				exist = mutation.utils.FileUtils.doesFileExist(ClassUtils
						.getJFilePath(appSrc, className));
				fileExistance.put(className, exist);
			}
			if (!exist) {
				it.remove();
			}
		}
	}
	
	private List<DebugLine> collectDebugLines(Map<String, List<BreakPoint>> classLocationMap,
			Map<String, DebugLineInsertionResult> mutationInfo) {
		List<DebugLine> debugLines = new ArrayList<DebugLine>();
		for (String className : classLocationMap.keySet()) {
			DebugLineInsertionResult lineInfo = mutationInfo.get(className);
			Map<Integer, Integer> lineToNextLine = lineInfo.getOldNewLocMap();
			List<BreakPoint> bkpsInClass = classLocationMap.get(className);
			for(BreakPoint location: bkpsInClass){
				Integer newLineNo = lineToNextLine.get(location.getLineNo());
				DebugLine debugLine = new DebugLine(location, newLineNo);
				debugLines.add(debugLine);
			}
		}
		return debugLines;
	}

	private MutanBug getMutanbug() {
		if (mutanbug == null) {
			mutanbug = new MutanBug();
		}
		return mutanbug;
	}

}
