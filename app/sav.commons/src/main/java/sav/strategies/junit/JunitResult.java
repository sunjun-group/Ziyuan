/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.junit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import sav.common.core.Pair;
import sav.common.core.utils.JunitUtils;
import sav.common.core.utils.StringUtils;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.ClassLocation;

/**
 * @author LLT
 * 
 */
public class JunitResult {
	private static final String JUNIT_RUNNER_BLOCK = "JunitRunner Result";
	private static final String JUNIT_RUNNER_FAILURE_TRACE = "FailureTraces";
	private Set<BreakPoint> failureTraces = new HashSet<BreakPoint>();
	private Map<Pair<String, String>, Boolean> testResult = new HashMap<Pair<String,String>, Boolean>();
	private List<Boolean> result = new ArrayList<Boolean>();

	public Set<BreakPoint> getFailureTraces() {
		return failureTraces;
	}
	
	public void addResult(Pair<String, String> classMethod, boolean pass) {
		testResult.put(classMethod, pass);
		result.add(pass);
	}
	
	public List<Pair<String, String>> getFailTests() {
		List<Pair<String, String>> tests = new ArrayList<Pair<String,String>>();
		for (Entry<Pair<String, String>, Boolean> entry : testResult.entrySet()) {
			if (!entry.getValue()) {
				tests.add(entry.getKey());
			}
		}
		return tests;
	}
	
	public boolean getResult(int idx) {
		if (idx < 0 || idx >= result.size()) {
			return false;
		}
		return result.get(idx);
	}
	
	public Map<String, Boolean> getResult(List<String> tcs) {
		List<Pair<String, String>> keys = JunitUtils.toPair(tcs);
		Map<String, Boolean> tcResults = new HashMap<String, Boolean>();
		for (int i = 0; i < keys.size(); i++) {
			tcResults.put(tcs.get(i), testResult.get(keys.get(i)));
		}
		return tcResults;
	}
	
	public List<Boolean> getTestResult() {
		return result;
	}

	public void save(File file) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(JUNIT_RUNNER_BLOCK)
			.append("\n");
		for (Entry<Pair<String, String>, Boolean> entry : testResult.entrySet()) {
			sb.append(StringUtils.spaceJoin(entry.getKey().a,
					entry.getKey().b, entry.getValue()))
				.append("\n");
		}
		if (!failureTraces.isEmpty()) {
			sb.append(JUNIT_RUNNER_FAILURE_TRACE)
				.append("\n");
			for (ClassLocation loc : failureTraces) {
				sb.append(StringUtils.spaceJoin(
						loc.getClassCanonicalName(), loc.getMethodName(),
						loc.getLineNo()))
					.append("\n");
			}
		}
		FileUtils.writeStringToFile(file, sb.toString()); 
	}
	
	@SuppressWarnings("unchecked")
	public static JunitResult readFrom(String junitResultFile)
			throws IOException {
		List<String> lines = (List<String>) FileUtils.readLines(new File(
				junitResultFile));
		JunitResult result = new JunitResult();
		String block = null;
		for (String line : lines) {
			if (JUNIT_RUNNER_BLOCK.equals(line)) {
				block = JUNIT_RUNNER_BLOCK;
				continue;
			}
			if (JUNIT_RUNNER_FAILURE_TRACE.equals(line)) {
				block = JUNIT_RUNNER_FAILURE_TRACE;
				continue;
			}
			if (block == JUNIT_RUNNER_BLOCK) {
				String[] strs = line.split(" ");
				if (strs.length != 3) {
					throw new IllegalArgumentException(
							"junit runner block was written incorrectly, expect \"{class} {method} {line}, get \""
									+ line);
				}
				Boolean pass = Boolean.valueOf(strs[strs.length - 1]);
				result.testResult.put(Pair.of(strs[0], strs[1]), pass);
				result.result.add(pass);
			} else if (block == JUNIT_RUNNER_FAILURE_TRACE) {
				String[] strs = line.split(" ");
				result.failureTraces.add(new BreakPoint(strs[0], 
						strs[1], Integer.valueOf(strs[2])));
			}
		}
		return result;
	}

	
}
