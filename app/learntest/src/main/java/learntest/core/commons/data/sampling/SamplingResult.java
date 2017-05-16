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
import learntest.core.commons.data.decision.DecisionNodeProbe;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.data.decision.IDecisionNode;
import learntest.core.commons.data.decision.NodeCoveredData;
import learntest.core.machinelearning.iface.ISampleResult;
import learntest.testcase.data.INodeCoveredData;

/**
 * @author LLT
 * work on DecisionNodeProbe
 */
public class SamplingResult implements ISampleResult {
	/* modified version of original probes (the probes will be modified directly in sampling execution). */
	private DecisionProbes decisionProbes;
	private int newTcsFirstIdx;
	private List<BreakpointValue> newTestInputs;
	
	/* cache data */
	private Map<Integer, INodeCoveredData> dataMap = new HashMap<Integer, INodeCoveredData>();
	
	public SamplingResult(DecisionProbes orgProbes) {
		this.decisionProbes = orgProbes;
		newTcsFirstIdx = orgProbes.getTestcases().size();
	}
	
	/**
	 * @param target
	 * @return
	 */
	public INodeCoveredData getNewData(IDecisionNode target) {
		CfgNode node = ((DecisionNodeProbe) target).getNode();
		INodeCoveredData bkpData = dataMap.get(node.getIdx());
		if (bkpData != null) {
			return bkpData;
		}
		/* build input data for new coverage */
		bkpData = new NodeCoveredData(decisionProbes.getCoverage(node), newTestInputs, newTcsFirstIdx);
		dataMap.put(node.getIdx(), bkpData);
		return bkpData;
	}

	/**
	 * @return the orgDecisionProbes 
	 */
	public DecisionProbes getDecisionProbes() {
		return decisionProbes;
	}

	/**
	 * @return
	 */
	public Map<String, CfgCoverage> getCfgCoverageMap() {
		Map<String, CfgCoverage> map = new HashMap<String, CfgCoverage>();
		map.put(decisionProbes.getCfg().getId(), decisionProbes);
		return map;
	}

	/**
	 * @param testInputs
	 */
	public void setNewInputData(List<BreakpointValue> testInputs) {
		this.newTestInputs = testInputs;
	}
	
	/**
	 * clear cache data 
	 */
	public void updateNewData() {
		decisionProbes.clearCaches();
		decisionProbes.addNewTestInputs(newTestInputs);
		dataMap.clear();
	}
}
