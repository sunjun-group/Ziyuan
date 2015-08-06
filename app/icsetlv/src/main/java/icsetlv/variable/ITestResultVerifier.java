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
public interface ITestResultVerifier {

	TestResult verify(JunitResult jResult, String test);

}
