/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.analysis.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import sav.common.core.utils.Assert;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.TextFormatUtils;


/**
 * @author LLT
 *
 */
public class CfgCoverage {
	protected CFG cfg;
	protected List<NodeCoverage> nodeCoverages;
	protected List<String> testcases;
	protected Map<Integer, Set<Integer>> dupTcMap;
	
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
		Assert.assertTrue(node.getIdx() < nodeCoverages.size(), "node doesnot have corresponding coverage!");
		return nodeCoverages.get(node.getIdx());
	}
	
	public NodeCoverage addCoverage(CfgNode node) {
		Assert.assertTrue(node.getIdx() == nodeCoverages.size(), "node and nodeCoverages are not consistent!");
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

	public void initNodeCoveragesIfEmpty() {
		if (cfg != null && nodeCoverages.isEmpty()) {
			for (CfgNode node : cfg.getNodeList()) {
				NodeCoverage nodeCoverage = new NodeCoverage(this, node);
				nodeCoverages.add(nodeCoverage);
			}
		}
	}

	public void updateDuplicateTcs(Map<String, Set<String>> duplicatedTcs) {
		if (CollectionUtils.isEmpty(duplicatedTcs)) {
			return;
		}
		if (dupTcMap == null) {
			this.dupTcMap = new HashMap<Integer, Set<Integer>>(duplicatedTcs.size());
		}
		for (Entry<String, Set<String>> entry : duplicatedTcs.entrySet()) {
			Set<Integer> dupTcIdxies = CollectionUtils.getSetInitIfEmpty(dupTcMap, testcases.indexOf(entry.getKey()));
			for (String tc : entry.getValue()) {
				dupTcIdxies.add(testcases.indexOf(tc));
			}
		}
	}
	
	/**
	 * map can be null
	 */
	public Map<Integer, Set<Integer>> getDupTcs() {
		return dupTcMap;
	}
	
	public Set<Integer> getDupTcs(int testIdx) {
		Set<Integer> dupTcs = null;
		if (dupTcMap != null) {
			dupTcs = dupTcMap.get(testIdx);
		}
		return CollectionUtils.nullToEmpty(dupTcs);
	}

	public int getTotalTcs() {
		return testcases.size();
	}

	public void addNewTestcases(List<String> newTestcases) {
		if (newTestcases.isEmpty()) {
			return;
		}
		if (testcases == null) {
			setTestcases(newTestcases);
		} else if (!testcases.contains(newTestcases.get(0))) {
			testcases.addAll(newTestcases);
		}
	}

	public int getTestIdx(String tc) {
		return testcases.indexOf(tc);
	}
}
