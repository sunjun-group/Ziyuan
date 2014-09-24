/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.iface;

import icsetlv.common.dto.BreakPoint;

import java.util.List;

/**
 * @author LLT
 *
 */
public interface ISlicer {

	List<BreakPoint> slice(List<BreakPoint> breakpoints, List<String> junitClassNames)
			throws Exception;
	
	void setAnalyzedClasses(List<String> analyzedClasses);
}
