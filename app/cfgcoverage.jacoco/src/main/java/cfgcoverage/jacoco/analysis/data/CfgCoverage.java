/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.analysis.data;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.utils.Assert;
import sav.common.core.utils.TextFormatUtils;


/**
 * @author LLT
 *
 */
public class CfgCoverage {
	private CFG cfg;
	private List<NodeCoverage> nodeCoverages;
	
	public CfgCoverage(CFG cfg) {
		this.cfg = cfg;
		nodeCoverages = new ArrayList<NodeCoverage>();
	}

	public CFG getCfg() {
		return cfg;
	}
	
	public NodeCoverage getCoverage(CfgNode node) {
		Assert.assertTrue(node.getIdx() < nodeCoverages.size(), "");
		return nodeCoverages.get(node.getIdx());
	}
	
	public NodeCoverage addCoverage(CfgNode node) {
		Assert.assertTrue(node.getIdx() == nodeCoverages.size(), "");
		NodeCoverage nodeCoverage = new NodeCoverage(this, node);
		nodeCoverages.add(nodeCoverage);
		return nodeCoverage;
	}

	@Override
	public String toString() {
		return "CfgCoverage \n cfg=[" + cfg + "],\n nodeCoverages=" + TextFormatUtils.printListSeparateWithNewLine(nodeCoverages) + "]";
	}
	
	
}
