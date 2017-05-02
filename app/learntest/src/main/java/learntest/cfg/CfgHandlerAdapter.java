/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.cfg;

import java.util.List;
import java.util.Map;

import learntest.breakpoint.data.DecisionBkpsData;
import learntest.breakpoint.data.DecisionLocation;
import learntest.calculator.OrCategoryCalculator;
import learntest.exception.LearnTestException;
import learntest.main.LearnTestParams;
import learntest.main.model.Branch;
import learntest.testcase.data.BreakpointData;
import libsvm.core.Divider;
import sav.common.core.Pair;
import sav.common.core.formula.Formula;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT
 *
 */
public class CfgHandlerAdapter implements ICfgHandler {
	private ICfgHandler cfgHandler;
	
	public enum CfgAproach {
		SOURCE_CODE_LEVEL,
		BYTE_CODE_LEVEL
	}
	
	public CfgHandlerAdapter(AppJavaClassPath appClassPath, LearnTestParams params, CfgAproach approach)
			throws LearnTestException {
		cfgHandler = CfgHandlerScInitializer.getINSTANCE().create(params);
	}
	
	public DecisionBkpsData getDecisionBkpsData() {
		return cfgHandler.getDecisionBkpsData();
	}

	public void updateRelevance(Map<DecisionLocation, BreakpointData> bkpDataMap) {
		cfgHandler.updateRelevance(bkpDataMap);
	}

	public void setVars(List<ExecVar> vars, List<ExecVar> originVars) {
		cfgHandler.setVars(vars, originVars);
	}

	public boolean isRelevant(int lineNo) {
		return cfgHandler.isRelevant(lineNo);
	}

	public OrCategoryCalculator getPreConditions(int lineNo) {
		return cfgHandler.getPreConditions(lineNo);
	}

	public void setPrecondition(int lineNo, Pair<Formula, Formula> learnedClassifier, List<Divider> curDividers) {
		cfgHandler.setPrecondition(lineNo, learnedClassifier, curDividers);
	}

	public List<Branch> getTotalBranches() {
		return cfgHandler.getTotalBranches();
	}
	
}
