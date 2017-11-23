/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.CfgJaCoCo;
import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.analysis.data.DecisionBranchType;
import cfgcoverage.jacoco.utils.CfgJaCoCoUtils;
import gentest.junit.TestsPrinter.PrintOption;
import icsetlv.DefaultValues;
import icsetlv.common.dto.BreakpointValue;
import icsetlv.variable.TestcasesExecutor;
import learntest.core.commons.data.classinfo.JunitTestsInfo;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.exception.LearnTestException;
import learntest.core.commons.utils.CoverageUtils;
import learntest.core.gentest.GentestParams;
import learntest.core.gentest.GentestResult;
import sav.common.core.SavException;
import sav.common.core.SavRtException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.FileUtils;
import sav.common.core.utils.SingleTimer;
import sav.common.core.utils.StringUtils;
import sav.common.core.utils.TextFormatUtils;
import sav.settings.SAVExecutionTimeOutException;
import sav.settings.SAVTimer;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;

/**
 * @author LLT
 *
 */
public abstract class AbstractLearntest implements ILearnTestSolution {
	private Logger log = LoggerFactory.getLogger(AbstractLearntest.class);
	protected LearningMediator mediator;
	protected AppJavaClassPath appClasspath;
	private TestcasesExecutor testcaseExecutor;
	
	public AbstractLearntest(AppJavaClassPath appClasspath) {
		this.appClasspath = appClasspath;
	}
	
	public GentestResult randomGentests(GentestParams params) {
		return getMediator().randomGentestAndCompile(params);
	}
	
	/**
	 * run coverage, and in case the coverage is too bad (means no branch is covered)
	 * try to generate another testcase.
	 */
	public GentestResult randomGenerateInitTestWithBestEffort(LearnTestParams params, GentestParams gentestParams) {
		TargetMethod targetMethod = params.getTargetMethod();
		CfgCoverage cfgCoverage = null;
		gentestParams.setPrintOption(PrintOption.APPEND);
		
		double bestCvg = -1;
		GentestResult gentestResult = null;
		int maxTry = 3;
		int i = 0;
		for (; i < maxTry; i++) {
			if (i > 0) {
				log.info("Try again for a better initial coverage..");
			}
			try {
				gentestResult = randomGentests(gentestParams);
				cfgCoverage = runCfgCoverage(targetMethod, gentestResult.getJunitClassNames());
				double cvg = CoverageUtils.calculateCoverage(cfgCoverage);
				if (cvg > 0) {
					log.info("Coverage: {}, branchCoverage: {}", cvg, CoverageUtils.calculateCoverageByBranch(cfgCoverage));
				} else {
					log.info("Coverage: {}", cvg);
				}
				/* replace current init test with new generated test */
				if (cvg > bestCvg) {
					bestCvg = cvg;
					params.setInitialTests(new JunitTestsInfo(gentestResult, appClasspath.getClassLoader()));
					params.setGeneratedInitTest(new JunitTestsInfo(gentestResult, appClasspath.getClassLoader()));
				} else if (gentestResult != null) {
					// remove files
					FileUtils.deleteFiles(gentestResult.getAllFiles());
				}
				if (i > maxTry || !CoverageUtils.noDecisionNodeIsCovered(cfgCoverage)) {
					break;
				}
			} catch (Exception e) {
				// ignore
				log.debug(e.getMessage());
				continue;
			}
		}
		// end of trying
		if (i > 0) {
			log.debug(String.format("Get best initial coverage after trying to regenerate test %d times", i));
		}
		return gentestResult;
	}

	public CfgCoverage runCfgCoverage(TargetMethod targetMethod, List<String> junitClasses) throws SavException {
		log.debug("calculate coverage..");
		SingleTimer timer = SingleTimer.start("cfg-coverage");
		CfgJaCoCo jacoco = new CfgJaCoCo(appClasspath);
		String methodId = CfgJaCoCoUtils.createMethodId(targetMethod.getClassName(), targetMethod.getMethodName(),
				targetMethod.getMethodSignature());
		List<String> targetMethods = CollectionUtils.listOf(methodId);
		Map<String, CfgCoverage> coverageMap = jacoco.runJunit(targetMethods, Arrays.asList(targetMethod.getClassName()),
				junitClasses);
		CfgCoverage cfgCoverage = coverageMap.get(methodId);
		if (cfgCoverage == null) {
			log.debug("Cannot get cfgCoverage from result map!");
			log.debug("coverageMap={}", TextFormatUtils.printMap(coverageMap));
		}else {
			List<String> lines = CoverageUtils.getBranchCoverageDisplayTexts(cfgCoverage, -1);
			for (String line : lines) {
				log.debug(line);
			}
		}
		targetMethod.updateCfgIfNotExist(cfgCoverage.getCfg());
		timer.logResults(log);
		log.debug("coverage: {}", CoverageUtils.calculateCoverageByBranch(cfgCoverage));
		return cfgCoverage;
	}
	
	protected RunTimeInfo getRuntimeInfo(CfgCoverage cfgCoverage, boolean test) {
		double coverage = CoverageUtils.calculateCoverageByBranch(cfgCoverage);
		log.debug("coverage: {}", coverage);
		long executionTime = SAVTimer.getExecutionTime();
		int testCnt = cfgCoverage.getTotalTcs();
		
		HashMap<String , Set<DecisionBranchType>> relationships = CoverageUtils.getBranchCoverage(cfgCoverage);
		
		List<String> lines = CoverageUtils.getBranchCoverageDisplayTexts(cfgCoverage, -1);
		for (String line : lines) {
			log.debug(line);
		}
		String cvgInfo = StringUtils.newLineJoin(lines);
		if (!test) {
			RunTimeInfo info = new RunTimeInfo(executionTime, coverage, testCnt, cvgInfo);
			info.setRelationships(relationships);
			return info;
		}

		RunTimeInfo tInfo = new TestRunTimeInfo(executionTime, coverage, testCnt, cvgInfo);
		tInfo.setRelationships(relationships);
		return tInfo;
	}
	
	protected DecisionProbes initProbes(TargetMethod targetMethod, CfgCoverage cfgcoverage, List<BreakpointValue> entryValues)
			throws LearnTestException {
		DecisionProbes probes = new DecisionProbes(targetMethod, cfgcoverage);
		if (CollectionUtils.isEmpty(entryValues)) {
			throw new LearnTestException("cannot get entry value when coverage is still not empty");
		}
		probes.setRunningResult(entryValues);
		return probes;
	}
	
	protected List<BreakpointValue> executeTestcaseAndGetTestInput(List<String> testcases, BreakPoint methodEntryBkp)
			throws SavException, SAVExecutionTimeOutException {
		ensureTestcaseExecutor();
		testcaseExecutor.setup(appClasspath, testcases);
		testcaseExecutor.run(CollectionUtils.listOf(methodEntryBkp, 1));
		List<BreakpointValue> result = new ArrayList<BreakpointValue>(testcases.size());
		Map<Integer, List<BreakpointValue>> bkpValsMap = testcaseExecutor.getBkpValsByTestIdx();
		for (int i = 0; i < testcases.size(); i++) {
			result.add(bkpValsMap.get(i).get(0));
		}
		return result;
	}
	
	protected TestcasesExecutor ensureTestcaseExecutor() {
		if (testcaseExecutor == null) {
			testcaseExecutor = new TestcasesExecutor(DefaultValues.DEBUG_VALUE_RETRIEVE_LEVEL);
		}
		return testcaseExecutor;
	}
	
	public LearningMediator getMediator() {
		if (mediator == null) {
			throw new SavRtException("learning mediator has not been initialized!");
		}
		return mediator;
	}

	public AppJavaClassPath getAppClasspath() {
		return appClasspath;
	}
}
