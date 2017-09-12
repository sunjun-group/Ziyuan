/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.CfgJaCoCo;
import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.utils.CfgJaCoCoUtils;
import gentest.junit.TestsPrinter.PrintOption;
import learntest.core.LearntestParamsUtils.GenTestPackage;
import learntest.core.commons.data.LearnTestApproach;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.core.gentest.GentestParams;
import learntest.core.gentest.GentestResult;
import learntest.core.gentest.TestGenerator;
import learntest.core.machinelearning.IInputLearner;
import learntest.core.machinelearning.PrecondDecisionLearner;
import learntest.core.machinelearning.RandomLearner;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.vm.JavaCompiler;
import sav.strategies.vm.VMConfiguration;

/**
 * @author LLT
 * this class holds all shared service and main data for learning process. 
 * The services should not care about how to run each other, and let the mediator take in to account that 
 * job.
 * 
 */
public class LearningMediator {
	private static final Logger log = LoggerFactory.getLogger(LearningMediator.class);
	/* services */
	private TestGenerator testGenerator;
	private JavaCompiler javaCompiler;
	private CfgJaCoCo cfgCoverageTool;
	
	/* share utils and project configuration */
	private TargetMethod targetMethod;
	private AppJavaClassPath appClassPath;
	private LearnTestParams learntestParams;
	
	public LearningMediator(AppJavaClassPath appClassPath, LearnTestParams params) {
		this.appClassPath = appClassPath;
		this.targetMethod = params.getTargetMethod();
		this.learntestParams = params;
	}

	public TestGenerator getTestGenerator() {
		if (testGenerator == null) {
			testGenerator = new TestGenerator(appClassPath);
		}
		return testGenerator;
	}

	public JavaCompiler getJavaCompiler() {
		if (javaCompiler == null) {
			javaCompiler = new JavaCompiler(new VMConfiguration(appClassPath));
		}
		return javaCompiler;
	}
	
	public void compile(List<File> junitFiles) throws SavException {
		getJavaCompiler().compile(getAppClassPath().getTestTarget(), junitFiles);
	}

	public TargetMethod getTargetMethod() {
		return targetMethod;
	}

	public AppJavaClassPath getAppClassPath() {
		return appClassPath;
	}

	public CfgJaCoCo getCfgCoverageTool() {
		if (cfgCoverageTool == null) {
			cfgCoverageTool = new CfgJaCoCo(appClassPath);
		}
		cfgCoverageTool.reset();
		return cfgCoverageTool;
	}
	
	public IInputLearner initDecisionLearner(LearnTestParams params) {
		String methodName = params.getTargetMethod().getMethodFullName()+"."+params.getTargetMethod().getLineNum();
		long time = System.currentTimeMillis();
		if (params.getApproach() == LearnTestApproach.L2T) {
			return new PrecondDecisionLearner(this, "./log/"+methodName+".l2t."+time+".log");
		} else {
			return new RandomLearner(this, params.getMaxTcs(), "./log/"+methodName+".randoop."+time+".log");
		}
	}

	public void runCoverageForGeneratedTests(Map<String, CfgCoverage> coverageMap, List<String> junitClassNames)
			throws SavException, IOException, ClassNotFoundException {
		TargetMethod targetMethod = getTargetMethod();
		String methodId = CfgJaCoCoUtils.createMethodId(targetMethod.getClassName(), targetMethod.getMethodName(),
				targetMethod.getMethodSignature());
		List<String> targetMethods = CollectionUtils.listOf(methodId);
		CfgJaCoCo cfgCoverageTool = getCfgCoverageTool();
		cfgCoverageTool .reset();
		cfgCoverageTool.setCfgCoverageMap(coverageMap);
		cfgCoverageTool.runBySimpleRunner(targetMethods, Arrays.asList(targetMethod.getClassName()),
				junitClassNames);
	}
	
	public GentestResult genTestAndCompile(List<double[]> solutions, List<ExecVar> vars, PrintOption printOption)
			throws SavException {
		log.debug("gentest..");
		GentestResult result = genTestAccordingToSolutions(solutions, vars, printOption);
		if (!result.isEmpty()) {
			log.debug("compile..");
			compile(result.getJunitfiles());
		}
		return result;
	}

	public GentestResult genTestAccordingToSolutions(List<double[]> solutions, List<ExecVar> vars,
			PrintOption printOption) throws SavException {
		GentestParams params = LearntestParamsUtils.createGentestParams(appClassPath, learntestParams, GenTestPackage.RESULT);
		params.setPrintOption(printOption);
		return getTestGenerator().genTestAccordingToSolutions(params, solutions, vars);
	}
	
}
