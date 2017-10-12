/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core;

import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import cfgcoverage.jacoco.analysis.data.CfgNode;
import icsetlv.common.dto.BreakpointValue;
import learntest.core.machinelearning.CfgNodeDomainInfo;
import learntest.core.machinelearning.FormulaInfo;
import learntest.core.machinelearning.IInputLearner;

/**
 * @author LLT
 * Extract to make code clean. [most of code in this is probably only for test]
 * This is just temporary to make RuntimeInfo clean [new TestRunTimeInfo makes the code quite messy now]
 */
public class TestRunTimeInfo extends RunTimeInfo {
	protected int learnState = 0; /** if only learn valid formula 1, also has rubbish learned formula 2; only rubbish -1, no formula 0 */
	private List<FormulaInfo> learnedFormulas = new LinkedList<>();
	private double validCoverage;
	public String l2tWorseThanRand = "", randWorseThanl2t = "";

	private HashMap<String, Collection<BreakpointValue>> trueSample = new HashMap<>(),
			falseSample = new HashMap<>();
	private HashMap<CfgNode, CfgNodeDomainInfo> domainMap = new HashMap<>(1);
	private String logFile;
	
	public TestRunTimeInfo(long time, double coverage, int testCnt) {
		super(time, coverage, testCnt);
	}
	
	public TestRunTimeInfo(long time, double coverage, int testCnt, String coverageInfo) {
		super(time, coverage, testCnt, coverageInfo);
	}
	
	public TestRunTimeInfo(long time, double coverage, int testCnt, double validCoverage) {
		this(time, coverage, testCnt);
		this.validCoverage = validCoverage;
	}
	
	public TestRunTimeInfo() {
		
	}

	public void add(TestRunTimeInfo subRunInfo) {
		super.add(subRunInfo);
		learnedFormulas.addAll(subRunInfo.learnedFormulas);
		learnState = subRunInfo.learnState;
		trueSample.putAll(subRunInfo.trueSample);
		falseSample.putAll(subRunInfo.falseSample);
		domainMap = subRunInfo.domainMap;
		logFile = subRunInfo.logFile;
	}

	public double getValidCoverage() {
		return validCoverage;
	}

	public List<FormulaInfo> getLearnedFormulas() {
		return learnedFormulas;
	}

	public int getLearnState() {
		return learnState;
	}

	public void setSample(IInputLearner learner) {
		this.trueSample.putAll(learner.getTrueSample());
		this.falseSample.putAll(learner.getFalseSample());		
	}

	public HashMap<String, Collection<BreakpointValue>> getTrueSample() {
		return trueSample;
	}

	public HashMap<String, Collection<BreakpointValue>> getFalseSample() {
		return falseSample;
	}

	public HashMap<CfgNode, CfgNodeDomainInfo> getDomainMap() {
		return domainMap;
	}

	public void setDomainMap(HashMap<CfgNode, CfgNodeDomainInfo> domainMap) {
		this.domainMap = domainMap;
	}

	public String getLogFile() {
		return logFile;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}
	
	public void setLearnState(int learnState) {
		this.learnState = learnState;
	}

	
	
}
