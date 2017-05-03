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
		if (jResult == null) {
			return TestResultType.UNKNOWN;
		}
		return TestResultType.of(jResult.getResult(test));
	}

}
