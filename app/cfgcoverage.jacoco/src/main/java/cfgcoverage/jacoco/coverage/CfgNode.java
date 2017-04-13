/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.coverage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.tree.AbstractInsnNode;

import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class CfgNode {
	private List<CfgNode> branches;
	private Map<String, List<CfgNode>> coveredBranches;
	private List<CfgNode> predecessors;
	
	private AbstractInsnNode insnNode;
	private int line;
	
	private NodeCoverage coverage;

	public CfgNode(AbstractInsnNode insnNode, int line) {
		predecessors = new ArrayList<CfgNode>();
		coverage = new NodeCoverage();
		coveredBranches = new HashMap<String, List<CfgNode>>();
		this.insnNode = insnNode;
		this.line = line;
	}

	public void markCovered(CfgNode coveredBranch, String testMethod) {
		if (coveredBranch != null) {
			updateCoveredBranchesForTc(coveredBranch, testMethod);
		}
		
		if (coverage.isCovered(testMethod)) {
			// no need to update its predecessors
			return;
		}
		// otherwise, mark covered and update all its predecessors
		coverage.setCovered(testMethod);
		for (CfgNode predecessor : predecessors) {
			predecessor.markCovered(this, testMethod);
		}
	}

	private void updateCoveredBranchesForTc(CfgNode coveredBranch, String testMethod) {
		List<CfgNode> coveredBranchesOnTc = coveredBranches.get(testMethod);
		if (CollectionUtils.isEmpty(coveredBranchesOnTc)) {
			coveredBranchesOnTc = new ArrayList<CfgNode>();
			coveredBranches.put(testMethod, coveredBranchesOnTc);
		}
		coveredBranchesOnTc.add(coveredBranch);
	}

	public void addBranch(CfgNode node) {
		branches = CollectionUtils.initIfEmpty(branches);
		branches.add(node);
	}

	public void setPredecessor(CfgNode predecessor) {
		predecessor.addBranch(this);
		predecessors.add(predecessor);
	}

	public List<CfgNode> getBranches() {
		return branches;
	}

	public List<CfgNode> getPredecessors() {
		return predecessors;
	}

	public AbstractInsnNode getInsnNode() {
		return insnNode;
	}

	public int getLine() {
		return line;
	}

	public NodeCoverage getCoverage() {
		return coverage;
	}
	
	public boolean hasBranch() {
		return CollectionUtils.isEmpty(branches);
	}
}
