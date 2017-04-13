/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import cfgcoverage.jacoco.coverage.CfgCoverage;
import cfgcoverage.jacoco.coverage.CfgNode;
import cfgcoverage.jacoco.extension.IAnalyzerListerner;
import cfgcoverage.jacoco.extension.IInstructionHandler;
import sav.common.core.utils.SignatureUtils;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class CfgCoverageCollector implements IAnalyzerListerner, IInstructionHandler {
	private CfgCoverage cfgCoverage;
	private String className;
	private Map<String, CfgCoverage> methodCfgMap;
	private String methodId;
	private int nodeIdx;
	private int testcaseIdx;
	private List<String> testMethods;
	private boolean newCoverage;
	
	public CfgCoverageCollector() {
		methodCfgMap = new HashMap<String, CfgCoverage>();
	}
	
	@Override
	public void onEnterClass(String name, String signature) {
		this.className = name;
	}

	@Override
	public void onEnterMethodNode(MethodNode methodNode) {
		newCoverage = false;
		methodId = createMethodId(methodNode);
		cfgCoverage = methodCfgMap.get(methodId);
		if (cfgCoverage == null) {
			newCoverage = true;
			cfgCoverage = new CfgCoverage();
			cfgCoverage.setMethodNode(methodNode);
			methodCfgMap.put(methodId, cfgCoverage);
		}
		nodeIdx = 0;
	}
	
	@Override
	public ExtInstruction createInstruction(AbstractInsnNode node, int line) {
		/* look up for existing node */
		CfgNode cfgNode = cfgCoverage.getNode(nodeIdx ++);
		ExtInstruction extInstruction;
		if (cfgNode == null) {
			extInstruction = new ExtInstruction(node, line);
			cfgCoverage.addNode(extInstruction.getCfgNode());
		} else {
			extInstruction = new ExtInstruction(cfgNode);
		}
		extInstruction.setTestcase(testMethods.get(testcaseIdx));
		return extInstruction;
	}

	@Override
	public void onExitMethodNode() {
		if (newCoverage) {
			cfgCoverage.updateExitNodes();
		}
	}
	
	private String createMethodId(MethodNode method) {
		String fullMethodName = StringUtils.dotJoin(className, method.name);
		return SignatureUtils.createMethodNameSign(fullMethodName, method.signature);
	}

	public Map<String, CfgCoverage> getMethodCfgMap() {
		return methodCfgMap;
	}

	public void setTestcaseIdx(int testcaseIdx) {
		this.testcaseIdx = testcaseIdx;
	}

	public void setTestcases(List<String> testMethods) {
		this.testMethods = testMethods;
	}

}
