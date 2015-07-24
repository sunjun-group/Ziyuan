/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.machinelearning;

import icsetlv.Engine;
import icsetlv.common.dto.BkpInvariantResult;
import icsetlv.variable.TestcasesExecutor;

import java.util.List;

import sav.common.core.utils.JunitUtils;
import sav.strategies.dto.BreakPoint;
import sav.strategies.vm.VMConfiguration;
import tzuyu.core.main.FaultLocateParams;

/**
 * @author khanh
 * 
 */
public class LearnInvariants {
	private Engine engine;

	public LearnInvariants(final VMConfiguration config, FaultLocateParams params) {
		engine = new Engine().setPort(config.getPort()).setJavaHome(config.getJavaHome());
		engine.setTestcaseExecutor(new TestcasesExecutor(params.getValueRetrieveLevel()));
		for (String path : config.getClasspaths()) {
			engine.addToClassPath(path);
		}
	}

	public List<BkpInvariantResult> learn(List<BreakPoint> breakpoints, List<String> junitClassNames, String sourceFolder) throws Exception {
		List<String> testcases = JunitUtils.extractTestMethods(junitClassNames);
		engine.addTestcases(testcases);
		
		for(BreakPoint breakpoint: breakpoints){
			engine.addBreakPoint(breakpoint);
		}
		
		return engine.run();
	}
}
