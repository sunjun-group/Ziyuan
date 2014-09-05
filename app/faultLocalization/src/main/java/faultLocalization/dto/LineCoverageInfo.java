/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package faultLocalization.dto;

import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * @author khanh
 *
 */
public class LineCoverageInfo {
	private String className;
	private int lineIndex;
	private float suspiciousness;
	
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
}

