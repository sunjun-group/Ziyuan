/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.test;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import learntest.core.commons.data.LearnTestApproach;
import learntest.core.commons.test.gan.GanTestTool;

/**
 * @author LLT
 *
 */
public class TestTools {
	private static TestTools instance = new TestTools();
	public GanTestTool gan = new GanTestTool();
	public TestTool curTestTool = TestTool.EMTPY_INSTANCE;
	
	public void reset(LearnTestApproach approach) {
		switch (approach) {
		case GAN:
			curTestTool = gan;
			break;

		default:
			break;
		}
	}
	
	public static TestTool getCurTestTool() {
		return getInstance().curTestTool;
	}
	
	public static TestTools getInstance() {
		return instance;
	}
	
	public void logFirstCoverage(double firstCoverage, CfgCoverage cfgCoverage) {
		curTestTool.logFirstCoverage(firstCoverage, cfgCoverage);
	}

	public static void log(String text) {
		instance.curTestTool.logFormat(text);
	}
	
	public static void log(Object... texts) {
		instance.curTestTool.log(texts);
	}
	
}
