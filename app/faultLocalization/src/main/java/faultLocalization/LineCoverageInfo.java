/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package faultLocalization;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.builder.CompareToBuilder;

import sav.strategies.dto.ClassLocation;
import faultLocalization.SpectrumBasedSuspiciousnessCalculator.SpectrumAlgorithm;

/**
 * @author khanh
 * 
 */
public class LineCoverageInfo {
	private ClassLocation location;
	private double suspiciousness;

	/**
	 * @return the suspiciousness
	 */
	public double getSuspiciousness() {
		return suspiciousness;
	}

	private ArrayList<Integer> passedTestcaseIndexesCover = new ArrayList<Integer>();
	private ArrayList<Integer> failedTestcaseIndexesCover = new ArrayList<Integer>();

	public LineCoverageInfo(String className, int lineIndex) {
		location = new ClassLocation(className, null, lineIndex);
	}

	public void addInfo(int testcaseIndex, boolean isPassed) {
		if (isPassed) {
			passedTestcaseIndexesCover.add(testcaseIndex);
		} else {
			failedTestcaseIndexesCover.add(testcaseIndex);
		}
	}

	public void computeSuspiciousness(int totalPassed, int totalFailed, SpectrumAlgorithm algorithm) {
		final SpectrumBasedSuspiciousnessCalculator calculator = new SpectrumBasedSuspiciousnessCalculator(totalPassed, totalFailed,
				passedTestcaseIndexesCover.size(), failedTestcaseIndexesCover.size(), algorithm);

		this.suspiciousness = calculator.compute();
	}
	
	public String toString() {
		return location.getId() + ":" + suspiciousness + "(P=" + passedTestcaseIndexesCover.size()
				+ ", F=" + failedTestcaseIndexesCover.size();
	}

	public static class LineCoverageInfoComparator implements Comparator<LineCoverageInfo> {
		public int compare(final LineCoverageInfo c1, final LineCoverageInfo c2) {
			return new CompareToBuilder().append(c2.getSuspiciousness(), c1.getSuspiciousness())
					.append(c1.location.getLineNo(), c2.location.getLineNo()).toComparison();
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

	public ClassLocation getLocation() {
		return location;
	}
}
