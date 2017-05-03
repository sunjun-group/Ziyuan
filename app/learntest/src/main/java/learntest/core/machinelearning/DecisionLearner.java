/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */
package learntest.core.machinelearning;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.analysis.data.CfgNode;
import learntest.calculator.OrCategoryCalculator;
import learntest.core.LearningMediator;
import learntest.core.data.DecisionNodeProbe;
import learntest.core.data.DecisionProbes;
import sav.common.core.Pair;
import sav.common.core.formula.Formula;
import sav.common.core.utils.Assert;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class DecisionLearner {
	protected static Logger log = LoggerFactory.getLogger(DecisionLearner.class);
	private LearningMediator mediator;
	
	private boolean usingPrecondsCache;
	
	public DecisionLearner(LearningMediator mediator) {
		this.mediator = mediator;
	}
	
	/**
	 * Each decision location has two formula, 
	 * one for true/false, and one for loop
	 * @param dataCoverage
	 */
	public void learn(List<String> labels) {
		Assert.assertTrue(CollectionUtils.isNotEmpty(labels));
		DecisionProbes decisionProbes = getDecisionProbes();
		List<DecisionNodeProbe> probes = decisionProbes.getNodeProbes();
		for (DecisionNodeProbe decisInput : probes) {
			log(decisInput);
			/* learn classifier */
			Pair<Formula, Formula> classifier = learn(decisInput, labels);
		}
	}

	/**
	 * @param decisCoveredInput inputdata of all testcases at decision nodes. 
	 * @return first formula is true/false formula, and the second formula is loop formula
	 */
	private Pair<Formula, Formula> learn(DecisionNodeProbe decisCoveredInput, List<String> labels) {
		boolean needFalse = true; // need to learn false branch.
		boolean needTrue = false; // need to learn true branch.
		if (decisCoveredInput.getNode().isInLoop()) {
			/* if begins to learn loop times data, the true branch must have been satisfied. 
			 * LLT: NOT CLEAR ENOUGH */
			needTrue = false; 
		}
		OrCategoryCalculator preconditions = getExistingPrecondition(decisCoveredInput.getNode());
		if (decisCoveredInput.isOnlyOneBranchIsCovered()) {
			decisCoveredInput = mediator.selectiveSamplingForEmpty(decisCoveredInput.getNode(), labels, 
						preconditions, null, true, false);
			/* after running, if selecting samples successful, we will have new testcases covered */
			/* if testcases still missing one branch, try another time */
			if (decisCoveredInput.isOnlyOneBranchIsCovered()) {
				decisCoveredInput = mediator.selectiveSamplingForEmpty(decisCoveredInput.getNode(), labels, 
						preconditions, null, false, false);
			}
		}
		
		/* after doing selective sampling, create formula */
		if (!usingPrecondsCache) {
			
		}
			
		return null;
	}

	/**
	 * base on mode of learning,
	 * if inherit, we try to get the existing one, otherwise, return nothing.
	 * @param node
	 * @return
	 */
	private OrCategoryCalculator getExistingPrecondition(CfgNode node) {
		if (usingPrecondsCache) {
			return getDecisionProbes().getPrecondition(node);
		}
		return null;
	}

	/**
	 * always get the coverage infor in mediator which can only be change inside
	 * mediator (for centralisation purpose)
	 * 
	 * @return
	 */
	private DecisionProbes getDecisionProbes() {
		return mediator.getDecisionProbes();
	}

	/**
	 * @param decisInput inputdata of all testcases at decision nodes. 
	 */
	private void log(DecisionNodeProbe decisInput) {
		System.out.println("true data: " + decisInput.getTrueValues());
		System.out.println("false data: " + decisInput.getFalseValues());
	}
	
}
