/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core;

import learntest.main.LearnTestParams;
import learntest.main.RunTimeInfo;

/**
 * @author LLT
 *
 */
public interface ILearnTestSolution {
	public RunTimeInfo run(LearnTestParams params) throws Exception;
}
