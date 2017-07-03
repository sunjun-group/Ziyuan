/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.machinelearning;

import learntest.core.commons.data.decision.DecisionProbes;
import sav.common.core.SavException;

/**
 * @author LLT
 *
 */
public interface IInputLearner {

	DecisionProbes learn(DecisionProbes inputProbes) throws SavException;

}
