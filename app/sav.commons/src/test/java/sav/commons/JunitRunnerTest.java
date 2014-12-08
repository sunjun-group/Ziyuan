/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.commons;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import sav.commons.testdata.SampleTestCase;

/**
 * @author LLT
 *
 */
public class JunitRunnerTest {

	@Test
	public void test() {
		JUnitCore core = new JUnitCore();
		Result result = core.run(SampleTestCase.class);
		for (Failure fail : result.getFailures()) {
			System.out.println(fail.toString());
		}
	}
	
}
