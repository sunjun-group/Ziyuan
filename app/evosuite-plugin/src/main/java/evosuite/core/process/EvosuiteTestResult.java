/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package evosuite.core.process;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.evosuite.result.BranchInfo;
import org.evosuite.result.TestGenerationResult;

/**
 * @author LLT
 *
 */
public class EvosuiteTestResult implements Serializable {
	private static final long serialVersionUID = -2666077310752763723L;
	private Set<BranchInfo> uncoveredBranches;
	private Set<BranchInfo> coveredBranches;
	private String targetClass;
	private long runningTime;

	public static List<List<EvosuiteTestResult>> extract (List<List<TestGenerationResult>> result, long runningTime) {
		List<List<EvosuiteTestResult>> eResult = new ArrayList<List<EvosuiteTestResult>>(result.size());
		for (List<TestGenerationResult> list : result) {
			List<EvosuiteTestResult> eList = new ArrayList<>(list.size());
			eResult.add(eList);
			for (TestGenerationResult testGenerationResult : list) {
				EvosuiteTestResult ele = new EvosuiteTestResult();
				eList.add(ele);
				ele.uncoveredBranches = testGenerationResult.getUncoveredBranches();
				ele.coveredBranches = testGenerationResult.getCoveredBranches();
				ele.targetClass = testGenerationResult.getClassUnderTest();
				ele.runningTime = runningTime;
			}
		}
		return eResult;
	}

	public Set<BranchInfo> getUncoveredBranches() {
		return uncoveredBranches;
	}

	public void setUncoveredBranches(Set<BranchInfo> uncoveredBranches) {
		this.uncoveredBranches = uncoveredBranches;
	}

	public Set<BranchInfo> getCoveredBranches() {
		return coveredBranches;
	}

	public void setCoveredBranches(Set<BranchInfo> coveredBranches) {
		this.coveredBranches = coveredBranches;
	}

	public String getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(String targetClass) {
		this.targetClass = targetClass;
	}

	public long getRunningTime() {
		return runningTime;
	}

	public void setRunningTime(long runningTime) {
		this.runningTime = runningTime;
	}
}
