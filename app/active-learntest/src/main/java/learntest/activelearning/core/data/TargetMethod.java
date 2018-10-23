/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.activelearning.core.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.VarInsnNode;

import cfg.CFG;
import cfg.CfgNode;
import cfg.utils.OpcodeUtils;
import sav.common.core.utils.Assert;
import sav.strategies.dto.BreakPoint;

/**
 * @author LLT
 *
 */
public class TargetMethod {
	private MethodInfo methodInfo;
	private CFG cfg;
	private Collection<String> accessedFields;
	private BreakPoint methodEntryBkp;

	public TargetMethod(MethodInfo methodInfo) {
		this.methodInfo = methodInfo;
	}

	public CFG getCfg() {
		return cfg;
	}

	public void updateCfgIfNotExist(CFG cfg) {
		if (this.cfg == null) {
			this.cfg = cfg;
		}
	}

	public Collection<String> getAccessedFields() {
		if (accessedFields == null) {
			accessedFields = getAccessedFields(cfg);
		}
		return accessedFields;
	}

	@SuppressWarnings("unchecked")
	private static Collection<String> getAccessedFields(CFG cfg) {
		if (cfg == null) {
			return Collections.EMPTY_LIST;
		}
		Set<String> fields = new HashSet<String>();
		for (CfgNode node : cfg.getNodeList()) {
			AbstractInsnNode asmNode = node.getInsnNode();
			if (OpcodeUtils.isLoadInst(asmNode.getOpcode())) {
				VarInsnNode varNode = (VarInsnNode) asmNode;
				List<LocalVariableNode> localVariables = cfg.getMethodNode().localVariables;
				if (localVariables == null || varNode.var >= localVariables.size()) {
					continue;
				}
				LocalVariableNode accessedVar = localVariables.get(varNode.var);
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
		return node.getBranches().iterator().next().getInsnNode();
	}

	public ClassInfo getTargetClazz() {
		return methodInfo.getTargetClazz();
	}

	public List<String> getParams() {
		return methodInfo.getParams();
	}

	public String getMethodName() {
		return methodInfo.getMethodName();
	}

	public String getMethodSignature() {
		return methodInfo.getSignature();
	}

	public int getLineNum() {
		return methodInfo.getLineNum();
	}

	public String getMethodFullName() {
		return methodInfo.getMethodFullName();
	}

	public String getClassName() {
		return methodInfo.getClassName();
	}

	public Map<String, List<String>> createClassMethodMap() {
		return methodInfo.createClassMethodMap();
	}

	public List<String> getParamTypes() {
		return methodInfo.getParamTypes();
	}

	public int getMethodLength() {
		return methodInfo.getMethodLength();
	}

	public String toString() {
		return methodInfo.toString();
	}

	public MethodInfo getMethodInfo() {
		return methodInfo;
	}

	public String getMethodId() {
		return methodInfo.getMethodId();
	}
	
}
