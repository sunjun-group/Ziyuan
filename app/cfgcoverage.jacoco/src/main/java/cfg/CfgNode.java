/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;

import cfg.utils.ControlRelationship;
import cfg.utils.OpcodeUtils;
import sav.common.core.utils.Assert;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 * BranchRelationship, and DecisionBranchType are all about byte code level, not source code as previous design.
 */
public class CfgNode {
	public static int INVALID_IDX = -1;

	private int idx = INVALID_IDX;
	private int line;
	private AbstractInsnNode insnNode;
	private List<SwitchCase> switchCases; /* not null if node is a switch type node */
	
	private boolean isDecisionNode;
	// TODO-NICE TO HAVE: implement for convenient if necessary
	private boolean isNegated; // flag which indicates whether the logic of condition is negated by compiler.

	/* if node is inside a loop, keep that loop header (should be a decision node) */
	private List<CfgNode> loopHeaders;

	/* parent nodes */
	private List<CfgNode> predecessors;
	private List<CfgNode> children; /* direct children at only 1 level in CFG */
	
	/* dominatees are decision nodes which controls this node, if this node is not a decision node,
	 * then its dominatee will be the previous decision node */
	private Set<CfgNode> dominators = new HashSet<>();
	private Set<CfgNode> loopDominators = new HashSet<>();
	
	// FOR DECISION NODE ONLY 
	/* branches are all children of node */
	private Map<DecisionBranchType, CfgNode> decisionBranches;
	/*
	 * dependentees is only not null if node is a decision node, dependentees
	 * are decision nodes which under control of this node.
	 * 
	 */
	private Set<CfgNode> dependentees = new HashSet<>();
	private Set<CfgNode> loopDependentees = new HashSet<>();
	
	/* keep type of relationship between this node with all of its 
	 * either direct or indirect children.
	 * */
	private Map<Integer, Short> decisionControlRelationships = new HashMap<>();
	
	public CfgNode(AbstractInsnNode insnNode, int line) {
		predecessors = new ArrayList<CfgNode>(1);
		children = new ArrayList<>(1);
		this.insnNode = insnNode;
		this.line = line;
	}

	public void setPredecessor(CfgNode predecessor) {
		predecessor.children.add(this);
		predecessors.add(predecessor);
	}

	public Collection<CfgNode> getBranches() {
		return decisionBranches == null ? Collections.emptyList() : decisionBranches.values();
	}
	
	public Map<DecisionBranchType, CfgNode> getDecisionBranches() {
		return decisionBranches;
	}
	
	public CfgNode getNext() {
		if (switchCases != null) {
			System.out.println("Switch case!");
			return children.get(0);
		}
		Assert.assertTrue(CollectionUtils.getSize(children) <= 1, "node has more than 1 branch");
		return isLeaf() ? null : children.get(0);
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
		return CollectionUtils.isEmpty(children);
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
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("node[%d,%s,line %d]", idx, OpcodeUtils.getCode(insnNode.getOpcode()), line));
		if (isDecisionNode) {
			sb.append(", decis");
			if (isNegated) {
				sb.append("(neg)");
			}
			if (decisionBranches != null) {
				int trueBranchId = decisionBranches.get(DecisionBranchType.TRUE).idx;
				int falseBranchId = decisionBranches.get(DecisionBranchType.FALSE).idx;
				sb.append("{T=").append(trueBranchId)
					.append(",F=").append(falseBranchId)
					.append("}");
				sb.append("(").append(trueBranchId).append("=").append(ControlRelationship.toString(getDecisionControlRelationship(trueBranchId)))
					.append(",").append(falseBranchId).append("=").append(ControlRelationship.toString(getDecisionControlRelationship(falseBranchId)))
					.append(")");
			} 
		} else if (insnNode instanceof JumpInsnNode) {
			sb.append(", jumpTo {");
			for (int i = 0; i < children.size(); i++) {
				CfgNode branch = children.get(i);
				sb.append(branch.getIdx());
				if (i != children.size() - 1) {
					sb.append(", ");
				}
			}
			sb.append("}");
		}
		if (isLoopHeader()) {
			sb.append(", loopHeader");
		} else if (isInLoop()) {
			sb.append(", inloop");
		}
		sb.append("]");
		return sb.toString();
	}
	
	public boolean isLoopHeader() {
		return loopHeaders != null && loopHeaders.contains(this);
	} 

	/**
	 * note: in case of a loop has multiple conditions conjunction, 
	 * the loopHeader would not have a backwardJumpIdx;
	 */
	public int getBackwardJumpIdx() {
		if (isLeaf()) {
			return INVALID_IDX;
		}
		for (CfgNode child : children) {
			/* if the node try to jump backward, it might be a loop header */
			if (child.idx < idx) {
				return child.idx;
			}
		}
		return INVALID_IDX;
	}
	
	public void addLoopHeader(CfgNode loopHeader) {
		if (loopHeaders == null) {
			loopHeaders = new ArrayList<CfgNode>(3); // there is not a normal case that we have more than 3-level loop.
		}
		CollectionUtils.addIfNotNullNotExist(loopHeaders, loopHeader);
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
	public void addDominator(CfgNode node, boolean loop) {
		if (loop) {
			loopDominators = CollectionUtils.initIfEmpty(loopDominators);
			loopDominators.add(node);
		} else {
			dominators = CollectionUtils.initIfEmpty(dominators);
			dominators.add(node);
		}
	}
	
	public void addDependentee(CfgNode node, boolean loop) {
		if (loop) {
			loopDependentees = CollectionUtils.initIfEmpty(loopDependentees);
			loopDependentees.add(node);
		} else {
			dependentees = CollectionUtils.initIfEmpty(dependentees);
			dependentees.add(node);
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

	public Set<CfgNode> getDominators() {
		return dominators;
	}

	public Set<CfgNode> getLoopDominators() {
		return loopDominators;
	}

	public Set<CfgNode> getDependentees() {
		return dependentees;
	}

	public Set<CfgNode> getLoopDependentees() {
		return loopDependentees;
	}

	public List<CfgNode> findTrueFalseBranches() {
		if (decisionBranches == null) {
			return Collections.emptyList();
		}
		List<CfgNode> result = new ArrayList<CfgNode>(2);
		for (CfgNode branch : decisionBranches.values()) {
			if (ControlRelationship.isTrueFalseRelationship(getDecisionControlRelationship(branch.getIdx()))) {
				result.add(branch);
			}
		}
		return result;
	}
	
	public void setDecisionBranch(CfgNode branch, DecisionBranchType type) {
		if (this.idx == branch.idx) {
			throw new IllegalArgumentException(String.format("invalid branch: %s, \n of node: %s", branch, this));
		}
		if (decisionBranches == null) {
			decisionBranches = new HashMap<DecisionBranchType, CfgNode>();
		}
		decisionBranches.put(type, branch);
		branch.setPredecessor(this);
		decisionControlRelationships.put(branch.idx, ControlRelationship.getRelationshipToDecisionPrecessor(type));
	}
	
	public CfgNode getDecisionBranch(DecisionBranchType type) {
		if (decisionBranches == null) {
			return null;
		}
		return decisionBranches.get(type);
	}
	
	public DecisionBranchType getDecisionBranchType(int nodeIdx) {
		if (decisionBranches == null) {
			return null;
		}
		for (DecisionBranchType type : DecisionBranchType.values()) {
			CfgNode branch = getDecisionBranch(type);
			if (branch != null && (branch.getIdx() == nodeIdx)) {
				return type;
			}
		}
		return null;
	}
	
	public short getDecisionControlRelationship(int nodeIdx) {
		Short value = decisionControlRelationships.get(nodeIdx);
		return value == null ? 0 : value;
	}
	
	public void setDecisionControlRelationship(int nodeIdx, short relationship) {
		if (this.idx == nodeIdx) {
			return;
		}
		decisionControlRelationships.put(nodeIdx, relationship);
	}
	
	public Map<Integer, Short> getDecisionControlRelationships() {
		return decisionControlRelationships;
	}

	public List<SwitchCase> getSwitchCases() {
		return switchCases;
	}

	public void setSwitchCases(List<SwitchCase> switchCases) {
		this.switchCases = switchCases;
	}

	public void updateDominator(CfgNode dominator, short relationship) {
		if (relationship == ControlRelationship.PD) {
			dominators.remove(dominator);
		} else {
			dominators.add(dominator);
		}
	}

	public List<CfgNode> getChildren() {
		return children;
	}

	public BranchRelationship getBranchRelationship(int nodeIdx) {
		short decisionControlRelationship = getDecisionControlRelationship(nodeIdx);
		return ControlRelationship.getBranchRelationship(decisionControlRelationship);
	}
}
