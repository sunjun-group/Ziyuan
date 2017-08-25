/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.analysis.data;

import java.util.Set;

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
	private ExtInstruction nextNode;
	
	public ExtInstruction(CfgNode cfgNode, NodeCoverage nodeCoverage, boolean newCfg) {
		super(cfgNode.getInsnNode(), cfgNode.getLine());
		this.cfgNode = cfgNode;
		this.nodeCoverage = nodeCoverage;
		this.newCfg = newCfg;
	}
	
	public void setCovered(int count, boolean multitargetJumpSource) {
		if (count > 0) {
			setCovered();
		}
		/* for the case of multitargetJumpSource, we don't update covered branch right away, but
		 * wait until all other probes are set covered, then update its true covered branch
		 * to get the correct coverage infor for specific branch
		 *  */
		if (!multitargetJumpSource && nextNode != null) {
			nodeCoverage.updateCoveredBranchesForTc(nextNode.cfgNode, testIdx);
		}
		setCovered(null, count);
	}
	
	public void updateTrueBranchCvgInCaseMultitargetJumpSources() {
		if (nextNode != null) {
			Set<Integer> coverTcs = nextNode.nodeCoverage.getUndupCoveredTcs().keySet();
			for (Integer coverTc : coverTcs) {
				nodeCoverage.updateCoveredBranchesForTc(nextNode.cfgNode, coverTc);
			}
		}
	}

	public void updateFalseBranchCvgInCaseMultitargetJumpSources() {
		CfgNode trueFalseBranch = cfgNode.findBranch(BranchRelationship.TRUE_FALSE);
		if (!nodeCoverage.isCovered(testIdx) || trueFalseBranch == null) {
			return;
		}
		CfgNode trueBranch = cfgNode.findBranch(BranchRelationship.TRUE);
		int trueCoveredFreq = getCoveredFreq(nodeCoverage.getCfgCoverage(), trueBranch, testIdx);
		int nodeCoveredFreq = nodeCoverage.getCoveredFreq(testIdx);
		if (nodeCoveredFreq - trueCoveredFreq > 0) {
			nodeCoverage.updateCoveredBranchesForTc(trueFalseBranch, testIdx);
		}
	}
	
	private static int getCoveredFreq(CfgCoverage cfgCoverage, CfgNode branch, int testIdx) {
		if (branch == null) {
			return 0;
		}
		return cfgCoverage.getCoverage(branch).getCoveredFreq(testIdx);
	}
	
	private void setCovered(ExtInstruction coveredBranch, int count) {
		if (coveredBranch != null) {
			nodeCoverage.updateCoveredBranchesForTc(coveredBranch.cfgNode, testIdx);
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
		source.nextNode = this;
		setNodePredecessor(source, false, false);
	}
	
	public void setNodePredecessor(ExtInstruction source, boolean jump, boolean multiTarget) {
		if (!newCfg) {
			return;
		}
		/* jump to a multitarget label */
		if (jump && multiTarget) {
			cfgNode.setPredecessor(source.cfgNode, BranchRelationship.TRUE_FALSE);
			return;
		}
		BranchRelationship branchRelationship = BranchRelationship.TRUE;
		boolean jumpForward = jump && (source.cfgNode.getIdx() < this.cfgNode.getIdx());
		if (jumpForward && OpcodeUtils.isCondition(source.cfgNode.getInsnNode().getOpcode())) {
			branchRelationship = BranchRelationship.FALSE;
		}
		
		cfgNode.setPredecessor(source.cfgNode, branchRelationship);
	}

	@Override
	public String toString() {
		return cfgNode.toString();
	}
}
