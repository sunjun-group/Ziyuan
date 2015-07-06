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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import main.FaultLocalization;

import org.apache.commons.collections.CollectionUtils;

import sav.common.core.Logger;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.utils.BreakpointUtils;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.StringUtils;
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
		if (params.isMutationEnable()) {
			mutation(report, params.getJunitClassNames(), params.getRankToExamine());
		}
		if (params.isMachineLearningEnable()) {
			machineLearning(report, params);
		}
	}

	private FaultLocalizationReport computeSuspiciousness(FaultLocateParams params) throws Exception {
		LOGGER.info("Running " + appData.getSuspiciousCalculAlgo());
		
		final FaultLocalization analyzer = new FaultLocalization(appContext);
		analyzer.setUseSlicer(params.isSlicerEnable());

		FaultLocalizationReport report;
		if (!params.isSlicerEnable()) {
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
		
		List<LineCoverageInfo> suspectLocations = report.getFirstRanks(params.getRankToExamine());
		
		if (CollectionUtils.isEmpty(suspectLocations)) {
			LOGGER.warn("No suspect line to learn. SVM will not run.");
		} else {
			filter(suspectLocations, appData.getAppSrc());
			Map<BreakPoint, Double> mapBkpToSuspeciousness = new HashMap<BreakPoint, Double>();
			List<BreakPoint> breakpoints = new ArrayList<BreakPoint>();
			for (LineCoverageInfo lineInfo : suspectLocations) {
				BreakPoint bkp = BreakpointUtils.toBreakPoint(lineInfo.getLocation());
				breakpoints.add(bkp);
				mapBkpToSuspeciousness.put(bkp, lineInfo.getSuspiciousness());
			}
			
			//compute variables appearing in each breakpoint
			VariableNameCollector nameCollector = new VariableNameCollector(
															params.getVarNameCollectionMode(),
															appData.getAppSrc());
			nameCollector.updateVariables(breakpoints);
			
			
			MutanBug mutanbug = new MutanBug();
			Map<BreakPoint, BreakPoint> mapNewToOldBkp = getNextLineToAddBreakpoint(mutanbug, BreakpointUtils.initBrkpsMap(breakpoints));
			
			List<BreakPoint> newBreakpoints = new ArrayList<BreakPoint>(mapNewToOldBkp.keySet());
			
			LearnInvariants learnInvariant = new LearnInvariants(appData.getVmConfig(), params);
			List<Result> invariants = learnInvariant.learn(newBreakpoints, junitClassNames, appData.getAppSrc());
			
			List<BugLocalizationLine> bugLines = new ArrayList<BugLocalizationLine>();
			
			for(Result invariant: invariants){
				if (!(invariant instanceof AllPositiveResult) && invariant.getAccuracy() > 0){
					BreakPoint oldBreakpoint = mapNewToOldBkp.get(invariant.getBreakPoint());
					double suspeciousness = mapBkpToSuspeciousness.get(oldBreakpoint);
					
					BugLocalizationLine bugLine = new BugLocalizationLine( oldBreakpoint, suspeciousness, invariant);
					bugLines.add(bugLine);
				}
			}
			Collections.sort(bugLines, new Comparator<BugLocalizationLine>() {

				@Override
				public int compare(BugLocalizationLine o1,
						BugLocalizationLine o2) {
					return Double.compare(o2.getSuspiciousness(), o1.getSuspiciousness());
				}
			});
			LOGGER.info("----------------FINISHED--------------------");
			LOGGER.info(StringUtils.join(bugLines, "\n\n"));
			mutanbug.restoreFiles();
		}
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

	private Map<BreakPoint, BreakPoint> getNextLineToAddBreakpoint(MutanBug mutanbug, 
			Map<String, List<BreakPoint>> classLocationMap) throws SavException {
		mutanbug.setAppData(appData);
		mutanbug.setMutator(appContext.getMutator());
		Map<String, DebugLineInsertionResult> mutationInfo = mutanbug
				.mutateForMachineLearning(classLocationMap);
		Map<BreakPoint, BreakPoint> newLocations = getNewLocationAfterMutation(classLocationMap, mutationInfo);
		return newLocations;
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
	
	private Map<BreakPoint, BreakPoint> getNewLocationAfterMutation(Map<String, List<BreakPoint>> classLocationMap,
			Map<String, DebugLineInsertionResult> mutationInfo) {
		Map<BreakPoint, BreakPoint> result = new HashMap<BreakPoint, BreakPoint>();
		for (String className : classLocationMap.keySet()) {
			DebugLineInsertionResult lineInfo = mutationInfo.get(className);
			Map<Integer, Integer> lineToNextLine = lineInfo.getOldNewLocMap();
			List<BreakPoint> bkpsInClass = classLocationMap.get(className);
			for(BreakPoint location: bkpsInClass){
				Integer newLineNo = lineToNextLine.get(location.getLineNo());
				MutationBreakPoint newBreakPoint = new MutationBreakPoint(location, newLineNo);
				result.put(newBreakPoint, location);
			}
		}
		return result;
	}

}
