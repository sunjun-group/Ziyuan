/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core;

import learntest.core.commons.data.LineCoverageResult;
import learntest.core.commons.data.classinfo.TargetMethod;

/**
 * @author LLT
 *
 */
public class EmptyRuntimeInfo extends RunTimeInfo {

	public EmptyRuntimeInfo(TargetMethod targetMethod) {
		LineCoverageResult lineCoverageResult = new LineCoverageResult(targetMethod.getMethodInfo());
		setLineCoverageResult(lineCoverageResult);
		setCoverageInfo("");
	}
	
	@Override
	public boolean isEmpty() {
		return true;
	}
}
