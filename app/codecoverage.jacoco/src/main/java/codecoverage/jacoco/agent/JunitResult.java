/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package codecoverage.jacoco.agent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import sav.common.core.Pair;
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
	private Map<Pair<String, String>, Boolean> testResult = new HashMap<Pair<String, String>, Boolean>();
	private List<Boolean> result = new ArrayList<Boolean>();

	public Set<BreakPoint> getFailureTraces() {
		return failureTraces;
	}
	
	public void addResult(Pair<String, String> classMethod, boolean pass) {
		testResult.put(classMethod, pass);
		result.add(pass);
	}
	
	public boolean getResult(int idx) {
		return result.get(idx);
	}

	public void save(File file) throws IOException {
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(file, true);
			IOUtils.write(JUNIT_RUNNER_BLOCK, output);
			IOUtils.write("\n", output);
			for (Entry<Pair<String, String>, Boolean> entry : testResult.entrySet()) {
				IOUtils.write(StringUtils.spaceJoin(entry.getKey().a,
						entry.getKey().b, entry.getValue()), output);
				IOUtils.write("\n", output);
			}
			if (!failureTraces.isEmpty()) {
				IOUtils.write(JUNIT_RUNNER_FAILURE_TRACE, output);
				IOUtils.write("\n", output);
				for (ClassLocation loc : failureTraces) {
					IOUtils.write(StringUtils.spaceJoin(
							loc.getClassCanonicalName(), loc.getMethodName(),
							loc.getLineNo()), output);
					IOUtils.write("\n", output);
				}
			}
		} finally {
			IOUtils.closeQuietly(output);
		}
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
				result.result.add(Boolean.valueOf(strs[strs.length - 1]));
			} else if (block == JUNIT_RUNNER_FAILURE_TRACE) {
				String[] strs = line.split(" ");
				result.failureTraces.add(new BreakPoint(strs[0], 
						strs[1], Integer.valueOf(strs[2])));
			}
		}
		return result;
	}

}
