/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.slicing;


import java.util.List;

import sav.strategies.dto.BreakPoint;


/**
 * @author LLT
 *
 */
public interface ISlicer {

	List<BreakPoint> slice(List<BreakPoint> breakpoints, List<String> junitClassNames)
			throws Exception;
	
	void setAnalyzedClasses(List<String> analyzedClasses);
}
