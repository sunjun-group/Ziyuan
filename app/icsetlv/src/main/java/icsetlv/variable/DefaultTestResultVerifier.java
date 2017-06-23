/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.variable;

import sav.strategies.dto.TestResultType;
import sav.strategies.junit.JunitResult;
import sav.strategies.junit.TestResult;

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
	public TestResultType verify(JunitResult jResult, String test) {
		TestResult testResult;
		if (jResult == null || ((testResult = jResult.getTestResult(test)) == null)) {
			return TestResultType.UNKNOWN;
		}
		return TestResultType.of(testResult.isPass());
	}
}
