/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data.decision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.analysis.data.CfgNode;
import icsetlv.common.dto.BreakpointValue;
import learntest.calculator.OrCategoryCalculator;
import sav.common.core.utils.CollectionUtils;
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

	/* TODO LLT: cache the node list, but be careful with the update */
	/**
	 * build decisionNodeProbe
	 * @return
	 */
	private List<DecisionNodeProbe> probes;
	public List<DecisionNodeProbe> getNodeProbes() {
		if (probes == null) {
			Map<Integer, DecisionNodeProbe> cfgNodeProbeMap = new HashMap<Integer, DecisionNodeProbe>();
			List<CfgNode> decisionNodes = getCfg().getDecisionNodes();
			probes = new ArrayList<DecisionNodeProbe>(decisionNodes.size());
			for (CfgNode node : decisionNodes) {
				DecisionNodeProbe nodeProbe = new DecisionNodeProbe(getCoverage(node), testResults, testInputs);
				probes.add(nodeProbe);
				cfgNodeProbeMap.put(node.getIdx(), nodeProbe);
			}
			
			/* update node dominatees */
			for (DecisionNodeProbe nodeProbe : probes) {
				Set<CfgNode> nodeDominatees = nodeProbe.getNode().getDominatees();
				List<DecisionNodeProbe> dominatees = new ArrayList<DecisionNodeProbe>(
						CollectionUtils.getSize(nodeDominatees));
				if (nodeDominatees != null) {
					for (CfgNode node : nodeDominatees) {
						dominatees.add(cfgNodeProbeMap.get(node.getIdx()));
					}
				}
				nodeProbe.setDominatees(dominatees);
			}
		}
		return probes;
	}
	
	public List<BreakpointValue> getTestInputs() {
		return testInputs;
	}

}
