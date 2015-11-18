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
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;
import tzuyu.core.main.FaultLocateParams;

/**
 * @author khanh
 * 
 */
public class LearnInvariants {
	private Engine engine;

	public LearnInvariants(AppJavaClassPath app, FaultLocateParams params) {
		engine = new Engine(app);
		engine.setTestcaseExecutor(new TestcasesExecutor(params.getValueRetrieveLevel()));
	}

	public List<BkpInvariantResult> learn(List<BreakPoint> breakpoints, List<String> junitClassNames, String sourceFolder) throws Exception {
		List<String> testcases = JunitUtils.extractTestMethods(junitClassNames, null);
		engine.addTestcases(testcases);
		
		for(BreakPoint breakpoint: breakpoints){
			engine.addBreakPoint(breakpoint);
		}
		
		return engine.run();
	}
}
