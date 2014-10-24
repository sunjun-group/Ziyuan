/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package faultLocalization;

import java.util.HashMap;
import java.util.Map;

/**
 * @author khanh
 *
 */
public class TestcaseCoverageInfo {
	private int testcaseIndex;
	private boolean isPassed;
	private Map<String, ClassCoverageInSingleTestcase> mapClass2LineCoverage 
							= new HashMap<String, ClassCoverageInSingleTestcase>();
	
	public TestcaseCoverageInfo(int testcaseIndex){
		this.testcaseIndex = testcaseIndex;
	}

	public void addInfo(String className, int lineIndex, boolean isCovered){
		ClassCoverageInSingleTestcase classCoverage;
		if (mapClass2LineCoverage.containsKey(className)) {
			classCoverage = mapClass2LineCoverage.get(className);
		} else {
			classCoverage = new ClassCoverageInSingleTestcase(className);
			mapClass2LineCoverage.put(className, classCoverage);
		}
		
		classCoverage.addInfo(lineIndex, isCovered);
	}
}
