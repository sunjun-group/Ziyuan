/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */
package learntest.core.machinelearning;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.analysis.data.CfgNode;
import icsetlv.common.utils.BreakpointDataUtils;
import learntest.calculator.OrCategoryCalculator;
import learntest.core.LearningMediator;
import learntest.core.commons.data.decision.DecisionNodeProbe;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.sampling.jacop.StoreSearcher;
import learntest.testcase.data.BranchType;
import libsvm.core.Divider;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.formula.Formula;
import sav.common.core.utils.CollectionUtils;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;

/**
 * @author LLT
 *
 */
public class DecisionLearner {
	protected static Logger log = LoggerFactory.getLogger(DecisionLearner.class);
	private LearningMediator mediator;
	
	private boolean usingPrecondsCache;
	
	/* LLT: temporary keep */
	private List<Divider> curDividers;
	
	public DecisionLearner(LearningMediator mediator) {
		this.mediator = mediator;
	}
	
	/**
	 * Each decision location has two formula, 
	 * one for true/false, and one for loop
	 * 
	 * @throws SAVExecutionTimeOutException, SavException 
	 */
	public void learn() throws SavException, SAVExecutionTimeOutException {
		DecisionProbes decisionProbes = getDecisionProbes();
		List<ExecVar> orgVars = BreakpointDataUtils.collectAllVars(decisionProbes.getTestInputs());
		if (CollectionUtils.isEmpty(orgVars)) {
			return;
		}
		
		List<ExecVar> polyClassifierVars = createPolyClassifierVars(orgVars);
		StoreSearcher.length = orgVars.size();
		List<String> labels = BreakpointDataUtils.extractLabels(orgVars);
		List<DecisionNodeProbe> probes = decisionProbes.getNodeProbes();
		for (DecisionNodeProbe decisInput : probes) {
			if (decisInput.isAllbranchesMissing()) {
				/* log and ignore */
				continue;
			}
			
			log(decisInput);
			/* learn classifier, after learning we will have curDividers and decisionProbes updated */
			Pair<Formula, Formula> classifier = learn(decisInput, orgVars);
			
			
			decisInput.setPrecondition(classifier, curDividers);
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
			/* if begins to learn loop times data, the true branch must have been satisfied. */
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
	 * create new variables for polynomial classification
	 * @param orgVars 
	 * @return 
	 */
	private List<ExecVar> createPolyClassifierVars(List<ExecVar> orgVars) {
		List<ExecVar> polyClassifierVars = new ArrayList<ExecVar>(orgVars);
		int size = orgVars.size();
		for (int i = 0; i < size; i++) {
			ExecVar var = orgVars.get(i);
			for (int j = i; j < size; j++) {
				polyClassifierVars.add(new ExecVar(var.getLabel() + " * " + orgVars.get(j).getLabel(), 
						ExecVarType.INTEGER));
			}
		}
		return polyClassifierVars;
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
