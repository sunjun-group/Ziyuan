/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.analysis.data;

import org.jacoco.core.internal.flow.Instruction;

/**
 * @author LLT
 *
 */
public class ExtInstruction extends Instruction {
	private CfgNode cfgNode;
	private NodeCoverage nodeCoverage;
	private boolean newCfg;
	private String testMethod;
	private ExtInstruction predecessor; // jacocoPredecessor
	
	public ExtInstruction(CfgNode cfgNode, NodeCoverage nodeCoverage, boolean newCfg) {
		super(cfgNode.getInsnNode(), cfgNode.getLine());
		this.cfgNode = cfgNode;
		this.nodeCoverage = nodeCoverage;
		this.newCfg = newCfg;
	}
	
	public void setPredecessor(Instruction predecessorInsn) {
		super.setPredecessor(predecessorInsn);
		this.predecessor = (ExtInstruction) predecessorInsn;
	}
	
	public void setCovered(int count) {
		if (count > 0) {
			setCovered();
		}
		setCovered(null, count);
	}
	
	public void setCovered(ExtInstruction coveredBranch, int count) {
		if (coveredBranch != null) {
			nodeCoverage.updateCoveredBranchesForTc(coveredBranch.cfgNode, testMethod);
		}
		if (nodeCoverage.isCovered(testMethod)) {
			// no need to update its predecessors
			return;
		}
		// otherwise, mark covered and update all its predecessors
		nodeCoverage.setCovered(testMethod, count);
		if (predecessor != null) {
			predecessor.setCovered(this, count);
		}
	}
	
	public CfgNode getCfgNode() {
		return cfgNode;
	}

	public void setTestcase(String testMethod) {
		this.testMethod = testMethod;
	}

	public void setNodePredecessor(ExtInstruction source) {
		if (newCfg) {
			cfgNode.setPredecessor(source.cfgNode);
		}
	}
}
