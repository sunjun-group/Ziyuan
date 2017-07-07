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

import cfgcoverage.jacoco.CfgJaCoCo;
import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.utils.CfgJaCoCoUtils;
import learntest.core.commons.data.LearnTestApproach;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.core.machinelearning.RandomLearner;
import learntest.core.machinelearning.IInputLearner;
import learntest.core.machinelearning.PrecondDecisionLearner;
import learntest.main.LearnTestParams;
import learntest.main.TestGenerator;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.AppJavaClassPath;
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
	/* services */
	private TestGenerator testGenerator;
	private JavaCompiler javaCompiler;
	private CfgJaCoCo cfgCoverageTool;
	
	/* share utils and project configuration */
	private TargetMethod targetMethod;
	private AppJavaClassPath appClassPath;
	
	public LearningMediator(AppJavaClassPath appClassPath, TargetMethod targetMethod) {
		this.appClassPath = appClassPath;
		this.targetMethod = targetMethod;
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
		if (params.getApproach() == LearnTestApproach.L2T) {
			return new PrecondDecisionLearner(this);
		} else {
			return new RandomLearner(this, params.getMaxTcs());
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
	
	
}
