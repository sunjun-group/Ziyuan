/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jacop.core.Domain;

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
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.core.commons.utils.CoverageUtils;
import learntest.core.gentest.GentestParams;
import learntest.core.gentest.TestGenerator;
import learntest.core.gentest.TestGenerator.GentestResult;
import learntest.main.RunTimeInfo;
import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
import sav.common.core.SavRtException;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StopTimer;
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
	protected AppJavaClassPath appClasspath;
	protected TestGenerator testGenerator;
	protected JavaCompiler compiler;
	private TestcasesExecutor testcaseExecutor;
	
	public AbstractLearntest(AppJavaClassPath appClasspath) {
		this.appClasspath = appClasspath;
	}
	
	protected GentestResult generateTestcases(GentestParams params) throws SavException {
		ensureTestGenerator();
		ensureCompiler();
		try {
			GentestResult result = testGenerator.genTest(params);
			compiler.compile(appClasspath.getTestTarget(), result.getAllFiles());
			return result;
		} catch (Exception e) {
			throw new SavException(ModuleEnum.UNSPECIFIED, e, e.getMessage());
		}
	}
	
	protected GentestResult genterateTestFromSolutions(List<ExecVar> vars, List<Domain[]> solutions)
			throws SavException {
		return genterateTestFromSolutions(vars, solutions, true);
	}
	
	protected GentestResult genterateTestFromSolutions(List<ExecVar> vars, List<Domain[]> solutions, boolean override)
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
	
	protected CfgCoverage runCfgCoverage(TargetMethod targetMethod, List<String> junitClasses)
			throws SavException, IOException, ClassNotFoundException {
		StopTimer timer = new StopTimer("");
		timer.newPoint("start cfgCoverage");
		CfgJaCoCo jacoco = new CfgJaCoCo(appClasspath);
		List<String> targetMethods = CollectionUtils.listOf(ClassUtils.toClassMethodStr(targetMethod.getClassName(),
				targetMethod.getMethodName()));
		Map<String, CfgCoverage> coverageMap = jacoco.runJunit(targetMethods, Arrays.asList(targetMethod.getClassName()),
				junitClasses);
		timer.newPoint("end cfgCoverage");
		CfgCoverage cfgCoverage = coverageMap.get(CfgJaCoCoUtils.createMethodId(targetMethod.getClassName(), targetMethod.getMethodName(),
				targetMethod.getMethodSignature()));
		targetMethod.updateCfgIfNotExist(cfgCoverage.getCfg());
		return cfgCoverage;
	}
	
	protected RunTimeInfo getRuntimeInfo(CfgCoverage cfgCoverage) {
		double coverage = CoverageUtils.calculateCoverage(cfgCoverage);
		System.out.println("coverage: " + coverage);
		for (CfgNode node : cfgCoverage.getCfg().getDecisionNodes()) {
			StringBuilder sb = new StringBuilder();
			NodeCoverage nodeCvg = cfgCoverage.getCoverage(node);
			Set<BranchRelationship> coveredBranches = new HashSet<BranchRelationship>(2);
			for (int branchIdx : nodeCvg.getCoveredBranches().keySet()) {
				BranchRelationship branchRelationship = node.getBranchRelationship(branchIdx);
				coveredBranches.add(branchRelationship == BranchRelationship.TRUE ? branchRelationship : 
										BranchRelationship.FALSE);
			}
			sb.append("NodeCoverage [").append(node).append(", coveredTcs=").append(nodeCvg.getCoveredTcs().size())
						.append(", coveredBranches=").append(nodeCvg.getCoveredBranches().size()).append(", ")
						.append(coveredBranches).append("]");
			System.out.println(sb.toString());
		}
		return new RunTimeInfo(SAVTimer.getExecutionTime(), coverage,
				cfgCoverage.getTestcases().size());
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
