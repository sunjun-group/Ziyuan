/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.main;

/**
 * @author LLT
 *
 */
public class LearnTestParams {
	private String filePath;
	private String typeName;
	private String className;
	private String methodName;
	private String testClass;
	private int methodLineNum;
	
	public static LearnTestParams initFromLearnTestConfig() {
		LearnTestParams params = new LearnTestParams();
		params.filePath = LearnTestConfig.getTestClassFilePath();
		params.typeName = LearnTestConfig.getSimpleClassName();
		params.className = LearnTestConfig.testClassName;
		params.methodName = LearnTestConfig.testMethodName;
		params.testClass = LearnTestConfig.getTestClass(LearnTestConfig.isL2TApproach);
		params.methodLineNum = LearnTestConfig.getMethodLineNumber();
		return params;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getTestClass() {
		return testClass;
	}

	public void setTestClass(String testClass) {
		this.testClass = testClass;
	}

	public int getMethodLineNum() {
		return methodLineNum;
	}

	public void setMethodLineNum(int methodLineNum) {
		this.methodLineNum = methodLineNum;
	}
	
}
