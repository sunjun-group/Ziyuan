/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core;

import cfgcoverage.jacoco.CfgJaCoCo;
import learntest.core.commons.data.testtarget.TargetMethod;
import learntest.main.TestGenerator;
import sav.common.core.utils.StopTimer;
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
	private CfgJaCoCo cfgCoverage;
	
	
	/* share utils and project configuration */
	private TargetMethod targetMethod;
	private AppJavaClassPath appClassPath;
	private StopTimer timer;
	
	public LearningMediator(AppJavaClassPath appClassPath, TargetMethod targetMethod, StopTimer timer) {
		this.appClassPath = appClassPath;
		this.targetMethod = targetMethod;
		this.timer = timer;
	}

	public TestGenerator getTestGenerator() {
		if (testGenerator == null) {
			testGenerator = new TestGenerator();
		}
		return testGenerator;
	}

	public JavaCompiler getJavaCompiler() {
		if (javaCompiler == null) {
			javaCompiler = new JavaCompiler(new VMConfiguration(appClassPath));
		}
		return javaCompiler;
	}

	public TargetMethod getTargetMethod() {
		return targetMethod;
	}

	public AppJavaClassPath getAppClassPath() {
		return appClassPath;
	}

	public StopTimer getTimer() {
		return timer;
	}
	
	public CfgJaCoCo getCfgCoverage() {
		if (cfgCoverage == null) {
			cfgCoverage = new CfgJaCoCo(appClassPath);
		}
		cfgCoverage.reset();
		return cfgCoverage;
	}
}
