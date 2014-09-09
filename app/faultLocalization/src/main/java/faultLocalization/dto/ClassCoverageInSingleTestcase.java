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


/**
 * @author LLT
 * 
 */
public class ClassCoverageInSingleTestcase {
	private String classResourcePath;
	private Map<Integer, Boolean> lineCorverageMap;
	
	public ClassCoverageInSingleTestcase(String classResourcePath) {
		this.classResourcePath = classResourcePath;
		lineCorverageMap = new HashMap<Integer, Boolean>();
	}
	
	public void addInfo(int line, boolean isCovered) {
		lineCorverageMap.put(line, isCovered);
	}
}
