/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.analysis.data;

import org.jacoco.core.internal.flow.Instruction;

import cfgcoverage.jacoco.utils.OpcodeUtils;

/**
 * @author LLT
 *
 */
public class ExtInstruction extends Instruction {
	private CfgNode cfgNode;
	private NodeCoverage nodeCoverage;
	private boolean newCfg;
	private int testIdx;
	private ExtInstruction predecessor; // jacocoPredecessor
	
	public ExtInstruction(CfgNode cfgNode, NodeCoverage nodeCoverage, boolean newCfg) {
		super(cfgNode.getInsnNode(), cfgNode.getLine());
		this.cfgNode = cfgNode;
		this.nodeCoverage = nodeCoverage;
		this.newCfg = newCfg;
	}
	
	public void setCovered(int count) {
		if (count > 0) {
			setCovered();
		}
		setCovered(null, count);
	}
	
	public void setCovered(ExtInstruction coveredBranch, int count) {
		if (coveredBranch != null) {
			nodeCoverage.updateCoveredBranchesForTc(coveredBranch.cfgNode, testIdx);
		}
		if (nodeCoverage.isCovered(testIdx)) {
			// no need to update its predecessors
			return;
		}
		// otherwise, mark covered and update all its predecessors
		nodeCoverage.setCovered(testIdx, count);
		if (predecessor != null) {
			predecessor.setCovered(this, count);
		}
	}
	
	public CfgNode getCfgNode() {
		return cfgNode;
	}

	public void setTestIdx(int testIdx) {
		this.testIdx = testIdx;
	}
	
	public void setPredecessor(Instruction predecessorInsn) {
		super.setPredecessor(predecessorInsn);
		this.predecessor = (ExtInstruction) predecessorInsn;
	}

	public void setNodePredecessor(ExtInstruction source) {
		setNodePredecessor(source, false);
	}
	
	public void setNodePredecessor(ExtInstruction source, boolean jump) {
		if (!newCfg) {
			return;
		}
		boolean jumpForward = jump && (source.cfgNode.getIdx() < this.cfgNode.getIdx());
		boolean falseBranch = jumpForward && OpcodeUtils.isCondition(source.cfgNode.getInsnNode().getOpcode());
		cfgNode.setPredecessor(source.cfgNode, falseBranch);
	}

}
