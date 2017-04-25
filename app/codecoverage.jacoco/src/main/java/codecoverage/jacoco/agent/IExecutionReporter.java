/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package codecoverage.jacoco.agent;

import java.util.List;

import sav.common.core.SavException;

/**
 * @author LLT
 *
 */
public interface IExecutionReporter {

	void report(String execFile, String junitResultFile, List<String> testingClassNames) throws SavException;

	void setTestcases(List<String> testMethods);

}
