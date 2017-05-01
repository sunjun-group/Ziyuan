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

import org.objectweb.asm.tree.AbstractInsnNode;

import cfgcoverage.jacoco.utils.OpcodeUtils;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class CfgNode {
	public static int INVALID_IDX = -1;
	private int idx = INVALID_IDX;
	private List<CfgNode> branches;
	private List<CfgNode> predecessors;
	private boolean isInLoop;	
	private boolean isDecisionNode;
	
	private AbstractInsnNode insnNode;
	private int line;

	public CfgNode(AbstractInsnNode insnNode, int line) {
		predecessors = new ArrayList<CfgNode>();
		this.insnNode = insnNode;
		this.line = line;
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
		return isInLoop;
	}

	public void setInLoop(boolean isInLoop) {
		this.isInLoop = isInLoop;
	}

	@Override
	public String toString() {
		return "node[" + OpcodeUtils.getCode(insnNode.getOpcode()) + ",line " + line +
				(isDecisionNode ? ", decis" : "") + (isInLoop ? ", inloop" : "") + "]";
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
	
}
