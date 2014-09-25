/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package faultLocalization.dto;

import faultLocalization.dto.SuspiciousnessCalculator.SuspiciousnessCalculationAlgorithm;
import icsetlv.common.utils.BreakpointUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.builder.CompareToBuilder;

/**
 * @author khanh
 * 
 */
public class LineCoverageInfo {
	private final String className;
	private final int lineIndex;
	private double suspiciousness;
	private final String locId;

	/**
	 * @return the suspiciousness
	 */
	public double getSuspiciousness() {
		return suspiciousness;
	}

	private ArrayList<Integer> passedTestcaseIndexesCover = new ArrayList<Integer>();
	private ArrayList<Integer> failedTestcaseIndexesCover = new ArrayList<Integer>();

	public LineCoverageInfo(String className, int lineIndex) {
		this.className = className;
		this.lineIndex = lineIndex;
		locId = BreakpointUtils.getLocationId(className, lineIndex);
	}

	public void addInfo(int testcaseIndex, boolean isPassed) {
		if (isPassed) {
			passedTestcaseIndexesCover.add(testcaseIndex);
		} else {
			failedTestcaseIndexesCover.add(testcaseIndex);
		}
	}

	public void computeSuspiciousness(int totalPassed, int totalFailed, SuspiciousnessCalculationAlgorithm algorithm) {
		final SuspiciousnessCalculator calculator = new SuspiciousnessCalculator();
		calculator.setCoveredAndFailed(failedTestcaseIndexesCover.size());
		calculator.setCoveredAndPassed(passedTestcaseIndexesCover.size());
		calculator.setFailed(totalFailed);
		calculator.setPassed(totalPassed);

		this.suspiciousness = calculator.getSuspiciousness(algorithm);
	}
	
	public String toString() {
		return className + "@" + lineIndex + ":" + suspiciousness;
	}

	public static class LineCoverageInfoComparator implements Comparator<LineCoverageInfo> {
		public int compare(final LineCoverageInfo c1, final LineCoverageInfo c2) {
			return new CompareToBuilder().append(c2.getSuspiciousness(), c1.getSuspiciousness())
					.append(c1.lineIndex, c2.lineIndex).toComparison();
		}
	}

	/**
	 * @param map
	 *            update the difference of passed commpared with failed testcase
	 */
	public void updateDifference(Map<Integer, Integer> map, Integer failedTest) {
		boolean isCoveredByFailTest = this.failedTestcaseIndexesCover.contains(failedTest);

		for (Entry<Integer, Integer> entry : map.entrySet()) {
			int value = (this.passedTestcaseIndexesCover.contains(entry.getKey()) == isCoveredByFailTest) ? 0
					: 1;

			entry.setValue(entry.getValue() + value);
		}
	}

	public String getClassName() {
		return className;
	}

	public int getLineIndex() {
		return lineIndex;
	}

	public String getLocId() {
		return locId;
	}
}
