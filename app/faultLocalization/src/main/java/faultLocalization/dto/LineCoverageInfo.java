/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package faultLocalization.dto;

import icsetlv.common.utils.BreakpointUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author khanh
 *
 */
public class LineCoverageInfo {
	private final String className;
	private final int lineIndex;
	private float suspiciousness;
	private String locId;
	
	/**
	 * @return the suspiciousness
	 */
	public float getSuspiciousness() {
		return suspiciousness;
	}

	private ArrayList<Integer> passedTestcaseIndexesCover = new ArrayList<Integer>();
	private ArrayList<Integer> failedTestcaseIndexesCover = new ArrayList<Integer>();
	
	public LineCoverageInfo(String className, int lineIndex){
		this.className = className;
		this.lineIndex = lineIndex;
		locId = BreakpointUtils.getLocationId(className, lineIndex);
	}
	
	public String getClassName() {
		return className;
	}



	public int getLineIndex() {
		return lineIndex;
	}



	public void addInfo(int testcaseIndex, boolean isPassed){
		if(isPassed){
			passedTestcaseIndexesCover.add(testcaseIndex);
		}else{
			failedTestcaseIndexesCover.add(testcaseIndex);
		}
	}
	
	public void computeSuspiciousness(int totalPassed, int totalFailed){
		float failedRate = failedTestcaseIndexesCover.size() / (float) totalFailed;
		float passRate = passedTestcaseIndexesCover.size() / (float) totalPassed;
		
		this.suspiciousness = failedRate / (passRate + failedRate);
	}
	
	public String toString() {
		return className + "@" + lineIndex + ":" + suspiciousness;
	}
	
	public static class LineCoverageInfoComparator implements Comparator<LineCoverageInfo>
	{
		public int compare(LineCoverageInfo c1, LineCoverageInfo c2)
		{
			int result = Float.valueOf(c2.getSuspiciousness()).compareTo(Float.valueOf(c1.getSuspiciousness()));
			if(result != 0){
				return result;
			}else{
				return Integer.valueOf(c1.lineIndex).compareTo(c2.lineIndex);
			}
		}
	}
	
	/**
	 * @param map update the difference of passed commpared with failed testcase
	 */
	public void updateDifference(Map<Integer, Integer> map, Integer failedTest){
		boolean isCoveredByFailTest = this.failedTestcaseIndexesCover.contains(failedTest);
		
		for(Entry<Integer, Integer> entry: map.entrySet()){
			int value = (this.passedTestcaseIndexesCover.contains(entry.getKey()) == isCoveredByFailTest)? 0: 1;
			
			entry.setValue(entry.getValue() + value);
		}
	}
	
	public String getLocId() {
		return locId;
	}
}

