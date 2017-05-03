/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.data.testtarget;

import java.util.ArrayList;
import java.util.List;

import cfgcoverage.jacoco.analysis.data.CFG;
import cfgcoverage.jacoco.analysis.data.CfgNode;
import cfgcoverage.jacoco.utils.OpcodeUtils;
import cfgcoverage.org.objectweb.asm.Opcodes;
import cfgcoverage.org.objectweb.asm.tree.AbstractInsnNode;
import cfgcoverage.org.objectweb.asm.tree.FieldInsnNode;
import cfgcoverage.org.objectweb.asm.tree.LocalVariableNode;
import cfgcoverage.org.objectweb.asm.tree.VarInsnNode;
import sav.common.core.utils.Assert;

/**
 * @author LLT
 *
 */
public class TargetMethod {
	private TargetClass targetClazz;
	private String methodName;
	private String methodSignature;
	private int lineNum;
	private List<String> params;
	private CFG cfg;
	private List<String> accessedFields;

	public TargetMethod() {

	}

	public TargetClass getTargetClazz() {
		return targetClazz;
	}

	public CFG getCfg() {
		return cfg;
	}

	public void updateCfgIfNotExist(CFG cfg) {
		if (this.cfg == null) {
			this.cfg = cfg;
		}
	}

	public List<String> getParams() {
		return params;
	}

	public List<String> getAccessedFields() {
		if (accessedFields == null) {
			accessedFields = getAccessedFields(cfg);
		}
		return accessedFields;
	}

	private static List<String> getAccessedFields(CFG cfg) {
		List<String> fields = new ArrayList<String>();
		for (CfgNode node : cfg.getNodeList()) {
			AbstractInsnNode asmNode = node.getInsnNode();
			if (OpcodeUtils.isLoadInst(asmNode.getOpcode())) {
				VarInsnNode varNode = (VarInsnNode) asmNode;
				LocalVariableNode accessedVar = cfg.getMethodNode().localVariables.get(varNode.var);
				if ("this".equals(accessedVar.name)) {
					AbstractInsnNode nextNode = getNextNode(node);
					if (nextNode.getOpcode() == Opcodes.GETFIELD) {
						fields.add(((FieldInsnNode) nextNode).name);
					}
				}
			}
		}
		return fields;
	}

	private static AbstractInsnNode getNextNode(CfgNode node) {
		Assert.assertNotNull(node.getBranches());
		Assert.assertTrue(node.getBranches().size() == 1);
		return node.getBranches().get(0).getInsnNode();
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getMethodSignature() {
		return methodSignature;
	}

	public void setMethodSignature(String methodSignature) {
		this.methodSignature = methodSignature;
	}

	public int getLineNum() {
		return lineNum;
	}

	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}
}
