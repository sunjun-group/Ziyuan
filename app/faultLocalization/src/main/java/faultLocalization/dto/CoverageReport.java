/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package faultLocalization.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import faultLocalization.dto.LineCoverageInfo.LineCoverageInfoComparator;

/**
 * @author khanh
 *
 */
public class CoverageReport {
	private HashMap<String, ClassCoverageInAllTestcases> mapClassLineToTestCasesCover = new HashMap<String, ClassCoverageInAllTestcases>();
	
	private HashMap<Integer, TestcaseCoverageInfo> passedTestcaseCoverageInfo = new HashMap<Integer, TestcaseCoverageInfo>();
	private HashMap<Integer, TestcaseCoverageInfo> failedTestcaseCoverageInfo = new HashMap<Integer, TestcaseCoverageInfo>();
	
	
	public CoverageReport(){
		
	}
	
	public void addInfo(int testcaseIndex, String className, int lineIndex, boolean isPassed, boolean isCovered){
		//update mapClassLineToTestCasesCover
		if (isCovered) {
			ClassCoverageInAllTestcases classCoverage;
			if (mapClassLineToTestCasesCover.containsKey(className)) {
				classCoverage = mapClassLineToTestCasesCover.get(className);
			} else {
				classCoverage = new ClassCoverageInAllTestcases(className);
				mapClassLineToTestCasesCover.put(className, classCoverage);
			}

			classCoverage.addInfo(lineIndex, testcaseIndex, isPassed);
		}
		
		//update passedTestcaseCoverageInfo, failedTestcaseCoverageInfo
		HashMap<Integer, TestcaseCoverageInfo> allTestcasesCoverageInfo = (isPassed)? passedTestcaseCoverageInfo: failedTestcaseCoverageInfo;
		TestcaseCoverageInfo testcaseCoverage;
		if(allTestcasesCoverageInfo.containsKey(testcaseIndex)){
			testcaseCoverage = allTestcasesCoverageInfo.get(testcaseIndex);
		}else{
			testcaseCoverage = new TestcaseCoverageInfo(testcaseIndex);
			allTestcasesCoverageInfo.put(testcaseIndex, testcaseCoverage);
		}
		testcaseCoverage.addInfo(className, lineIndex, isCovered);
	}
	
	public void LocalizeFault(){
		ArrayList<LineCoverageInfo> linesCoverageInfo = new ArrayList<LineCoverageInfo>();
		
		for(String key: mapClassLineToTestCasesCover.keySet()){
			ClassCoverageInAllTestcases classCoverage = mapClassLineToTestCasesCover.get(key);
			linesCoverageInfo.addAll(classCoverage.getLineCoverageInfo());
		}
		
		for(LineCoverageInfo lineCoverageInfo: linesCoverageInfo){
			lineCoverageInfo.computeSuspiciousness(passedTestcaseCoverageInfo.size(), failedTestcaseCoverageInfo.size());
		}
		
		Collections.sort(linesCoverageInfo, new LineCoverageInfoComparator());
		
		for(LineCoverageInfo lineCoverageInfo: linesCoverageInfo){
			System.out.println(lineCoverageInfo.toString());
		}
	}
}
