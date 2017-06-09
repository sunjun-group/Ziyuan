/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.analysis.data.CfgNode;
import cfgcoverage.jacoco.analysis.data.NodeCoverage;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class CoverageUtils {
	private CoverageUtils() {}

	/**
	 * we only need to check the first node of cfg to know if the cfg is covered.
	 */
	public static boolean notCoverAtAll(CfgCoverage cfgcoverage) {
		NodeCoverage nodeCvg = cfgcoverage.getCoverage(cfgcoverage.getCfg().getStartNode());
		if(nodeCvg.getCoveredTcs().isEmpty()) {
			return true;
		}
		return false;
	}

	public static boolean noDecisionNodeIsCovered(CfgCoverage cfgcoverage) {
		for (CfgNode node : cfgcoverage.getCfg().getDecisionNodes()) {
			if (!cfgcoverage.getCoverage(node).getCoveredTcs().isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	public static double calculateCoverageByBranch(CfgCoverage cfgCoverage) {
		int totalBranches = 0;
		int coveredBranches = 0;
		List<CfgNode> decisionNodes = cfgCoverage.getCfg().getDecisionNodes();
		if (decisionNodes.isEmpty()) {
			if (cfgCoverage.getCoverage(cfgCoverage.getCfg().getStartNode()).getCoveredTcs().isEmpty()) {
				return 0.0;
			}
			return 1.0;
		}
		for (CfgNode node : decisionNodes) {
			totalBranches += CollectionUtils.getSize(node.getBranches());
			coveredBranches += cfgCoverage.getCoverage(node).getCoveredBranches().size();
		}
		return coveredBranches / (double) totalBranches;
	}
	
	public static double calculateCoverage(CfgCoverage cfgCoverage) {
		int covered = 0;
		for (NodeCoverage nodeCvg : cfgCoverage.getNodeCoverages()) {
			if (!nodeCvg.getCoveredTcs().isEmpty()) {
				covered++;
			}
		}
		double coverage = covered / (double) cfgCoverage.getCfg().getNodeList().size();
		return Math.round (coverage * 100.0) / 100.0;
	}
	
	/**
	 * build a coverage map from one single cfg coverage.
	 */
	public static Map<String, CfgCoverage> getCfgCoverageMap(CfgCoverage cfgCoverage) {
		Map<String, CfgCoverage> map = new HashMap<String, CfgCoverage>();
		map.put(cfgCoverage.getCfg().getId(), cfgCoverage);
		return map;
	}
	
}
