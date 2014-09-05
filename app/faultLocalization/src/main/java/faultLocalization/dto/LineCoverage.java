/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package faultLocalization.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author LLT
 * 
 */
public class LineCoverage {
	private String classResourcePath;
	private Map<Integer, int[]> lineCorverageMap;
	
	public LineCoverage(String classResourcePath) {
		lineCorverageMap = new HashMap<Integer, int[]>();
		this.classResourcePath = classResourcePath;
	}
	
	public void addResult(boolean isPassed, int line, boolean cover) {
		int[] passFail = lineCorverageMap.get(line);
		if (passFail == null) {
			passFail = new int[2];
			lineCorverageMap.put(line, passFail);
		}
		if (cover) {
			if (isPassed) {
				passFail[0]++;
			} else {
				passFail[1]++;
			}
		}
	}

	public int count(int line, Boolean option) {
		int[] passFail = lineCorverageMap.get(line);
		if (passFail == null) {
			return 0;
		}
		if (option == null) {
			return passFail[0] + passFail[1];
		}
		if (option) {
			return passFail[0];
		}
		return passFail[1];
	}
	
	public int passed(int line) {
		return count(line, true);
	}
	
	public int failed(int line) {
		return count(line, false);
	}
	
	public int countTotal(Boolean option) {
		int total = 0;
		for (Integer line : getPotentialLines()) {
			total += count(line, option);
		}
		return total;
	}
	
	public Set<Integer> getPotentialLines() {
		return lineCorverageMap.keySet();
	}
	
	public int totalFail() {
		return countTotal(false);
	}
	
	public int totalPass() {
		return countTotal(true);
	}
	
	public String getClassResourcePath() {
		return classResourcePath;
	}
}
