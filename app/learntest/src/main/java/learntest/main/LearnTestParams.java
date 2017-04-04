/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.main;

import learntest.main.model.MethodInfo;
import learntest.util.LearnTestUtil;
import sav.common.core.SavException;

/**
 * @author LLT
 *
 */
public class LearnTestParams {
	private String filePath;
	private String testClass;
	private MethodInfo targetMethodInfo;
	
	private boolean randomDecision;
	
	public static LearnTestParams initFromLearnTestConfig() throws SavException {
		LearnTestParams params = new LearnTestParams();
		params.filePath = LearnTestConfig.getTestClassFilePath();
		params.testClass = LearnTestConfig.getTestClass(LearnTestConfig.isL2TApproach);

		String className = LearnTestConfig.targetClassName;
		String methodName = LearnTestConfig.targetMethodName;
		int lineNumber = LearnTestConfig.getMethodLineNumber();
		String methodSign = LearnTestUtil.getMethodSignature(className, methodName, lineNumber);
		params.targetMethodInfo = new MethodInfo(className, methodName, methodSign, lineNumber);
		return params;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getTestClass() {
		return testClass;
	}

	public void setTestClass(String testClass) {
		this.testClass = testClass;
	}

	public boolean isRandomDecision() {
		return randomDecision;
	}

	public void setRandomDecision(boolean randomDecision) {
		this.randomDecision = randomDecision;
	}
	
	public MethodInfo getTestMethodInfo() {
		return targetMethodInfo;
	}
}
