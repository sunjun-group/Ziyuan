/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package faultLocalization;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author khanh
 * 
 */
public class ClassCoverageInAllTestcases extends AbstractClassCoverage {

	private Map<Integer, LineCoverageInfo> mapLines2CoverageInfo 
								= new HashMap<Integer, LineCoverageInfo>();

	public ClassCoverageInAllTestcases(final String classResourcePath) {
		super(classResourcePath);
	}

	public void addInfo(final int lineIndex, final int testcaseIndex, final boolean isPassed) {
		LineCoverageInfo lineCoverage = mapLines2CoverageInfo.get(lineIndex);
		if (lineCoverage == null) {
			lineCoverage = new LineCoverageInfo(getClassResourcePath(), lineIndex);
			mapLines2CoverageInfo.put(lineIndex, lineCoverage);
		}

		lineCoverage.addInfo(testcaseIndex, isPassed);
	}

	public Collection<LineCoverageInfo> getLineCoverageInfo() {
		return mapLines2CoverageInfo.values();
	}

	public void updateDifference(final Map<Integer, Integer> map, final Integer failedTest) {
		for (LineCoverageInfo lineInfo : mapLines2CoverageInfo.values()) {
			lineInfo.updateDifference(map, failedTest);
		}
	}

}
