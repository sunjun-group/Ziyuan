/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import icsetlv.Engine.AllPositiveResult;
import icsetlv.Engine.Result;
import icsetlv.common.exception.IcsetlvException;
import icsetlv.variable.VariableNameCollector;
import japa.parser.ast.CompilationUnit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import main.FaultLocalization;

import org.apache.commons.collections.CollectionUtils;

import sav.common.core.Logger;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.utils.BreakpointUtils;
import sav.common.core.utils.ClassUtils;
import sav.strategies.IApplicationContext;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.MutationBreakPoint;
import sav.strategies.mutanbug.DebugLineInsertionResult;
import tzuyu.core.inject.ApplicationData;
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
	private static final Logger<?> LOGGER = Logger.getDefaultLogger();
	private IApplicationContext appContext;
	private ApplicationData appData;
	
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
		FaultLocalizationReport report = computeSuspiciousness(params);
		if (params.isRunMutation()) {
			mutation(report, params.getJunitClassNames(), params.getRanktoexamine());
		}
		machineLearning(report, params);
	}

	private FaultLocalizationReport computeSuspiciousness(FaultLocateParams params) throws Exception {
		LOGGER.info("Running " + appData.getSuspiciousCalculAlgo());
		
		final FaultLocalization analyzer = new FaultLocalization(appContext);
		analyzer.setUseSlicer(params.isUseSlicer());

		FaultLocalizationReport report;
		if (CollectionUtils.isEmpty(params.getTestingPkgs())) {
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
	
	private void machineLearning(FaultLocalizationReport report,
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
		
		List<LineCoverageInfo> suspectLocations = report.getFirstRanks(params.getRanktoexamine());
		
		List<BreakPoint> breakpoints = new ArrayList<BreakPoint>();
		for(LineCoverageInfo suspectLocation: suspectLocations){
			breakpoints.add(BreakpointUtils.toBreakPoint(suspectLocation.getLocation()));
		}
		
		//compute variables appearing in each breakpoint
		VariableNameCollector nameCollector = new VariableNameCollector(appData.getAppSrc());
		nameCollector.updateVariables(breakpoints);
		MutanBug mutanbug = new MutanBug();
		List<BreakPoint> newBreakpoints = getNextLineToAddBreakpoint(mutanbug, breakpoints);
		
		if (CollectionUtils.isEmpty(suspectLocations)) {
			LOGGER.warn("No suspect line to learn. SVM will not run.");
		} else {
			LearnInvariants learnInvariant = new LearnInvariants(appData.getVmConfig());
			List<Result> invariants = learnInvariant.learn(newBreakpoints, junitClassNames, appData.getAppSrc());
			
			List<BugLocalizationLine> bugLines = new ArrayList<BugLocalizationLine>();
			for (int i = 0; i < invariants.size(); i++) {
				Result invariant = invariants.get(i);
				if (!(invariant instanceof AllPositiveResult) && invariant.getAccuracy() > 0){
					BugLocalizationLine bugLine = new BugLocalizationLine(
							breakpoints.get(i), suspectLocations.get(i)
									.getSuspiciousness(), invariants.get(i));
					bugLines.add(bugLine);
				}
			}
			
			LOGGER.info("----------------FINISHED--------------------");
			LOGGER.info(bugLines);
		}
		mutanbug.restoreFiles();
	}
	
	private List<String> generateNewTests(String testingClassName,
			String methodName, String verificationMethod, int numberOfTestCases)
			throws ClassNotFoundException, SavException {
		Class<?> targetClass = Class.forName(testingClassName);
		
		FixTraceGentestBuilder builder = new FixTraceGentestBuilder(numberOfTestCases );
		
		String methodAlias = "methodName";
		builder.forClass(targetClass).method(methodName, methodAlias)
					.evaluationMethod(Class.forName(testingClassName), verificationMethod,
							methodAlias).paramAutofill();
		Pair<List<Sequence>, List<Sequence>> testcases = builder.generate();
		final FileCompilationUnitPrinter cuPrinter = new FileCompilationUnitPrinter(
				appData.getAppSrc());
		final List<String> junitClassNames = new ArrayList<String>();
		TestsPrinter printer = new TestsPrinter("test", null, "test",
				targetClass.getSimpleName(), new ICompilationUnitPrinter() {
					
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
		Recompiler recompiler = new Recompiler(appData.getVmConfig());
		recompiler.recompileJFile(appData.getAppTestTarget(), generatedFiles);
		
		return junitClassNames;
	}

	private List<BreakPoint> getNextLineToAddBreakpoint(MutanBug mutanbug, 
			List<BreakPoint> suspectLocations) throws SavException {
		mutanbug.setAppData(appData);
		mutanbug.setMutator(appContext.getMutator());
		Map<String, DebugLineInsertionResult> mutationInfo = mutanbug.mutateForMachineLearning(suspectLocations);
		suspectLocations = getNewLocationAfterMutation(suspectLocations, mutationInfo);
		return suspectLocations;
	}
	
	private List<BreakPoint> getNewLocationAfterMutation(
			List<BreakPoint> suspectLocations,
			Map<String, DebugLineInsertionResult> mutationInfo) {
		List<BreakPoint> result = new ArrayList<BreakPoint>(suspectLocations.size());
		DebugLineInsertionResult lineInfo = mutationInfo.get(suspectLocations.get(0).getClassCanonicalName());
		Map<Integer, Integer> lineToNextLine = lineInfo.getOldNewLocMap();
		
		for(BreakPoint location: suspectLocations){
			Integer newLineNo = lineToNextLine.get(location.getLineNo());
			MutationBreakPoint newBreakPoint = new MutationBreakPoint(location, newLineNo);
			result.add(newBreakPoint);
		}
		
		return result;
	}

}
