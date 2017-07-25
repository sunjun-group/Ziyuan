/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.backup.cfg;

import learntest.backup.breakpoint.data.DecisionBkpsData;
import learntest.backup.main.DecisionLearner.IConditionHandler;

/**
 * @author LLT
 *
 */
public interface ICfgHandler extends IConditionHandler {
	
	DecisionBkpsData getDecisionBkpsData();
}
