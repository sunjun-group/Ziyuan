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
import learntest.core.commons.data.DecisionNodeProbe;
import learntest.core.commons.data.DecisionProbes;
import learntest.testcase.data.BranchType;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.formula.Formula;
import sav.common.core.utils.Assert;
import sav.common.core.utils.CollectionUtils;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.dto.execute.value.ExecVar;

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
	 * @throws SAVExecutionTimeOutException 
	 * @throws SavException 
	 */
	public void learn(List<ExecVar> orgVars) throws SavException, SAVExecutionTimeOutException {
		Assert.assertTrue(CollectionUtils.isNotEmpty(orgVars));
		DecisionProbes decisionProbes = getDecisionProbes();
		List<DecisionNodeProbe> probes = decisionProbes.getNodeProbes();
		for (DecisionNodeProbe decisInput : probes) {
			log(decisInput);
			/* learn classifier */
			Pair<Formula, Formula> classifier = learn(decisInput, orgVars);
		}
	}

	/**
	 * @param decisCoveredInput inputdata of all testcases at decision nodes. 
	 * @return first formula is true/false formula, and the second formula is loop formula
	 * @throws SAVExecutionTimeOutException 
	 * @throws SavException 
	 */
	private Pair<Formula, Formula> learn(DecisionNodeProbe decisCoveredInput, List<ExecVar> originVars) throws SavException, SAVExecutionTimeOutException {
		boolean needFalse = true; // need to learn false branch.
		boolean needTrue = false; // need to learn true branch.
		if (decisCoveredInput.getNode().isInLoop()) {
			/* if begins to learn loop times data, the true branch must have been satisfied. 
			 * LLT: NOT CLEAR ENOUGH */
			needTrue = false; 
		}
		OrCategoryCalculator preconditions = getExistingPrecondition(decisCoveredInput.getNode());
		BranchType missingBranch;
		if ((missingBranch = decisCoveredInput.getMissingBranch()) != null) {
			decisCoveredInput = mediator.selectiveSamplingForEmpty(decisCoveredInput, originVars, 
						preconditions, null, missingBranch, false);
			/* after running, if selecting samples successful, we will have new testcases covered */
			/* if testcases still missing one branch, try another time */
			if ((missingBranch = decisCoveredInput.getMissingBranch()) != null) {
				decisCoveredInput = mediator.selectiveSamplingForEmpty(decisCoveredInput, originVars, 
						preconditions, null, missingBranch, false);
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
