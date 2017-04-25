/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco;

import java.io.IOException;
import java.util.List;

import codecoverage.jacoco.agent.IExecutionReporter;
import codecoverage.jacoco.agent.JaCoCo;
import sav.common.core.SavException;
import sav.strategies.dto.AppJavaClassPath;

/**
 * @author LLT
 *
 */
public class CfgJaCoCo {
	private AppJavaClassPath appClasspath;
	private JaCoCo jacoco;
	
	public CfgJaCoCo(AppJavaClassPath appClasspath) {
		this.appClasspath = appClasspath;
		jacoco = new JaCoCo(appClasspath);
	}
	
	public void run(List<String> testingClassNames,
			List<String> junitClassNames) throws SavException, IOException,
			ClassNotFoundException {
		IExecutionReporter reporter = new ExecutionReporter(new String[] {
				appClasspath.getTarget(), appClasspath.getTestTarget() });
		jacoco.run(reporter, testingClassNames, junitClassNames);
	}
}
