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
import sav.strategies.dto.ClassLocation;

/**
 * @author khanh
 *
 */
public class LearnInvariants {
	private static final int DEBUG_PORT = 8787;
	private Engine engine;

	public LearnInvariants() {
//		final TestConfiguration config = TestConfiguration.getInstance();
//		
//		engine = new Engine().setPort(DEBUG_PORT)
//				.setJavaHome(TestConfigUtils.getJavaHome())
//				.addToClassPath(config.getJavaBin())
//				.addToClassPath("E:/Code/Tzuyu/trunk/etc/app_assembly/sav-commons.jar")
//		.addToClassPath(TestConfiguration.getTestTarget("sav.commons"));
	}

	public void learn(List<ClassLocation> locations, List<String> junitClassNames) throws Exception{
		List<String> testcases = JunitUtils.extractTestMethods(junitClassNames);
		engine.addNotExecutedTestcases(testcases);
		
		for(ClassLocation location: locations){
			engine.addBreakPoint(location.getClassCanonicalName(), location.getMethodSign(), getNextLineNumber(location));
		}

		engine.run();
		final List<Result> results = engine.getResults();
		for (Result result : results) {
			System.out.println(result);
		}
	}

	/*
	 * TODO: Use Java Parser to get the next statement
	 */
	private int getNextLineNumber(ClassLocation location) {
		//location.getLineNo() + 1: we want to learn the line after
		return location.getLineNo() + 1;
	}

}
