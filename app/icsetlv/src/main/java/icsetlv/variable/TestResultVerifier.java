/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.variable;

import java.util.HashSet;
import java.util.Set;

import icsetlv.variable.TestcasesExecutor.TestResultType;
import sav.common.core.Logger;
import sav.strategies.junit.JunitResult;


/**
 * @author LLT
 *
 */
public class TestResultVerifier extends DefaultTestResultVerifier implements ITestResultVerifier {
	private Logger<?> log = Logger.getDefaultLogger();
	private JunitResult orgResult;
	private Set<String> allTraces;
	
	public TestResultVerifier(JunitResult orgResult) {
		setupOrgResult(orgResult);
	}

	@Override
	public TestResultType verify(JunitResult jResult, String test) {
		sav.strategies.junit.TestResult testResult = jResult.getTestResult(test);
		if (testResult.isPass() || orgResult == null) {
			return TestResultType.of(testResult.isPass());
		}
		// check if the trace match with the original;
		sav.strategies.junit.TestResult orgTestResult = orgResult.getTestResult(test);
		if (allTraces.contains(testResult.getFailureTrace())) {
			return TestResultType.FAIL;
		}
		if (!orgTestResult.getFailureTrace().isEmpty()) {
			log.debug("unknown test result. orgTrace:",
					orgTestResult.getFailureTrace(), ", currentTrace: ",
					testResult.getFailureTrace());
		}
		return TestResultType.UNKNOWN;
	}

	public void setupOrgResult(JunitResult jResult) {
		this.orgResult = jResult;
		allTraces = new HashSet<String>();
		for (sav.strategies.junit.TestResult testResult : jResult.getTestResults().values()) {
			if (!testResult.isPass()) {
				allTraces.add(testResult.getFailureTrace());
			}
		}
	}

	public JunitResult getOrgResult() {
		return orgResult;
	}
}
