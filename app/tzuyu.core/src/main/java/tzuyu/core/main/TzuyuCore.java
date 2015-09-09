/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.CompareToBuilder;

import faultLocalization.FaultLocalizationReport;
import faultLocalization.LineCoverageInfo;
import gentest.builder.FixTraceGentestBuilder;
import gentest.core.data.Sequence;
import gentest.junit.FileCompilationUnitPrinter;
import gentest.junit.ICompilationUnitPrinter;
import gentest.junit.TestsPrinter;
import icsetlv.common.dto.BkpInvariantResult;
import icsetlv.common.exception.IcsetlvException;
import icsetlv.variable.VariableNameCollector;
import japa.parser.ast.CompilationUnit;
import main.FaultLocalization;
import sav.common.core.Logger;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.utils.BreakpointUtils;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StopTimer;
import sav.common.core.utils.StringUtils;
import sav.strategies.IApplicationContext;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.ClassLocation;
import sav.strategies.dto.DebugLine;
import sav.strategies.mutanbug.DebugLineInsertionResult;
import sav.strategies.vm.VMConfiguration;
import tzuyu.core.inject.ApplicationData;
import tzuyu.core.machinelearning.LearnInvariants;
import tzuyu.core.mutantbug.MutanBug;
import tzuyu.core.mutantbug.Recompiler;


/**
 * @author LLT
 *
 */
public class TzuyuCore {
	protected static final Logger<?> LOGGER = Logger.getDefaultLogger();
	protected IApplicationContext appContext;
	protected ApplicationData appData;
	protected MutanBug mutanbug;
	
	public TzuyuCore(IApplicationContext appContext, ApplicationData appData) {
		this.appContext = appContext;
		this.appData = appData;
	}

	public FaultLocalizationReport faultLocalization(List<String> testingClassNames,
			List<String> junitClassNames) throws Exception {
		return faultLocalization(testingClassNames, junitClassNames, true);
	}
	
	public FaultLocalizationReport faultLocalization(List<String> testingClassNames,
			List<String> junitClassNames, boolean useSlicer) throws Exception {
		FaultLocalization analyzer = new FaultLocalization(appContext);
		analyzer.setUseSlicer(useSlicer);
		FaultLocalizationReport report = analyzer.analyse(testingClassNames, junitClassNames,
				appData.getSuspiciousCalculAlgo());
//		mutation(report, junitClassNames);
		return report;
	}
	
	public FaultLocalizationReport faultLocalization2(
			List<String> testingClassNames, List<String> testingPackages,
			List<String> junitClassNames, boolean useSlicer) throws Exception {
		FaultLocalization analyzer = new FaultLocalization(appContext);
		analyzer.setUseSlicer(useSlicer);
		FaultLocalizationReport report = analyzer.analyseSlicingFirst(
				testingClassNames, testingPackages, junitClassNames,
				appData.getSuspiciousCalculAlgo());
		
//		mutation(report, junitClassNames);
		return report;
	}

	public void faultLocate(FaultLocateParams params)
			throws Exception {
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
		timer.logResults(LOGGER);
	}

	protected FaultLocalizationReport computeSuspiciousness(FaultLocateParams params) throws Exception {
		LOGGER.info("Running " + appData.getSuspiciousCalculAlgo());
		
		final FaultLocalization analyzer = new FaultLocalization(appContext);
		analyzer.setUseSlicer(params.isSlicerEnable());

		FaultLocalizationReport report;
		if (!params.isSlicerEnable() || params.getTestingPkgs().isEmpty()) {
			report = analyzer.analyse(params.getTestingClassNames(), params.getJunitClassNames(),
					appData.getSuspiciousCalculAlgo());
		} else {
			report = analyzer.analyseSlicingFirst(params.getTestingClassNames(), params.getTestingPkgs(),
					params.getJunitClassNames(), appData.getSuspiciousCalculAlgo());
		}
		LOGGER.info(report);
		return report;
	}

	private void mutation(FaultLocalizationReport report,
			List<String> junitClassNames, int rankToExamine) throws Exception {
		LOGGER.info("Running Mutation");
		MutanBug mutanbug = new MutanBug();
		mutanbug.setAppData(appData);
		mutanbug.setMutator(appContext.getMutator());
		mutanbug.mutateAndRunTests(report, rankToExamine, junitClassNames);
		LOGGER.info(report);
	}
	
	protected void machineLearning(FaultLocalizationReport report,
			FaultLocateParams params) throws ClassNotFoundException,
			SavException, IcsetlvException, Exception {
		LOGGER.info("Running Machine Learning");
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
			LOGGER.warn("No suspect line to learn. SVM will not run.");
		} else {
			filter(suspectLocations, appData.getAppSrc());
			if (LOGGER.isDebug()) {
				LOGGER.debug("before grouping: ");
				LOGGER.debug(StringUtils.join(suspectLocations, "\n"));
			}
			// Select from suspectLocations to monitor
			List<LineCoverageInfo> selectedLocations = suspectLocations;
			if (params.isGroupLines()) {
				selectedLocations = selectLinesByGrouping(suspectLocations);
			}

			/* compute variables appearing at each breakpoint */
			VariableNameCollector nameCollector = new VariableNameCollector(
					params.getVarNameCollectionMode(), appData.getAppSrc());
			LocatedLines locatedLines = new LocatedLines(selectedLocations);
			nameCollector.updateVariables(locatedLines.getLocatedLines());
			/*
			 * add new debug line if needed in order to collect data of
			 * variables at a certain line after that line is executed
			 */
			List<DebugLine> debugLines = getDebugLines(locatedLines.getLocatedLines());
			DebugLinePreProcessor preProcessor = new DebugLinePreProcessor();
			debugLines = preProcessor.preProcess(debugLines);
			if (LOGGER.isDebug()) {
				LOGGER.debug("after grouping & processing: ");
				LOGGER.debug(StringUtils.join(debugLines, "\n"));
			}
			LearnInvariants learnInvariant = new LearnInvariants(appData.getAppClassPath(), params);
			List<BkpInvariantResult> invariants = learnInvariant.learn(new ArrayList<BreakPoint>(debugLines), 
										junitClassNames, appData.getAppSrc());
			
			locatedLines.updateInvariantResult(invariants);
			
			LOGGER.info("----------------FINISHED--------------------");
			LOGGER.info(locatedLines.getDisplayResult());
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
				appData.getAppSrc());
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
		
		Recompiler recompiler = new Recompiler(appData.initVmConfig());
		recompiler.recompileJFile(appData.getAppTestTarget(), generatedFiles);
		
		return junitClassNames;
	}

	protected List<DebugLine> getDebugLines(List<BreakPoint> locatedLines) throws SavException {
		mutanbug = getMutanbug();
		mutanbug.setAppData(appData);
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
	
	public void genAssertion(AssertionGenerationParams params) throws Exception {
		
	}
}
