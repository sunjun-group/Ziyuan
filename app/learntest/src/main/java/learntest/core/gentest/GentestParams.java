/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.gentest;

/**
 * @author LLT
 *
 */
public class GentestParams {
	private static final int DEFAULT_QUERY_MAX_LENGTH = 1;
	private String methodSignature;
	private String targetClassName;
	private int numberOfTcs;
	private int testPerQuery;

	/* for generated tests printer */
	private String testSrcFolder;
	private String testPkg;
	private String testClassPrefix;
	private String testMethodPrefix;
	private boolean generateMainClass;

	public String getMethodSignature() {
		return methodSignature;
	}

	public void setMethodSignature(String methodSignature) {
		this.methodSignature = methodSignature;
	}

	public String getTargetClassName() {
		return targetClassName;
	}

	public void setTargetClassName(String targetClassName) {
		this.targetClassName = targetClassName;
	}

	public int getNumberOfTcs() {
		return numberOfTcs;
	}

	public void setNumberOfTcs(int numberOfTcs) {
		this.numberOfTcs = numberOfTcs;
	}

	public int getTestPerQuery() {
		return testPerQuery;
	}

	public void setTestPerQuery(int testPerQuery) {
		this.testPerQuery = testPerQuery;
	}

	public String getTestSrcFolder() {
		return testSrcFolder;
	}

	public void setTestSrcFolder(String testSrcFolder) {
		this.testSrcFolder = testSrcFolder;
	}

	public String getTestPkg() {
		return testPkg;
	}

	public void setTestPkg(String testPkg) {
		this.testPkg = testPkg;
	}

	public String getTestClassPrefix() {
		return testClassPrefix;
	}

	public void setTestClassPrefix(String testClassPrefix) {
		this.testClassPrefix = testClassPrefix;
	}

	public String getTestMethodPrefix() {
		return testMethodPrefix;
	}

	public void setTestMethodPrefix(String testMethodPrefix) {
		this.testMethodPrefix = testMethodPrefix;
	}

	public int getQueryMaxLength() {
		return DEFAULT_QUERY_MAX_LENGTH;
	}

	public boolean generateMainClass() {
		return generateMainClass;
	}

	public void setGenerateMainClass(boolean generateMainClass) {
		this.generateMainClass = generateMainClass;
	}
	
}
