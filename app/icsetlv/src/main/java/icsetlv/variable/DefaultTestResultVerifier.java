/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.variable;

import icsetlv.variable.TestcasesExecutor.TestResult;
import sav.strategies.junit.JunitResult;

/**
 * @author LLT
 * 
 */
public class DefaultTestResultVerifier implements ITestResultVerifier {
	private static final ITestResultVerifier INSTANCE = new DefaultTestResultVerifier();

	public static ITestResultVerifier getInstance() {
		return INSTANCE;
	}

	@Override
	public TestResult verify(JunitResult jResult, String test) {
		return TestResult.of(jResult.getResult(test));
	}

}
