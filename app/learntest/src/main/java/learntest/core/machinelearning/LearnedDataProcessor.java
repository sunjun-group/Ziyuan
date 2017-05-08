/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.machinelearning;

import cfgcoverage.jacoco.analysis.data.CfgNode;
import learntest.core.LearningMediator;
import learntest.core.commons.data.decision.CoveredBranches;
import learntest.core.commons.data.decision.DecisionNodeProbe;
import learntest.core.commons.data.decision.DecisionProbes;
import sav.common.core.SavException;

/**
 * @author LLT
 *
 */
public class LearnedDataProcessor {
	private LearningMediator mediator;
	
	public LearnedDataProcessor(LearningMediator mediator) {
		this.mediator = mediator;
	}
	
	public DecisionProbes preprocess(DecisionProbes decisionProbes, CfgNode node)
			throws SavException {
		DecisionNodeProbe nodeProbe = decisionProbes.getNodeProbe(node);
		/*
		 * if all branches are missing, nothing we can do, and if all branches
		 * are covered, then do not need to do anything
		 */
		if (nodeProbe.areAllbranchesMissing()) {
			return decisionProbes;
		}
		
		CoveredBranches coveredType;
		int round = 1;
		DecisionProbes processedProbes = decisionProbes;
		do {
			coveredType = nodeProbe.getCoveredBranches();
			if (coveredType.isOneBranchMissing()) {
				processedProbes = mediator.selectiveSamplingForEmpty(nodeProbe, processedProbes.getAllVars(), 
						processedProbes.getPrecondition(node), null, coveredType.getOnlyOneMissingBranch(), false);
				/* update node probe */
				nodeProbe = processedProbes.getNodeProbe(node);
			}
		} while (round <= 2); // try 2 times to select sampling
		
		return processedProbes;
	}
	
	/**
	 * @param decisionProbes
	 * @param node
	 * @return 
	 */
	public DecisionProbes processData(DecisionProbes decisionProbes, CfgNode node) {
		/*
		 * Map<DecisionLocation, BreakpointData> newMap = selectiveSampling.selectDataForModel(bkpData.getLocation(),
					originVars, mcm.getDataPoints(), preconditions, mcm.getLearnedDividers());
		 * 
		 * */
		return null;
	}

	/**
	 * @param decisionProbes
	 * @param node
	 * @return 
	 */
	public DecisionProbes onBeforeLearningLoop(DecisionProbes decisionProbes, CfgNode node) {
		// TODO Auto-generated method stub
		return null;
	}
}
