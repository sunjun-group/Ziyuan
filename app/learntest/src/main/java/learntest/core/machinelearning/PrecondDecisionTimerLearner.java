/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.machinelearning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import cfgcoverage.jacoco.analysis.data.CfgNode;
import learntest.core.LearningMediator;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.time.CovTimer;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.settings.SAVTimer;

/**
 * @author LLT
 *
 */
public class PrecondDecisionTimerLearner extends PrecondDecisionLearner {

	private List<Pair<Integer, Double>> timeLine;
	
	public PrecondDecisionTimerLearner(LearningMediator mediator, String logFile) {
		super(mediator, logFile);
	}

	@Override	
	public void learn(CfgNode node, DecisionProbes probes, List<Integer> visitedNodes,
			HashMap<Integer, List<CfgNode>> indexMap) throws SavException{
		CovTimer timer = new CovTimer(probes, SAVTimer.getExecutionTime());
		timer.start();		
		CfgNode startNode = node;
		try {
			while (!CovTimer.stopFlag)
				super.learn(startNode, probes, new ArrayList<Integer>(), indexMap);
		} finally {
			timer.close();
			timer.recordCovTimeLine(probes);
			timeLine = timer.getCovTimeLine();
		}
	}

	@Override
	public List<Pair<Integer, Double>> getCovTimeLine() {
		return timeLine;
	}
}
