/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.cfg;

import learntest.breakpoint.data.DecisionBkpsData;
import learntest.main.DecisionLearner.IConditionHandler;

/**
 * @author LLT
 *
 */
public interface ICfgHandler extends IConditionHandler {
	
	DecisionBkpsData getDecisionBkpsData();
}
