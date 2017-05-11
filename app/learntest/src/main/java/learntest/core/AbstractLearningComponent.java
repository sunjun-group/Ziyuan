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

/**
 * @author LLT
 * 
 */
public abstract class AbstractLearningComponent {
	protected LearningMediator mediator;
	
	public AbstractLearningComponent(LearningMediator mediator) {
		this.mediator = mediator;
	}
	
	protected TestGenerator getTestGenerator() {
		return mediator.getTestGenerator();
	}

	public JavaCompiler getJavaCompiler() {
		return mediator.getJavaCompiler();
	}

	public TargetMethod getTargetMethod() {
		return mediator.getTargetMethod();
	}

	public AppJavaClassPath getAppClassPath() {
		return mediator.getAppClassPath();
	}

	public StopTimer getTimer() {
		return mediator.getTimer();
	}

	public CfgJaCoCo getCfgCoverage() {
		return mediator.getCfgCoverage();
	}
	
}
