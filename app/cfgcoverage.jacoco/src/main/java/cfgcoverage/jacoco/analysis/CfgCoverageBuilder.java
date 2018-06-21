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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfg.CFG;
import cfg.CfgNode;
import cfg.utils.CfgConstructor;
import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.analysis.data.ExtInstruction;
import cfgcoverage.jacoco.utils.CfgJaCoCoUtils;
import codecoverage.jacoco.agent.JaCoCoUtils;
import sav.common.core.utils.Assert;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.SignatureUtils;

/**
 * @author LLT
 *
 */
public class CfgCoverageBuilder {
	private static Logger log = LoggerFactory.getLogger(CfgCoverageBuilder.class);
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
	private State state;
	private Map<String, Set<String>> duplicatedTcs;
	private Set<CfgCoverage> updatedCfgCvgs = new HashSet<CfgCoverage>();
	
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
		updatedCfgCvgs.clear();
		return this;
	}
	
	public void setTargetMethods(List<String> targetMethods) {
		this.targetMethods = targetMethods;
	}
	
	public boolean acceptMethod(String method, String signature) {
		for (String targetMethod : targetMethods) {
			String targetMethodSign = SignatureUtils.extractSignature(targetMethod);
			if (targetMethodSign.isEmpty()) {
				if (targetMethod.equals(ClassUtils.toClassMethodStr(className, method))) {
					return true;
				}
			} else {
				if (targetMethod.equals(CfgJaCoCoUtils.createMethodId(className, method, signature))) {
					return true;
				}
			}
		}
		return false;
	}

	public void startMethod(MethodNode methodNode) {
		Assert.assertTrue(state == State.CLASS, "expect state CLASS, get state ", state.toString());
		state = State.METHOD;
		methodId = createMethodId(methodNode);
		cfgCoverage = methodCfgCoverageMap.get(methodId);
		if (cfgCoverage == null) {
			cfg = new CfgConstructor().constructCFG(className, methodNode);
			cfgCoverage = new CfgCoverage(cfg);
			methodCfgCoverageMap.put(methodId, cfgCoverage);
		} else {
			cfg = cfgCoverage.getCfg();
			cfgCoverage.initNodeCoveragesIfEmpty();
		}
		if (!updatedCfgCvgs.contains(cfgCoverage)) {
			cfgCoverage.addNewTestcases(testMethods);
			updatedCfgCvgs.add(cfgCoverage);
		}
		methodTestcaseIdx = cfgCoverage.getTestIdx(testMethods.get(testcaseIdx));
	}
	
	public ExtInstruction instruction(AbstractInsnNode node, int line) {
		Assert.assertTrue(state == State.METHOD, "expect state METHOD, get state ", state.toString());
		/* look up for existing node */
		CfgNode cfgNode = cfg.getNode(nodeIdx ++);
		ExtInstruction extInstruction;
		extInstruction = new ExtInstruction(cfgNode, cfgCoverage.getCoverage(cfgNode));
		extInstruction.setTestIdx(methodTestcaseIdx);
		return extInstruction;
	}

	public void endMethod() {
		Assert.assertTrue(state == State.METHOD, "expect state METHOD, get state ", state.toString());
		reset();
		state = State.CLASS;
	}
	
	private void reset() {
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
	
	List<String> getTestMethods() {
		return testMethods;
	}
	
	String getCurrentTestCase() {
		return testMethods.get(testcaseIdx);
	}
	
	public void endClass() {
		state = State.INIT;
	}
	
	private enum State {
		INIT,
		CLASS,
		METHOD,
	}

	public void addDuplicate(String orgTc, String dupTc) {
		if (duplicatedTcs == null) {
			duplicatedTcs = new HashMap<String, Set<String>>();
		}
		CollectionUtils.getSetInitIfEmpty(duplicatedTcs, orgTc).add(dupTc);
	}

	public void commitDuplicate() {
		for (CfgCoverage coverage : methodCfgCoverageMap.values()) {
			/* make sure that new testcases already added to cfgCoverage */
			coverage.addNewTestcases(testMethods);
			coverage.updateDuplicateTcs(duplicatedTcs);
		}
	}

	public void endAnalyzing() {
		if (duplicatedTcs != null) {
			log.debug("duplicate coverage path: {}", duplicatedTcs.size());
			commitDuplicate();
		}
	}
}
