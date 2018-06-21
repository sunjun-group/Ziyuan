/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.analysis.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jacoco.core.internal.flow.Instruction;

import cfg.CfgNode;
import cfg.DecisionBranchType;
import cfg.utils.ControlRelationship;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class ExtInstruction extends Instruction {
	private CfgNode cfgNode;
	private NodeCoverage nodeCoverage;
	private int testIdx;
	private ExtInstruction predecessor; // jacocoPredecessor
	private ExtInstruction nextNode;
	
	public ExtInstruction(CfgNode cfgNode, NodeCoverage nodeCoverage) {
		super(cfgNode.getInsnNode(), cfgNode.getLine());
		this.cfgNode = cfgNode;
		this.nodeCoverage = nodeCoverage;
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
	
	public void updateNextBranchCvgInCaseMultitargetJumpSources() {
		if (nextNode != null) {
			updateBranchCvg(nextNode);
		}
	}

	public void updateBranchCvg(ExtInstruction branchInsn) {
		Set<Integer> coverTcs = branchInsn.nodeCoverage.getUndupCoveredTcs().keySet();
		for (Integer coverTc : coverTcs) {
			if (nodeCoverage.isCovered(coverTc)) {
				nodeCoverage.updateCoveredBranchesForTc(branchInsn.cfgNode, coverTc);
			}
		}
	}

	/**
	 * in normal cases which only have atmost one of branches of a node point to a multitarget node,
	 * we can count exactly coverage of TRUE_FALSE branch by extracting coverage of TRUE node.
	 * but in special cases where both branches of a node point to multitarget node,
	 * there is no way we can distinguish coverage count for each branch, 
	 * so we will leave the max count for such cases which is of course lead to a potential bug in cases we don't know. 
	 * [sadly, we only can do as best as we can here]
	 */
	public void updateTargetBranchCvgInCaseMultitargetJumpSources() {
		List<CfgNode> trueFalseBranches = findTrueFalseBranches(cfgNode);
		if (!nodeCoverage.isCovered(testIdx) || CollectionUtils.isEmpty(trueFalseBranches)) {
			return;
		}
		CfgNode falseBranch = cfgNode.getDecisionBranch(DecisionBranchType.FALSE);
		int falseCoveredFreq = getCoveredFreq(nodeCoverage.getCfgCoverage(), falseBranch, testIdx);
		int nodeCoveredFreq = nodeCoverage.getCoveredFreq(testIdx);
		for (CfgNode trueFalseBranch : trueFalseBranches) {
			int trueFalseBranchCvg = getCoveredFreq(nodeCoverage.getCfgCoverage(), trueFalseBranch, testIdx);
			if (nodeCoveredFreq - falseCoveredFreq > 0 && trueFalseBranchCvg > 0) {
				nodeCoverage.updateCoveredBranchesForTc(trueFalseBranch, testIdx);
			}
		}
	}
	
	private List<CfgNode> findTrueFalseBranches(CfgNode node) {
		if (node.getBranches() == null) {
			return Collections.emptyList();
		}
		List<CfgNode> result = new ArrayList<CfgNode>(2);
		for (CfgNode branch : node.getBranches()) {
			if (ControlRelationship.isTrueFalseRelationship(node.getDecisionControlRelationship(branch))) {
				result.add(branch);
			}
		}
		return result;
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
	}
	
	@Override
	public String toString() {
		return cfgNode.toString();
	}
}
