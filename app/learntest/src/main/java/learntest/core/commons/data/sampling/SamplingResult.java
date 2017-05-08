/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data.sampling;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.analysis.data.CfgNode;
import icsetlv.common.dto.BreakpointValue;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.data.decision.IDecisionNode;
import learntest.testcase.data.IBreakpointData;

/**
 * @author LLT
 *
 */
public class SamplingResult {
	private List<String> sampleTestcases;
	/* after sampling, original decisionProbes is modified. */
	private DecisionProbes orgDecisionProbes;
	private int newTcsFirstIdx;
	private List<BreakpointValue> newTestInputs;
	
	public SamplingResult(DecisionProbes orgProbes) {
		this.orgDecisionProbes = orgProbes;
		newTcsFirstIdx = orgProbes.getTestcases().size();
	}

	/**
	 * @param target
	 * @return
	 */
	public IBreakpointData get(IDecisionNode target) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the orgDecisionProbes 
	 */
	public DecisionProbes getDecisionProbes() {
		return orgDecisionProbes;
	}

	/**
	 * @return
	 */
	public Map<String, CfgCoverage> getCfgCoverageMap() {
		Map<String, CfgCoverage> map = new HashMap<String, CfgCoverage>();
		map.put(orgDecisionProbes.getCfg().getId(), orgDecisionProbes);
		return map;
	}

	/**
	 * @param testInputs
	 */
	public void setNewInputData(List<BreakpointValue> testInputs) {
		this.newTestInputs = testInputs;
	}

	/**
	 * @param node
	 * @return
	 */
	public IBreakpointData getNewData(CfgNode node) {
		// TODO Auto-generated method stub
		return null;
	}
}
