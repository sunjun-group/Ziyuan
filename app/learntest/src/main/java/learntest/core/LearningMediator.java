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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.CfgJaCoCo;
import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.utils.CfgJaCoCoUtils;
import gentest.junit.JWriter;
import gentest.junit.MWriter;
import gentest.junit.TestsPrinter.PrintOption;
import learntest.core.LearntestParamsUtils.GenTestPackage;
import learntest.core.commons.data.LearnTestApproach;
import learntest.core.commons.data.LineCoverageResult;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.core.gan.GanDecisionLearner;
import learntest.core.gentest.GentestParams;
import learntest.core.gentest.GentestResult;
import learntest.core.gentest.TestGenerator;
import learntest.core.machinelearning.IInputLearner;
import learntest.core.machinelearning.PrecondDecisionLearner;
import learntest.core.machinelearning.RandomLearner;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.FileUtils;
import sav.common.core.utils.JavaFileUtils;
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
	private FinalTests finalTests;
	
	public LearningMediator(AppJavaClassPath appClassPath, LearnTestParams params) {
		this.appClassPath = appClassPath;
		this.targetMethod = params.getTargetMethod();
		this.learntestParams = params;
		this.finalTests = new FinalTests();
	}

	private TestGenerator getTestGenerator() {
		if (testGenerator == null) {
			testGenerator = new TestGenerator(appClassPath);
		}
		return testGenerator;
	}

	private JavaCompiler getJavaCompiler() {
		if (javaCompiler == null) {
			javaCompiler = new JavaCompiler(new VMConfiguration(appClassPath));
		}
		return javaCompiler;
	}
	
	public void compile(List<File> junitFiles) throws SavException {
		getJavaCompiler().compile(getAppClassPath().getTestTarget(), junitFiles);
	}
	
	private void compileAndLogTestSequences(GentestResult result, GentestParams params) throws SavException {
		System.currentTimeMillis();
//		compile(result.getJunitfiles());
//		List<File> main = new LinkedList<>();
//		main.add(result.getMainClassFile());
//		compile(main);
		List<File> needCompiled = new LinkedList<>();
		needCompiled.addAll(result.getJunitfiles());
		if (params.generateMainClass()) {
			needCompiled.add(result.getMainClassFile());
		}
		compile(needCompiled);
		finalTests.log(result);
	}

	public TargetMethod getTargetMethod() {
		return targetMethod;
	}

	public AppJavaClassPath getAppClassPath() {
		return appClassPath;
	}

	private CfgJaCoCo getCfgCoverageTool() {
		if (cfgCoverageTool == null) {
			cfgCoverageTool = new CfgJaCoCo(appClassPath);
		}
		cfgCoverageTool.reset();
		return cfgCoverageTool;
	}
	
	public IInputLearner initDecisionLearner(LearnTestParams params) {
		String methodName = params.getTargetMethod().getMethodFullName()+"."+params.getTargetMethod().getLineNum();
		long time = System.currentTimeMillis();
		switch (params.getApproach()) {
		case L2T:
			return new PrecondDecisionLearner(this, "./logs/"+methodName+".l2t."+time+".log");
		case RANDOOP:
			return new RandomLearner(this, params.getMaxTcs(), "./logs/"+methodName+".randoop."+time+".log");
		case GAN:
			return new GanDecisionLearner(this);
		}
		return null; // this should never happen
	}

	public void runCoverageForGeneratedTests(Map<String, CfgCoverage> coverageMap, List<String> junitClassNames)
			throws SavException, IOException, ClassNotFoundException {
		TargetMethod targetMethod = getTargetMethod();
		String methodId = CfgJaCoCoUtils.createMethodId(targetMethod.getClassName(), targetMethod.getMethodName(),
				targetMethod.getMethodSignature());
		List<String> targetMethods = CollectionUtils.listOf(methodId);
		CfgJaCoCo cfgCoverageTool = getCfgCoverageTool();
		cfgCoverageTool.reset();
		cfgCoverageTool.setCfgCoverageMap(coverageMap);
		cfgCoverageTool.runBySimpleRunner(targetMethods, Arrays.asList(targetMethod.getClassName()),
				junitClassNames);
		finalTests.filterByCoverageResult(coverageMap);
	}
	
	public GentestResult genTestAndCompile(List<double[]> solutions, List<ExecVar> vars, PrintOption printOption)
			throws SavException {
		GentestParams params = LearntestParamsUtils.createGentestParams(appClassPath, learntestParams,
				GenTestPackage.RESULT);
		params.setPrintOption(printOption);
		return gentestAndCompile(solutions, vars, params);
	}

	public GentestResult gentestAndCompile(List<double[]> solutions, List<ExecVar> vars, GentestParams params)
			throws SavException {
		log.debug("gentest..");
		GentestResult result = getTestGenerator().genTestAccordingToSolutions(params, solutions, vars, new JWriter());
		if (!result.isEmpty()) {
			log.debug("compile..");
			compileAndLogTestSequences(result, params);
		}
		return result;
	}
	
	public GentestResult randomGentestAndCompile(GentestParams params) {
		try {
			GentestResult result = getTestGenerator().genTest(params);
			compileAndLogTestSequences(result, params);
			return result;
		} catch (Exception e) {
			log.warn("Cannot generate testcase: [{}] {}", e, e.getMessage());
			return GentestResult.getEmptyResult();
		}		
	}

	public GentestResult genTestAccordingToSolutions(List<double[]> solutions, List<ExecVar> vars,
			PrintOption printOption) throws SavException {
		GentestParams params = LearntestParamsUtils.createGentestParams(appClassPath, learntestParams, GenTestPackage.RESULT);
		params.setPrintOption(printOption);
		return getTestGenerator().genTestAccordingToSolutions(params, solutions, vars, new JWriter());
	}
	
	public GentestResult genMainAndCompile(List<double[]> solutions, List<ExecVar> vars, PrintOption printOption)
			throws SavException {
		log.debug("gentest..");
		GentestResult result = genMainAccordingToSolutions(solutions, vars, printOption);
		if (!result.isEmpty()) {
			log.debug("compile..");
			compile(result.getJunitfiles());
		}
		return result;
	}

	public GentestResult genMainAccordingToSolutions(List<double[]> solutions, List<ExecVar> vars,
			PrintOption printOption) throws SavException {
		GentestParams params = LearntestParamsUtils.createGentestParams(appClassPath, learntestParams, GenTestPackage.MAIN);
		params.setPrintOption(printOption);
		params.setTestMethodPrefix("main");
		return getTestGenerator().genTestAccordingToSolutions(params, solutions, vars, new MWriter());
	}

	public LearnTestParams getLearntestParams() {
		return learntestParams;
	}
	
	
	public LineCoverageResult commitFinalTests(CfgCoverage cfgCoverage, TargetMethod targetMethod) {
		/* delete init & result folder */
		FileUtils.deleteAllFiles(JavaFileUtils.getClassFolder(appClassPath.getTestSrc(), 
				learntestParams.getTestPackage(GenTestPackage.INIT)));
		FileUtils.deleteAllFiles(JavaFileUtils.getClassFolder(appClassPath.getTestSrc(),
				learntestParams.getTestPackage(GenTestPackage.RESULT)));
		/* generate new test files from reduction tests */
		GentestParams params = LearntestParamsUtils.createGentestParams(appClassPath, learntestParams,
				GenTestPackage.RESULT);
		params.setPrintOption(PrintOption.OVERRIDE);
		List<File> junitFiles = finalTests.commit(params.getPrinterParams(), cfgCoverage, targetMethod);
		try {
			compile(junitFiles);
			return finalTests.getLineCoverageResult();
		} catch (SavException e) {
			log.error("Error when Compiling final tests: {}, {}", e.getMessage(), e);
			return new LineCoverageResult(targetMethod.getMethodInfo());
		}
	}

}
