/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.machinelearning;

import cfgcoverage.jacoco.analysis.data.CfgNode;
import learntest.calculator.OrCategoryCalculator;
import learntest.core.commons.data.decision.DecisionNodeProbe;
import learntest.core.commons.data.decision.DecisionProbes;
import sav.settings.SAVExecutionTimeOutException;

/**
 * @author LLT
 *	only do sampling randomly, not based on precondition.
 */
public class DecisionLearner extends PrecondDecisionLearner {

	@Override
	protected void updatePrecondition(DecisionNodeProbe nodeProbe) throws SAVExecutionTimeOutException {
		/* do nothing */
	}
	
	@Override
	protected OrCategoryCalculator getPreconditions(DecisionProbes probes, CfgNode node) {
		return null;
	}
}
