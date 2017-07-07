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
import learntest.core.gentest.TestGenerator;
import learntest.main.LearnTestParams;
import learntest.main.RunTimeInfo;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.FileUtils;
import sav.common.core.utils.SingleTimer;
import sav.common.core.utils.TextFormatUtils;
import sav.settings.SAVExecutionTimeOutException;
import sav.settings.SAVTimer;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.vm.JavaCompiler;
import sav.strategies.vm.VMConfiguration;

/**
 * @author LLT
 *
 */
public abstract class AbstractLearntest implements ILearnTestSolution {
	private Logger log = LoggerFactory.getLogger(AbstractLearntest.class);
	protected AppJavaClassPath appClasspath;
	protected TestGenerator testGenerator;
	protected JavaCompiler compiler;
	private TestcasesExecutor testcaseExecutor;
	
	public AbstractLearntest(AppJavaClassPath appClasspath) {
		this.appClasspath = appClasspath;
	}
	
	private GentestResult randomGentests(GentestParams params) {
		ensureTestGenerator();
		ensureCompiler();
		try {
			GentestResult result = testGenerator.genTest(params);
			compiler.compile(appClasspath.getTestTarget(), result.getAllFiles());
			return result;
		} catch (Exception e) {
			log.debug(e.getMessage());
			return GentestResult.getEmptyResult();
		}
	}
	
	/**
	 * run coverage, and in case the coverage is too bad (means no branch is covered)
	 * try to generate another testcase.
	 */
	protected GentestResult randomGenerateInitTestWithBestEffort(LearnTestParams params, GentestParams gentestParams) {
		TargetMethod targetMethod = params.getTargetMethod();
		CfgCoverage cfgCoverage = null;
		gentestParams.setPrintOption(PrintOption.APPEND);
		
		double bestCvg = -1;
		GentestResult gentestResult = null;
		int i = 0;
		for (i = 0; i <= 3; i++) {
			try {
				gentestResult = randomGentests(gentestParams);
				cfgCoverage = runCfgCoverage(targetMethod, gentestResult.getJunitClassNames());
				double cvg = CoverageUtils.calculateCoverage(cfgCoverage);
				/* replace current init test with new generated test */
				if (cvg > bestCvg) {
					bestCvg = cvg;
					params.setInitialTests(new JunitTestsInfo(gentestResult, appClasspath.getClassLoader()));
				} else if (gentestResult != null) {
					// remove files
					FileUtils.deleteFiles(gentestResult.getAllFiles());
				}
				if (i >= 3 || !CoverageUtils.noDecisionNodeIsCovered(cfgCoverage)) {
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

	protected GentestResult genterateTestFromSolutions(List<ExecVar> vars, List<double[]> solutions)
			throws SavException {
		return genterateTestFromSolutions(vars, solutions, true);
	}
	
	protected GentestResult genterateTestFromSolutions(List<ExecVar> vars, List<double[]> solutions, boolean override)
			throws SavException {
		try {
			ensureCompiler();
			PrintOption printOption = override ? PrintOption.OVERRIDE : PrintOption.APPEND;
			GentestResult gentestResult = new learntest.main.TestGenerator(appClasspath)
					.genTestAccordingToSolutions(solutions, vars, printOption);
			compiler.compile(appClasspath.getTestTarget(), gentestResult.getJunitfiles());
			return gentestResult;
		} catch (Exception e) {
			e.printStackTrace();
			return GentestResult.getEmptyResult();
		}
	}
	
	protected CfgCoverage runCfgCoverage(TargetMethod targetMethod, List<String> junitClasses) throws SavException {
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
		log.debug("coverage: {}", CoverageUtils.calculateCoverage(cfgCoverage));
		return cfgCoverage;
	}
	
	protected RunTimeInfo getRuntimeInfo(CfgCoverage cfgCoverage) {
		double coverage = CoverageUtils.calculateCoverage(cfgCoverage);
		log.debug("coverage: {}", coverage);
		for (CfgNode node : cfgCoverage.getCfg().getDecisionNodes()) {
			StringBuilder sb = new StringBuilder();
			NodeCoverage nodeCvg = cfgCoverage.getCoverage(node);
			Set<BranchRelationship> coveredBranches = new HashSet<BranchRelationship>(2);
			for (int branchIdx : nodeCvg.getCoveredBranches()) {
				BranchRelationship branchRelationship = node.getBranchRelationship(branchIdx);
				coveredBranches.add(branchRelationship == BranchRelationship.TRUE ? branchRelationship : 
										BranchRelationship.FALSE);
			}
			sb.append("NodeCoverage [").append(node).append(", coveredTcs=").append(nodeCvg.getCoveredTcsTotal())
						.append(", coveredBranches=").append(nodeCvg.getCoveredBranches().size()).append(", ")
						.append(coveredBranches).append("]");
			log.debug(sb.toString());
		}
		return new RunTimeInfo(SAVTimer.getExecutionTime(), coverage, cfgCoverage.getTotalTcs());
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
	
	protected void ensureCompiler() {
		if (compiler == null) {
			compiler = new JavaCompiler(new VMConfiguration(appClasspath));
		}
	}
	
	protected void ensureTestGenerator() {
		if (testGenerator == null) {
			testGenerator = new TestGenerator(appClasspath);
		}
	}
}
