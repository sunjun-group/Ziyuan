/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.CfgJaCoCo;
import cfgcoverage.jacoco.analysis.data.BranchRelationship;
import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.analysis.data.CfgNode;
import cfgcoverage.jacoco.analysis.data.NodeCoverage;
import cfgcoverage.jacoco.utils.CfgJaCoCoUtils;
import gentest.junit.TestsPrinter.PrintOption;
import icsetlv.DefaultValues;
import icsetlv.common.dto.BreakpointData;
import icsetlv.variable.TestcasesExecutor;
import learntest.core.commons.data.classinfo.JunitTestsInfo;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.core.commons.utils.CoverageUtils;
import learntest.core.gentest.GentestParams;
import learntest.core.gentest.GentestResult;
import sav.common.core.SavException;
import sav.common.core.SavRtException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.FileUtils;
import sav.common.core.utils.SingleTimer;
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
		
		StringBuffer coverageInfoBuf = new StringBuffer();
		for (CfgNode node : cfgCoverage.getCfg().getDecisionNodes()) {
			StringBuilder sb = new StringBuilder();
			NodeCoverage nodeCvg = cfgCoverage.getCoverage(node);
			Set<BranchRelationship> coveredBranches = new HashSet<BranchRelationship>(2);
			for (int branchIdx : nodeCvg.getCoveredBranches()) {
				BranchRelationship branchRelationship = node.getBranchRelationship(branchIdx);
				coveredBranches.add(branchRelationship == BranchRelationship.TRUE ? branchRelationship : 
										BranchRelationship.FALSE);
			}
			sb.append("NodeCoverage [").append(node).append(", covered=").append(nodeCvg.isCovered())
						.append(", coveredBranches=").append(nodeCvg.getCoveredBranches().size()).append(", ")
						.append(coveredBranches).append("]");
			String sbStr = sb.toString();
			log.debug(sbStr);
			
			coverageInfoBuf.append(sbStr + "\n");
		}
		
		if (!test) {
			return new RunTimeInfo(executionTime, coverage, testCnt, coverageInfoBuf.toString());
		}
		
		return new TestRunTimeInfo(executionTime, coverage, testCnt, coverageInfoBuf.toString());
	}
	
	protected BreakpointData executeTestcaseAndGetTestInput(List<String> testcases, BreakPoint methodEntryBkp)
			throws SavException, SAVExecutionTimeOutException {
		ensureTestcaseExecutor();
		testcaseExecutor.setup(appClasspath, testcases);
		testcaseExecutor.run(CollectionUtils.listOf(methodEntryBkp, 1));
		BreakpointData result = CollectionUtils.getFirstElement(testcaseExecutor.getResult());
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
