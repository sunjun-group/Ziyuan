/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.variable;

import icsetlv.variable.TestcasesExecutor.TestResultType;
import sav.strategies.junit.JunitResult;

/**
 * @author LLT
 *
 */
public interface ITestResultVerifier {

	TestResultType verify(JunitResult jResult, String test);

}
