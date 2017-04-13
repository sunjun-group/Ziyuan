/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco;

import org.jacoco.core.internal.flow.Instruction;
import org.objectweb.asm.tree.AbstractInsnNode;

import cfgcoverage.jacoco.coverage.CfgNode;

/**
 * @author LLT
 *
 */
public class ExtInstruction extends Instruction {
	private CfgNode cfgNode;
	private boolean newCfg;
	private String testMethod;
	
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
		if (newCfg) {
			ExtInstruction extPredecessor = (ExtInstruction) predecessorInsn;
			cfgNode.setPredecessor(extPredecessor.cfgNode);
		}
	}
	
	@Override
	public void setCovered() {
		super.setCovered();
		cfgNode.markCovered(null, testMethod);
	}
	
	public CfgNode getCfgNode() {
		return cfgNode;
	}

	public void setTestcase(String testMethod) {
		this.testMethod = testMethod;
	}
}
