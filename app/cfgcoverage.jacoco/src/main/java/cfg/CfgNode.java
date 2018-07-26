/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfg;

import java.util.ArrayList;
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
	
	/* if node is inside a loop, keep that loop header (should be a decision node) */
	private List<CfgNode> loopHeaders;

	/* parent nodes */
	private List<CfgNode> predecessors;
	private List<CfgNode> branches;
	
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
	 */
	private Set<CfgNode> dependentees = new HashSet<>();
	private Set<CfgNode> loopDependentees = new HashSet<>();
	
	/* keep type of relationship between this node with all of its 
	 * either direct or indirect children.
	 * */
	private Map<CfgNode, Short> decisionControlRelationships = new HashMap<>();
	private CFG subCfg;
	private CFG cfg;
	
	
	private boolean isLoopHeader;
	
	public CfgNode(AbstractInsnNode insnNode, int line) {
		predecessors = new ArrayList<CfgNode>(1);
		branches = new ArrayList<>(1);
		this.insnNode = insnNode;
		this.line = line;
	}

	public void setPredecessor(CfgNode predecessor) {
		predecessor.branches.add(this);
		predecessors.add(predecessor);
	}

	public List<CfgNode> getBranches() {
		return branches;
	}
	
	public Map<DecisionBranchType, CfgNode> getDecisionBranches() {
		return decisionBranches;
	}
	
	public CfgNode getNext() {
		if (switchCases != null) {
			System.out.println("Switch case!");
			return branches.get(0);
		}
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

	void setIdx(int idx) {
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
			if (decisionBranches != null) {
				CfgNode trueBranch = decisionBranches.get(DecisionBranchType.TRUE);
				CfgNode falseBranch = decisionBranches.get(DecisionBranchType.FALSE);
				sb.append("{T=").append(trueBranch.idx)
					.append(",F=").append(falseBranch.idx)
					.append("}");
				sb.append("(").append(trueBranch.idx).append("=").append(ControlRelationship.toString(getDecisionControlRelationship(trueBranch)))
					.append(",").append(falseBranch.idx).append("=").append(ControlRelationship.toString(getDecisionControlRelationship(falseBranch)))
					.append(")");
			} else {
				sb.append("{");
				for (int i = 0; i < branches.size(); i++) {
					CfgNode branch = branches.get(i);
					sb.append(i).append("=").append(branch.getIdx());
					if (i != branches.size() - 1) {
						sb.append(", ");
					}
				}
				sb.append("}, (");
				for (int i = 0; i < branches.size(); i++) {
					CfgNode branch = branches.get(i);
					sb.append(branch.getIdx()).append("=").append(ControlRelationship
							.toString(getDecisionControlRelationship(branch), getBranchTotal()));
					if (i != branches.size() - 1) {
						sb.append(", ");
					}
				}
				sb.append(")");
			}
		} else if (insnNode instanceof JumpInsnNode) {
			sb.append(", jumpTo {");
			for (int i = 0; i < branches.size(); i++) {
				CfgNode branch = branches.get(i);
				sb.append(branch.getIdx());
				if (i != branches.size() - 1) {
					sb.append(", ");
				}
			}
			sb.append("}");
		} else if (getNext() != null){
			sb.append(", nextNode {").append(getNext().idx).append("}");
		}
		if (isLoopHeader()) {
			sb.append(", loopHeader");
		} else if (isInLoop()) {
			sb.append(", inloop");
		}
		sb.append("]");
		return sb.toString();
	}
	
	public String getFullString() {
		StringBuilder sb = new StringBuilder();
		sb.append(toString());
		sb.append(", dominators: {");
		int i = 0;
		for (CfgNode dominator : dominators) {
			sb.append(dominator.idx);
			if (++i != dominators.size()) {
				sb.append(", ");
			}
		}
		sb.append("}");
		sb.append(", dependentees: {");
		i = 0;
		for (CfgNode dependentee : CollectionUtils.nullToEmpty(dependentees)) {
			sb.append(dependentee.idx);
			if (++i != dependentees.size()) {
				sb.append(", ");
			}
		}
		sb.append("}");
		if (isLoopHeader) {
			sb.append(", isLoopHeader");
		}
//		if (isLoopHeader) {
//			sb.append(", isLoopHeaderOf { ");
//			sb.append(StringUtils.join(inLoopNode, ", ")).append("}");
//		}
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
		for (CfgNode child : branches) {
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
	
	public void addBranch(CfgNode branch) {
		branch.setPredecessor(this);
	}

	public void setDecisionBranch(CfgNode branch, DecisionBranchType type) {
		if (this.idx == branch.idx) {
			throw new IllegalArgumentException(String.format("invalid branch: %s, \n of node: %s", branch, this));
		}
		if (decisionBranches == null) {
			decisionBranches = new HashMap<DecisionBranchType, CfgNode>();
		}
		decisionBranches.put(type, branch);
		/* set precessor */
		branch.predecessors.add(this);
		if (branches.isEmpty()) {
			branches = new ArrayList<>(2);
			for (int i = 0; i < 2; i++) {
				branches.add(null);
			}
		}
		branches.set(type.ordinal(), branch);
		decisionControlRelationships.put(branch, ControlRelationship.getRelationshipToDecisionPrecessor(type));
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
	
	public short getDecisionControlRelationship(CfgNode node) {
		Short value = decisionControlRelationships.get(node);
		return value == null ? 0 : value;
	}
	
	public void setDecisionControlRelationship(CfgNode node, short relationship) {
		if (this.idx == node.idx) {
			return;
		}
		decisionControlRelationships.put(node, relationship);
	}
	
	public Map<CfgNode, Short> getDecisionControlRelationships() {
		return decisionControlRelationships;
	}

	public List<SwitchCase> getSwitchCases() {
		return switchCases;
	}

	public void setSwitchCases(List<SwitchCase> switchCases) {
		this.switchCases = switchCases;
	}

	public void updateDominator(CfgNode dominator, short relationship) {
		if (ControlRelationship.isPostDominance(relationship, dominator.getBranchTotal())) {
			dominators.remove(dominator);
		} else {
			dominators.add(dominator);
		}
	}

	public BranchRelationship getBranchRelationship(CfgNode node) {
		short decisionControlRelationship = getDecisionControlRelationship(node);
		return ControlRelationship.getBranchRelationship(decisionControlRelationship);
	}

	public int getBranchTotal() {
		return getBranches().size();
	}

	public void setSubCfg(CFG subCfg) {
		this.subCfg = subCfg;
	}
	
	public CFG getSubCfg() {
		return subCfg;
	}

	public void setLoopHeader(boolean isLoopHeader) {
		this.isLoopHeader = isLoopHeader;
	}

	public CFG getCfg() {
		return cfg;
	}

	public void setCfg(CFG cfg) {
		this.cfg = cfg;
	}
}
