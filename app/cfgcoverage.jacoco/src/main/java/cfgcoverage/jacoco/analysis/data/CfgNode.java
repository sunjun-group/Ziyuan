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
import java.util.Set;

import org.objectweb.asm.tree.AbstractInsnNode;

import cfgcoverage.jacoco.utils.OpcodeUtils;
import sav.common.core.utils.Assert;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class CfgNode {
	public static int INVALID_IDX = -1;

	private AbstractInsnNode insnNode;
	private int idx = INVALID_IDX;
	private int line;
	
	/* if node is inside a loop, keep that loop header (should be a decision node) */
	private List<CfgNode> loopHeaders;
	private boolean isDecisionNode;

	/* branches are all children of node */
	private List<CfgNode> branches;
	/* parent nodes */
	private List<CfgNode> predecessors;
	
	/* dominatees are decision nodes which controls this node, if this node is not a decision node,
	 * then its dominatee will be the previous decision node */
	private Set<CfgNode> dominatees;
	private Set<CfgNode> loopDominatees;
	/*
	 * dependentees is only not null if node is a decision node, dependentees
	 * are decision nodes which under control of this node
	 */
	private Set<CfgNode> dependentees;
	private Set<CfgNode> loopDependentees;
	/* keep type of relationship between this node with other nodes */
	private Map<Integer, BranchRelationship> branchTypes = new HashMap<Integer, BranchRelationship>();
	
	public CfgNode(AbstractInsnNode insnNode, int line) {
		predecessors = new ArrayList<CfgNode>();
		this.insnNode = insnNode;
		this.line = line;
	}

	public void addBranch(CfgNode node, boolean falseBranch) {
		branches = CollectionUtils.initIfEmpty(branches);
		branches.add(node);
		addBranchRelationship(node, falseBranch);
	}

	private void addBranchRelationship(CfgNode node, boolean falseBranch) {
		addBranchRelationship(node.idx, BranchRelationship.valueOf(falseBranch));
	}
	
	private void addBranchRelationship(int nodeIdx, BranchRelationship branchRelationship) {
		BranchRelationship curRelationship = branchTypes.get(nodeIdx);
		branchTypes.put(nodeIdx, BranchRelationship.merge(curRelationship, branchRelationship));
	}

	public void setPredecessor(CfgNode predecessor, boolean falseBranch) {
		predecessor.addBranch(this, falseBranch);
		predecessors.add(predecessor);
		addBranchRelationship(predecessor, falseBranch);
	}

	public List<CfgNode> getBranches() {
		return branches;
	}
	
	public CfgNode getNext() {
		Assert.assertTrue(CollectionUtils.getSize(branches) <= 1, "node has more than 1 branch");
		return isLeaf() ? null : branches.get(0);
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

	public boolean isLeaf() {
		return CollectionUtils.isEmpty(branches);
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}
	
	public int getIdx() {
		return idx;
	}
	
	public boolean isDecisionNode() {
		return isDecisionNode;
	}

	public void setDecisionNode(boolean isDecisionNode) {
		this.isDecisionNode = isDecisionNode;
	}

	public boolean isInLoop() {
		return loopHeaders != null;
	}

	@Override
	public String toString() {
		return "node[" + idx + "," + OpcodeUtils.getCode(insnNode.getOpcode()) + ",line " + line +
				(isDecisionNode ? ", decis" : "") + (isInLoop() ? ", inloop" : "") + "]";
	}
	
	public boolean isLoopHeader() {
		return loopHeaders != null && loopHeaders.contains(this);
	} 

	public int getFistBlkIdxIfLoopHeader() {
		if (isLeaf()) {
			return INVALID_IDX;
		}
		for (CfgNode child : branches) {
			/* if the node try to jump backward, it must be a loop header */
			if (child.idx < idx) {
				return child.idx;
			}
		}
		return INVALID_IDX;
	}
	
	public void addLoopHeader(CfgNode loopHeader) {
		loopHeaders = CollectionUtils.initIfEmpty(loopHeaders);
		loopHeaders.add(loopHeader);
	}

	/**
	 * if we have a loop like this:
	 * 10 instr a
	 * . 
	 * 15 decision node ()	
	 * .
	 * 20 decision node (i < a) jump to 10 
	 * 
	 * then node 20 has node 15 as its loopdominatee
	 * and node 15 has node 20 as its loopdependentee
	 * 
	 * @param node
	 * @param branchType 
	 */
	public void addDominatee(CfgNode node, boolean loop, BranchRelationship branchType) {
		if (loop) {
			loopDominatees = CollectionUtils.initIfEmpty(loopDominatees);
			loopDominatees.add(node);
		} else {
			dominatees = CollectionUtils.initIfEmpty(dominatees);
			dominatees.add(node);
			addBranchRelationship(node.idx, branchType);
		}
	}
	
	public void addDependentee(CfgNode node, boolean loop, BranchRelationship branchType) {
		if (loop) {
			loopDependentees = CollectionUtils.initIfEmpty(loopDependentees);
			loopDependentees.add(node);
		} else {
			dependentees = CollectionUtils.initIfEmpty(dependentees);
			dependentees.add(node);
			addBranchRelationship(node.idx, branchType);
		}
	}
	
	public boolean isLoopHeaderOf(CfgNode node) {
		if (node.getLoopHeaders() != null && node.getLoopHeaders().contains(this)) {
			return true;
		}
		return false;
	}

	public List<CfgNode> getLoopHeaders() {
		return loopHeaders;
	}

	public Set<CfgNode> getDominatees() {
		return dominatees;
	}

	public Set<CfgNode> getLoopDominatees() {
		return loopDominatees;
	}

	public Set<CfgNode> getDependentees() {
		return dependentees;
	}

	public Set<CfgNode> getLoopDependentees() {
		return loopDependentees;
	}

	public void addChildsDependentees(CfgNode dependentee) {
		if (dependentee.getDependentees() != null) {
			for (CfgNode childDependentee : dependentee.getDependentees()) {
				addDependentee(childDependentee, false, dependentee.getBranchRelationship(childDependentee.idx));
			}
		}
	}
	
	public BranchRelationship getBranchRelationship(int nodeIdx) {
		return branchTypes.get(nodeIdx);
	}
}
