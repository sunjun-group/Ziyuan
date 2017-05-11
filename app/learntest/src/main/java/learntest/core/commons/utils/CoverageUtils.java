/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.utils;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.analysis.data.CfgNode;
import cfgcoverage.jacoco.analysis.data.NodeCoverage;

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

}
