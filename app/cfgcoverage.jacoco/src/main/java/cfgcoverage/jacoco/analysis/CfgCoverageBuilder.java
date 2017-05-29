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

import cfgcoverage.jacoco.analysis.data.CFG;
import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.analysis.data.CfgNode;
import cfgcoverage.jacoco.analysis.data.ExtInstruction;
import cfgcoverage.jacoco.analysis.data.NodeCoverage;
import cfgcoverage.jacoco.utils.CfgConstructorUtils;
import codecoverage.jacoco.agent.JaCoCoUtils;
import sav.common.core.utils.Assert;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.SignatureUtils;

/**
 * @author LLT
 *
 */
public class CfgCoverageBuilder {
	private String className;
	private List<String> targetMethods;
	private Map<String, CfgCoverage> methodCfgCoverageMap;
	private CfgCoverage cfgCoverage;
	private CFG cfg;
	
	/* internal info */
	private String methodId;
	private int nodeIdx;
	/* index of testcase in testMethodsList */
	private int testcaseIdx;
	private List<String> testMethods;
	/*
	 * testcase index in method coverage, this idx and testcaseIdx can be
	 * different if the testcase list for the builder is different from testcase
	 * list in cfgCoverage
	 */
	private int methodTestcaseIdx;
	private boolean newCfg;
	private State state;
	
	public CfgCoverageBuilder(List<String> targetMethods) {
		methodCfgCoverageMap = new HashMap<String, CfgCoverage>();
		this.targetMethods = targetMethods;
	}
	
	/**
	 * @param cfgCoverageMap  the map between methodIds (className.methodName) and theirs existing cfgcoverage
	 */
	public void setMethodCfgCoverageMap(Map<String, CfgCoverage> cfgCoverageMap) {
		this.methodCfgCoverageMap = cfgCoverageMap;
	}
	
	public CfgCoverageBuilder startClass(String name, String signature) {
		state = State.CLASS;
		this.className = JaCoCoUtils.getClassName(name);
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
	
	public void setTargetMethods(List<String> targetMethods) {
		this.targetMethods = targetMethods;
	}
	
	public boolean acceptMethod(String name) {
		if (!targetMethods.contains(ClassUtils.toClassMethodStr(className, name))) {
			return false;
		}
		return true;
	}

	public void startMethod(MethodNode methodNode) {
		Assert.assertTrue(state == State.CLASS, "expect state CLASS, get state ", state.toString());
		state = State.METHOD;
		methodId = createMethodId(methodNode);
		cfgCoverage = methodCfgCoverageMap.get(methodId);
		if (cfgCoverage == null) {
			newCfg = true;
			cfg = new CFG(methodId);
			cfg.setMethodNode(methodNode);
			cfgCoverage = new CfgCoverage(cfg);
			methodCfgCoverageMap.put(methodId, cfgCoverage);
		} else {
			cfg = cfgCoverage.getCfg();
			cfgCoverage.initNodeCoveragesIfEmpty();
		}
		methodTestcaseIdx = cfgCoverage.addTestcases(testMethods.get(testcaseIdx));
	}
	
	public ExtInstruction instruction(AbstractInsnNode node, int line) {
		Assert.assertTrue(state == State.METHOD, "expect state METHOD, get state ", state.toString());
		/* look up for existing node */
		CfgNode cfgNode = cfg.getNode(nodeIdx ++);
		ExtInstruction extInstruction;
		if (cfgNode == null) {
			cfgNode = new CfgNode(node, line);
			cfg.addNode(cfgNode);
			NodeCoverage nodeCoverage = cfgCoverage.addCoverage(cfgNode);
			extInstruction = new ExtInstruction(cfgNode, nodeCoverage, true);
		} else {
			extInstruction = new ExtInstruction(cfgNode, cfgCoverage.getCoverage(cfgNode), false);
		}
		extInstruction.setTestIdx(methodTestcaseIdx);
		return extInstruction;
	}

	public void endMethod() {
		Assert.assertTrue(state == State.METHOD, "expect state METHOD, get state ", state.toString());
		if (newCfg) {
			CfgConstructorUtils.completeCfg(cfg);
		}
		reset();
		state = State.CLASS;
	}
	
	private void reset() {
		newCfg = false;
		methodId = null;
		nodeIdx = 0;
		cfgCoverage = null;
		cfg = null;
	}

	private String createMethodId(MethodNode method) {
		String fullMethodName = ClassUtils.toClassMethodStr(className, method.name);
		return SignatureUtils.createMethodNameSign(fullMethodName, method.desc);
	}
	
	public void match(boolean b) {
		// nothing to do for now.
	}
	
	public Map<String, CfgCoverage> getMethodCfgCoverageMap() {
		return methodCfgCoverageMap;
	}
	
	public List<CfgCoverage> getCoverage() {
		return new ArrayList<CfgCoverage>(getMethodCfgCoverageMap().values());
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
