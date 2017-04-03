/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.cfg.bytecode;

import java.util.List;
import java.util.Map;

import cfgextractor.CFG;
import learntest.breakpoint.data.DecisionBkpsData;
import learntest.breakpoint.data.DecisionLocation;
import learntest.calculator.OrCategoryCalculator;
import learntest.cfg.ICfgHandler;
import learntest.main.model.Branch;
import learntest.testcase.data.BreakpointData;
import libsvm.core.Divider;
import sav.common.core.Pair;
import sav.common.core.formula.Formula;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT
 *
 */
public class CfgHandler implements ICfgHandler {
	
	public CfgHandler(CFG cfg) {
		
	}

	/* (non-Javadoc)
	 * @see learntest.main.DecisionLearner.IConditionHandler#updateRelevance(java.util.Map)
	 */
	@Override
	public void updateRelevance(Map<DecisionLocation, BreakpointData> bkpDataMap) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see learntest.main.DecisionLearner.IConditionHandler#setVars(java.util.List, java.util.List)
	 */
	@Override
	public void setVars(List<ExecVar> vars, List<ExecVar> originVars) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see learntest.main.DecisionLearner.IConditionHandler#isRelevant(int)
	 */
	@Override
	public boolean isRelevant(int lineNo) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see learntest.main.DecisionLearner.IConditionHandler#getPreConditions(int)
	 */
	@Override
	public OrCategoryCalculator getPreConditions(int lineNo) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see learntest.main.DecisionLearner.IConditionHandler#setPrecondition(int, sav.common.core.Pair, java.util.List)
	 */
	@Override
	public void setPrecondition(int lineNo, Pair<Formula, Formula> learnedClassifier, List<Divider> curDividers) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see learntest.main.DecisionLearner.IConditionHandler#getTotalBranches()
	 */
	@Override
	public List<Branch> getTotalBranches() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DecisionBkpsData getDecisionBkpsData() {
		// TODO Auto-generated method stub
		return null;
	}

}
