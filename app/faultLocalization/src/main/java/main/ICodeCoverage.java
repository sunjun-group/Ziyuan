/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package main;

import java.util.List;

import faultLocalization.dto.CoverageReport;

/**
 * @author LLT
 *
 */
public interface ICodeCoverage {
	public CoverageReport run(List<String> testingClassNames,
			List<String> junitClassNames) throws Exception;
}
