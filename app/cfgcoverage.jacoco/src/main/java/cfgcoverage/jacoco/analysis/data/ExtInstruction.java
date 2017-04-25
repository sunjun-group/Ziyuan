/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.analysis.data;

import org.jacoco.core.internal.flow.Instruction;
import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * @author LLT
 *
 */
public class ExtInstruction extends Instruction {
	private CfgNode cfgNode;
	private boolean newCfg;
	private String testMethod;
	private ExtInstruction predecessor; // jacocoPredecessor
	
	public ExtInstruction(AbstractInsnNode node, int line) {
		super(node, line);
		cfgNode = new CfgNode(node, line);
		newCfg = true;
	}
	
	public ExtInstruction(CfgNode cfgNode) {
		super(cfgNode.getInsnNode(), cfgNode.getLine());
		this.cfgNode = cfgNode;
		newCfg = false;
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
			cfgNode.updateCoveredBranchesForTc(coveredBranch.cfgNode, testMethod);
		}
		if (cfgNode.getCoverage().isCovered(testMethod)) {
			// no need to update its predecessors
			return;
		}
		// otherwise, mark covered and update all its predecessors
		cfgNode.getCoverage().setCovered(testMethod, count);
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
