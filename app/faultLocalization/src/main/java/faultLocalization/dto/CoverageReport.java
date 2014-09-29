/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package faultLocalization.dto;

import faultLocalization.dto.LineCoverageInfo.LineCoverageInfoComparator;
import faultLocalization.dto.SuspiciousnessCalculator.SuspiciousnessCalculationAlgorithm;
import icsetlv.common.dto.BreakPoint;
import icsetlv.common.dto.ClassLocation;
import icsetlv.common.utils.BreakpointUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import sav.common.core.Logger;

/**
 * @author khanh
 * 
 */
public class CoverageReport {
	private Logger<?> logger = Logger.getDefaultLogger();
	private Map<String, ClassCoverageInAllTestcases> mapClassLineToTestCasesCover = new HashMap<String, ClassCoverageInAllTestcases>();
	private Map<Integer, TestcaseCoverageInfo> passedTestcaseCoverageInfo = new HashMap<Integer, TestcaseCoverageInfo>();
	private Map<Integer, TestcaseCoverageInfo> failedTestcaseCoverageInfo = new HashMap<Integer, TestcaseCoverageInfo>();

	private final List<BreakPoint> failureTraces = new ArrayList<BreakPoint>();

	private final List<String> testingClassNames;

	public CoverageReport(final List<String> testingClassNames) {
		this.testingClassNames = testingClassNames;
	}

	public void addInfo(final int testcaseIndex, final String className, final int lineIndex,
			final boolean isPassed, final boolean isCovered) {
		// update mapClassLineToTestCasesCover
		if (isCovered) {
			ClassCoverageInAllTestcases classCoverage = mapClassLineToTestCasesCover.get(className);
			if (classCoverage == null) {
				classCoverage = new ClassCoverageInAllTestcases(className);
				mapClassLineToTestCasesCover.put(className, classCoverage);
			}

			classCoverage.addInfo(lineIndex, testcaseIndex, isPassed);
		}

		// update passedTestcaseCoverageInfo, failedTestcaseCoverageInfo
		final Map<Integer, TestcaseCoverageInfo> allTestcasesCoverageInfo = (isPassed) ? passedTestcaseCoverageInfo
				: failedTestcaseCoverageInfo;
		TestcaseCoverageInfo testcaseCoverage = allTestcasesCoverageInfo.get(testcaseIndex);
		if (testcaseCoverage == null) {
			testcaseCoverage = new TestcaseCoverageInfo(testcaseIndex);
			allTestcasesCoverageInfo.put(testcaseIndex, testcaseCoverage);
		}
		testcaseCoverage.addInfo(className, lineIndex, isCovered);
	}

	public List<LineCoverageInfo> tarantula() {
		return tarantula(new ArrayList<ClassLocation>());
	}

	/**
	 * @param failedTestcaseIndex
	 * @return return the passed testcase with the spectrum which is nearest
	 *         compared with the one of the failed testcase
	 */
	public int getNearestPassedTestcase(final int failedTestcaseIndex) {
		final Map<Integer, Integer> mapPassedTest2Difference = new HashMap<Integer, Integer>();
		// init value
		for (Integer passedTest : passedTestcaseCoverageInfo.keySet()) {
			mapPassedTest2Difference.put(passedTest, 0);
		}

		for (Entry<String, ClassCoverageInAllTestcases> entry : mapClassLineToTestCasesCover
				.entrySet()) {
			final String tempClassName = entry.getKey().replace('/', '.');
			if (testingClassNames.contains(tempClassName)) {
				entry.getValue().updateDifference(mapPassedTest2Difference, failedTestcaseIndex);
			}
		}

		int minDifference = Integer.MAX_VALUE;
		int nearestPassedIndex = -1;

		for (Entry<Integer, Integer> entry : mapPassedTest2Difference.entrySet()) {
			Integer difference = entry.getValue();

			if (difference > 0 && difference < minDifference) {
				minDifference = entry.getKey();
				nearestPassedIndex = entry.getKey();
			}
		}

		logger.debug("nearest " + nearestPassedIndex);
		return nearestPassedIndex;
	}

	public void test() {
		for (Integer failTest : failedTestcaseCoverageInfo.keySet()) {
			getNearestPassedTestcase(failTest);
		}
	}

	public void addFailureTrace(final List<BreakPoint> traces) {
		failureTraces.addAll(traces);
	}

	public List<BreakPoint> getFailureTraces() {
		return failureTraces;
	}

	public <T extends ClassLocation> List<LineCoverageInfo> tarantula(final List<T> filteredPoints) {
		final List<LineCoverageInfo> linesCoverageInfo = new ArrayList<LineCoverageInfo>();

		final List<String> pointLocIds = BreakpointUtils.toLocationIds(filteredPoints);
		for (ClassCoverageInAllTestcases classCoverage : mapClassLineToTestCasesCover.values()) {
			if (pointLocIds.isEmpty()) {
				linesCoverageInfo.addAll(classCoverage.getLineCoverageInfo());
				continue;
			}
			for (LineCoverageInfo lineInfo : classCoverage.getLineCoverageInfo()) {
				if (pointLocIds.contains(lineInfo.getLocId())) {
					linesCoverageInfo.add(lineInfo);
				}
			}
		}

		// use slicing to remove unrelated lines in linesCoverageInfo
		// failed test -> where failed assertion -> line -> slicing
		//
		for (LineCoverageInfo lineCoverageInfo : linesCoverageInfo) {
			lineCoverageInfo.computeSuspiciousness(passedTestcaseCoverageInfo.size(),
					failedTestcaseCoverageInfo.size(), SuspiciousnessCalculationAlgorithm.JACCARD);
		}

		Collections.sort(linesCoverageInfo, new LineCoverageInfoComparator());

		for (LineCoverageInfo lineCoverageInfo : linesCoverageInfo) {
			// TODO replace with log4j?
			System.out.println(lineCoverageInfo.toString());
		}
		return linesCoverageInfo;
	}
}
