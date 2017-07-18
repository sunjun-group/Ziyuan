/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core;

import cfgcoverage.jacoco.CfgJaCoCo;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.core.gentest.TestGenerator;
import sav.strategies.dto.AppJavaClassPath;

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

	public TargetMethod getTargetMethod() {
		return mediator.getTargetMethod();
	}

	public AppJavaClassPath getAppClassPath() {
		return mediator.getAppClassPath();
	}

	public CfgJaCoCo getCfgCoverage() {
		return mediator.getCfgCoverageTool();
	}
	
}
