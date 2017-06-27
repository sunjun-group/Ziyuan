/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.gentest;

import gentest.junit.PrinterParams;
import gentest.junit.TestsPrinter.PrintOption;

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
	private long methodExecTimeout;

	/* for generated tests printer */
	private PrinterParams printerParams = new PrinterParams();
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

	public void setTestSrcFolder(String testSrcFolder) {
		this.printerParams.setSrcPath(testSrcFolder);
	}

	public void setTestPkg(String testPkg) {
		this.printerParams.setPkg(testPkg);
	}

	public void setTestClassPrefix(String testClassPrefix) {
		this.printerParams.setClassPrefix(testClassPrefix);
	}

	public void setTestMethodPrefix(String testMethodPrefix) {
		this.printerParams.setMethodPrefix(testMethodPrefix);
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
	
	public void setPrintOption(PrintOption option) {
		printerParams.setPrintOption(option);
	}
	
	public PrinterParams getPrinterParams() {
		return printerParams;
	}

	public long getMethodExecTimeout() {
		return methodExecTimeout;
	}

	public void setMethodExecTimeout(long methodExecTimeout) {
		this.methodExecTimeout = methodExecTimeout;
	}
}
