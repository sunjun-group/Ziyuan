/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.junit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LLT
 * 
 */
public class JunitRunnerParameters {
	static final String TESTCASES = "testcases";
	static final String TESTCASE_TIME_OUT = "tc_timeout";
	
	private List<String[]> testcases;
	/* for each testcase, to make sure the testcase does not run forever */
	private long timeout; 
	
	public static JunitRunnerParameters parse(String[] args) {
		CommandLine cmd = CommandLine.parse(args);
		JunitRunnerParameters params = new JunitRunnerParameters();
		List<String> tcFullNames = cmd.getStringList(TESTCASES);
		params.setTestcases(tcFullNames);
		params.timeout = cmd.getLong(TESTCASE_TIME_OUT, -1);
		return params;
	}

	public void setTestcases(List<String> tcFullNames) {
		List<String[]> tcs = new ArrayList<String[]>();
		for (String tcFullName : tcFullNames) {
			tcs.add(splitTestcase(tcFullName));
		}
		testcases = tcs;
	}
	
	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
	public List<String[]> getTestcases() {
		return testcases;
	}
	
	public static String[] splitTestcase(String name) {
		int idx = name.lastIndexOf(".");
		if (idx > 0) {
			return new String[] { name.substring(0, idx), name.substring(idx + 1) };
		}
		throw new IllegalArgumentException(
				"Cannot extract method from string, expect [classname].[method], get "
						+ name);
	}
	
	public static String toTestcaseName(String className, String methodName) {
		return new StringBuilder(className).append(".").append(methodName).toString();
	}
}
