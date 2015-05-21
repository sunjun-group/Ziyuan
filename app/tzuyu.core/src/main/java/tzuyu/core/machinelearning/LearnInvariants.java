/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.machinelearning;

import icsetlv.Engine;
import icsetlv.Engine.Result;

import java.util.List;

import sav.common.core.utils.JunitUtils;
import sav.strategies.dto.BreakPoint;
import sav.strategies.vm.VMConfiguration;

/**
 * @author khanh
 * 
 */
public class LearnInvariants {
	private Engine engine;

	public LearnInvariants(final VMConfiguration config) {
		engine = new Engine().setPort(config.getPort()).setJavaHome(config.getJavaHome());
		for (String path : config.getClasspaths()) {
			engine.addToClassPath(path);
		}
	}

	public void learn(List<BreakPoint> breakpoints, List<String> junitClassNames, String sourceFolder) throws Exception {
		List<String> testcases = JunitUtils.extractTestMethods(junitClassNames);
		engine.addNotExecutedTestcases(testcases);
		
		for(BreakPoint breakpoint: breakpoints){
			engine.addBreakPoint(breakpoint);
		}
		
		engine.run();
		final List<Result> results = engine.getResults();
	}
}
