/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.utils;

import java.util.ArrayList;
import java.util.List;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.analysis.data.CfgNode;
import cfgcoverage.jacoco.analysis.data.NodeCoverage;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class CfgCoverageUtils {

	public static List<NodeCoverage> getDecisionNodeCoverages(CfgCoverage cfgCoverage) {
		List<CfgNode> decisionNodes = cfgCoverage.getCfg().getDecisionNodes();
		List<NodeCoverage> coverages = new ArrayList<NodeCoverage>(decisionNodes.size());
		for (CfgNode node : decisionNodes) {
			coverages.add(cfgCoverage.getCoverage(node));
		}
		return coverages;
	}

	/**
	 * @param decisionCoverages
	 * @return
	 */
	public static boolean notAnyDecisionNodeIsCovered(List<NodeCoverage> decisionCoverages) {
		if (CollectionUtils.isEmpty(decisionCoverages)) {
			return true;
		}
		for (NodeCoverage nodeCvg : decisionCoverages) {
			if (!nodeCvg.getCoveredTcs().isEmpty()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param coverage
	 * @return
	 */
	public static boolean isMethodNotCovered(CfgCoverage coverage) {
		if (coverage == null || CollectionUtils.isEmpty(coverage.getNodeCoverages())) {
			return true;
		}
		return coverage.getNodeCoverages().get(0).getCoveredTcs().isEmpty();
	}
}
