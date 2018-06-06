/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data.classinfo;

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
import cfg.analysis.OpcodeUtils;
import learntest.core.BreakpointCreator;
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
		return node.getBranches().get(0).getInsnNode();
	}

	public BreakPoint getEntryBkp() {
		if (methodEntryBkp == null) {
			Assert.assertNotNull(cfg, "cfg for method is not set!");
			methodEntryBkp = BreakpointCreator.createMethodEntryBkp(this);
		}
		return methodEntryBkp;
	}

	/**
	 * @return
	 * @see learntest.core.commons.data.classinfo.MethodInfo#getTargetClazz()
	 */
	public ClassInfo getTargetClazz() {
		return methodInfo.getTargetClazz();
	}

	/**
	 * @return
	 * @see learntest.core.commons.data.classinfo.MethodInfo#getParams()
	 */
	public List<String> getParams() {
		return methodInfo.getParams();
	}

	/**
	 * @return
	 * @see learntest.core.commons.data.classinfo.MethodInfo#getMethodName()
	 */
	public String getMethodName() {
		return methodInfo.getMethodName();
	}

	/**
	 * @return
	 * @see learntest.core.commons.data.classinfo.MethodInfo#getMethodSignature()
	 */
	public String getMethodSignature() {
		return methodInfo.getMethodSignature();
	}

	/**
	 * @return
	 * @see learntest.core.commons.data.classinfo.MethodInfo#getLineNum()
	 */
	public int getLineNum() {
		return methodInfo.getLineNum();
	}

	/**
	 * @return
	 * @see learntest.core.commons.data.classinfo.MethodInfo#getMethodFullName()
	 */
	public String getMethodFullName() {
		return methodInfo.getMethodFullName();
	}

	/**
	 * @return
	 * @see learntest.core.commons.data.classinfo.MethodInfo#getClassName()
	 */
	public String getClassName() {
		return methodInfo.getClassName();
	}

	/**
	 * @return
	 * @see learntest.core.commons.data.classinfo.MethodInfo#createClassMethodMap()
	 */
	public Map<String, List<String>> createClassMethodMap() {
		return methodInfo.createClassMethodMap();
	}

	/**
	 * @return
	 * @see learntest.core.commons.data.classinfo.MethodInfo#getParamTypes()
	 */
	public List<String> getParamTypes() {
		return methodInfo.getParamTypes();
	}

	/**
	 * @return
	 * @see learntest.core.commons.data.classinfo.MethodInfo#getMethodLength()
	 */
	public int getMethodLength() {
		return methodInfo.getMethodLength();
	}

	/**
	 * @return
	 * @see learntest.core.commons.data.classinfo.MethodInfo#toString()
	 */
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
