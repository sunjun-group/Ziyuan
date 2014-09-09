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
import java.util.HashMap;
import java.util.Map;

/**
 * @author khanh
 *
 */
public class ClassCoverageInAllTestcases {
	private String classResourcePath;
	private HashMap<Integer, LineCoverageInfo> mapLines2CoverageInfo = new HashMap<Integer, LineCoverageInfo>();
	
	public ClassCoverageInAllTestcases(String classResourcePath) {
		this.classResourcePath = classResourcePath;
	}
		
	public String getClassResourcePath() {
		return classResourcePath;
	}
	
	public void addInfo(int lineIndex, int testcaseIndex, boolean isPassed){
		LineCoverageInfo lineCoverage;
		if(mapLines2CoverageInfo.containsKey(lineIndex)){
			lineCoverage = mapLines2CoverageInfo.get(lineIndex);
		}else{
			lineCoverage = new LineCoverageInfo(classResourcePath, lineIndex);
			mapLines2CoverageInfo.put(lineIndex, lineCoverage);
		}
		
		lineCoverage.addInfo(testcaseIndex, isPassed);
		
	}
	
	public Collection<LineCoverageInfo> getLineCoverageInfo(){
		return mapLines2CoverageInfo.values();
	}
}
