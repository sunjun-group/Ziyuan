/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.analysis.data.NodeCoverage;
import learntest.core.commons.data.classinfo.MethodInfo;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.core.commons.utils.CoverageUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.JunitUtils;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class LineCoverageResult {
	private Map<String, LineCoverage> testCoverageMap = Collections.EMPTY_MAP;
	private MethodInfo methodInfo;
	private Set<String> passTests;

	public LineCoverageResult(MethodInfo methodInfo) {
		this.methodInfo = methodInfo;
		passTests = new HashSet<>();
	}

	public void updateTestcase(Map<String, String> oldToNewTestcaseMap) {
		Map<String, LineCoverage> newCoverageMap = new HashMap<String, LineCoverage>();
		for (Entry<String, LineCoverage> entry : testCoverageMap.entrySet()) {
			String oldTc = entry.getKey();
			String newTc = oldToNewTestcaseMap.get(oldTc);
			if (newTc == null) {
				newTc = oldTc;
			}
			LineCoverage lineCoverage = entry.getValue();
			lineCoverage.setTestcase(newTc);
			newCoverageMap.put(newTc, lineCoverage);
		}
		testCoverageMap = newCoverageMap;
	}

	/**
	 * this approach keep all different paths of a method.
	 * */
	public static LineCoverageResult build(Collection<String> testcases, CfgCoverage cfgCoverage,
			TargetMethod targetMethod, boolean filterDuplicateLineCoverage) {
		MethodInfo methodInfo = targetMethod.getMethodInfo();
		LineCoverageResult result = new LineCoverageResult(methodInfo);
		result.testCoverageMap = new HashMap<String, LineCoverage>();
		Set<String> existingCoverages = new HashSet<String>();
		for (String testcase : testcases) {
			int orgTcIdx = cfgCoverage.getTestIdx(testcase);
			LineCoverage lineCoverage = buildLineCoverage(testcase, orgTcIdx, cfgCoverage, methodInfo);
			boolean add = true;
			if (filterDuplicateLineCoverage) {
				String coveredStr = StringUtils.join(lineCoverage.getCoveredLineNums(), ",");
				if (existingCoverages.contains(coveredStr) && 
						!(lineCoverage.isTestPass() ^ result.passTests.contains(testcase))) {
					add = false;
				} else {
					existingCoverages.add(coveredStr);
				}
			}
			if (add) {
				result.testCoverageMap.put(testcase, lineCoverage);
				if (lineCoverage.isTestPass()) {
					result.passTests.add(testcase);
				}
			}
		}
		return result;
	}

	private static LineCoverage buildLineCoverage(String newTc, int orgTcIdx, CfgCoverage cfgCoverage,
			MethodInfo methodInfo) {
		LineCoverage lineCoverage = new LineCoverage(methodInfo, newTc);
		List<Integer> coveredLineNums = new ArrayList<Integer>();
		for (NodeCoverage nodeCoverage : cfgCoverage.getNodeCoverages()) {
			int lineNo = nodeCoverage.getCfgNode().getLine();
			if (!coveredLineNums.contains(lineNo) && nodeCoverage.isCovered(orgTcIdx)) {
				coveredLineNums.add(lineNo);
			}
		}
		Collections.sort(coveredLineNums, new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return Integer.compare(o1, o2);
			}
		});
		lineCoverage.setCoveredLineNums(coveredLineNums);
		lineCoverage.setBranchCoverageText(CoverageUtils.getBranchCoverageDisplayText(cfgCoverage, orgTcIdx));
		lineCoverage.setTestPass(cfgCoverage.isPass(orgTcIdx));
		return lineCoverage;
	}

	public String getDisplayText() {
		if (CollectionUtils.isEmpty(testCoverageMap)) {
			return "empty result!";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("\nTarget method: ").append(methodInfo.getMethodFullName()).append("\n")
			.append("------------------------------------").append("\n");
		Map<String, List<String>> groupByTestClass = JunitUtils.toClassMethodsMap(testCoverageMap.keySet());
		sb.append("Generated testcases: ").append("\n");
		for (Entry<String, List<String>> entry : groupByTestClass.entrySet()) {
			sb.append(entry.getKey()).append("\n\n");
			int lastIdx = entry.getValue().size() - 1;
			StringUtils.sortAlphanumericStrings(entry.getValue());
			for (int i = 0; i <= lastIdx; i++) {
				String method = entry.getValue().get(i);
				sb.append("method ").append(method).append("(): lines[");
				LineCoverage lineCoverage = testCoverageMap.get(StringUtils.dotJoin(entry.getKey(), method));
				sb.append(StringUtils.join(lineCoverage.getCoveredLineNums(), ", ")).append("]").append("\n");
				sb.append(lineCoverage.getBranchCoverageText());
				if (i != lastIdx) {
					sb.append("\n\n");
				}
			}
		}
		return sb.toString();
	}

	public Map<String, LineCoverage> getTestCoverageMap() {
		return testCoverageMap;
	}

	public Collection<String> getCoveredTestcases() {
		if (CollectionUtils.isEmpty(testCoverageMap)) {
			return new ArrayList<String>(0);
		}
		return testCoverageMap.keySet();
	}

	@Override
	public String toString() {
		return getDisplayText();
	}

	public Set<Integer> getCoveredLines(List<String> selectedTcs) {
		Set<Integer> lines = new HashSet<Integer>();
		if (CollectionUtils.isEmpty(selectedTcs)) {
			for (LineCoverage lineCoverage : testCoverageMap.values()) {
				lines.addAll(lineCoverage.getCoveredLineNums());
			}
		} else {
			for (String testcase : selectedTcs) {
				LineCoverage lineCoverage = testCoverageMap.get(testcase);
				lines.addAll(lineCoverage.getCoveredLineNums());
			}
		}
		return lines;
	}
	
	public MethodInfo getMethodInfo() {
		return methodInfo;
	}
	
	public Set<String> getPassTests() {
		return passTests;
	}
}
