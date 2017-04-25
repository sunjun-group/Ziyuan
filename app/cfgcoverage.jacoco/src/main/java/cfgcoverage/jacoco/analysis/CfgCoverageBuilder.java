/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.analysis.data.CfgNode;
import cfgcoverage.jacoco.analysis.data.ExtInstruction;
import sav.common.core.utils.Assert;
import sav.common.core.utils.SignatureUtils;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class CfgCoverageBuilder {
	private CfgCoverage cfgCoverage;
	private String className;
	private Map<String, CfgCoverage> methodCfgMap;
	private String methodId;
	private int nodeIdx;
	private int testcaseIdx;
	private List<String> testMethods;
	private boolean newCoverage;
	private State state;
	
	public CfgCoverageBuilder() {
		methodCfgMap = new HashMap<String, CfgCoverage>();
	}
	
	public CfgCoverageBuilder startClass(String name, String signature) {
		state = State.CLASS;
		this.className = name;
		return this;
	}
	
	public CfgCoverageBuilder testcase(int testcaseIdx) {
		this.testcaseIdx = testcaseIdx;
		return this;
	}

	public CfgCoverageBuilder testcases(List<String> testMethods) {
		this.testMethods = testMethods;
		return this;
	}

	public void startMethod(MethodNode methodNode) {
		Assert.assertTrue(state == State.CLASS, "expect state CLASS, get state ", state.toString());
		state = State.METHOD;
		methodId = createMethodId(methodNode);
		cfgCoverage = methodCfgMap.get(methodId);
		if (cfgCoverage == null) {
			newCoverage = true;
			cfgCoverage = new CfgCoverage();
			cfgCoverage.setMethodNode(methodNode);
			methodCfgMap.put(methodId, cfgCoverage);
		}
	}
	
	public ExtInstruction instruction(AbstractInsnNode node, int line) {
		Assert.assertTrue(state == State.METHOD, "expect state METHOD, get state ", state.toString());
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

	public void endMethod() {
		Assert.assertTrue(state == State.METHOD, "expect state METHOD, get state ", state.toString());
		if (newCoverage) {
			cfgCoverage.updateExitNodes();
		}
		newCoverage = false;
		methodId = null;
		nodeIdx = 0;
		state = State.CLASS;
	}
	
	private String createMethodId(MethodNode method) {
		String fullMethodName = StringUtils.dotJoin(className, method.name);
		return SignatureUtils.createMethodNameSign(fullMethodName, method.signature);
	}
	
	/**
	 * @param b
	 */
	public void match(boolean b) {
		// TODO Auto-generated method stub
		
	}
	
	public Map<String, CfgCoverage> getMethodCfgMap() {
		return methodCfgMap;
	}
	
	public List<CfgCoverage> getCoverage() {
		return new ArrayList<CfgCoverage>(methodCfgMap.values());
	}
	
	public void endClass() {
		state = State.INIT;
	}

	private enum State {
		INIT,
		CLASS,
		METHOD,
	}

}
