/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.analysis.data.CfgNode;
import icsetlv.common.dto.BreakpointValue;
import learntest.calculator.OrCategoryCalculator;
import sav.strategies.dto.TestResultType;

/**
 * @author LLT
 *
 */
public class DecisionProbes extends CfgCoverage {
	private List<String> testcases;
	private Map<TestResultType, List<Integer>> testResults;
	private List<BreakpointValue> testInputs;
	
	public DecisionProbes(List<String> testcases) {
		super(null);
		this.testcases = testcases;
	}
	
	public void transferCoverage(CfgCoverage cfgCoverage) {
		setCfg(cfgCoverage.getCfg());
		setNodeCoverages(cfgCoverage.getNodeCoverages());
	}
	
	/**
	 * @param node
	 * @return
	 */
	public OrCategoryCalculator getPrecondition(CfgNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * build decisionNodeProbe
	 * @return
	 */
	public List<DecisionNodeProbe> getNodeProbes() {
		List<CfgNode> decisionNodes = getCfg().getDecisionNodes();
		List<DecisionNodeProbe> probes = new ArrayList<DecisionNodeProbe>(decisionNodes.size());
		for (CfgNode node : decisionNodes) {
			probes.add(new DecisionNodeProbe(getCoverage(node), testResults, testInputs));
		}
		return null;
	}

}
