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
	private List<String> testcases;
	
	public CfgCoverage(CFG cfg) {
		this.cfg = cfg;
		nodeCoverages = new ArrayList<NodeCoverage>();
	}
	
	/* return idx of the added testcase */
	public int addTestcases(String testcase) {
		if (this.testcases == null) {
			this.testcases = new ArrayList<String>();
		}
		this.testcases.add(testcase);
		return testcases.size() - 1;
	}
	
	public void setTestcases(List<String> testcases) {
		this.testcases = testcases;
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
	
	public CFG getCfg() {
		return cfg;
	}

	public void setCfg(CFG cfg) {
		this.cfg = cfg;
	}

	public List<NodeCoverage> getNodeCoverages() {
		return nodeCoverages;
	}

	public void setNodeCoverages(List<NodeCoverage> nodeCoverages) {
		this.nodeCoverages = nodeCoverages;
	}
	
	/**
	 * @return the testcases all run testcases.
	 */
	public List<String> getTestcases() {
		return testcases;
	}

	@Override
	public String toString() {
		return "CfgCoverage \n cfg=[" + cfg + "],\n nodeCoverages=" + TextFormatUtils.printListSeparateWithNewLine(nodeCoverages) + "]";
	}

	/**
	 * in case of a false branch of a decision node is undefined,
	 * branch coverage for it will not be update in nodeCoverage (as default behavior of jacoco),
	 * so we have to do it manually for that case, 
	 * if next node after condition block is covered and true branch is not covered, then false branch is covered,
	 * otherwise if true branch is covered then false branch is of course uncovered.
	 */
	public void solveMissingBranchesCoverage() {
		for (CfgNode node : cfg.getDecisionNodes()) {
			nodeCoverages.get(node.getIdx()).solveMissingBranchesCoverage();
		}
	}

}
