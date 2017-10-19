/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package evosuite;

import java.util.List;
import java.util.Set;

import org.evosuite.EvoSuite;
import org.evosuite.result.BranchInfo;
import org.evosuite.result.TestGenerationResult;

import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class EvosuiteRunner {
	
	public static EvosuiteResult run(EvosuitParams params) {
		EvoSuite evosuite = new EvoSuite();
		EvosuiteResult evoResult = new EvosuiteResult();
		List<List<TestGenerationResult>> result = (List<List<TestGenerationResult>>) evosuite
				.parseCommandLine(params.getCommandLine());
		for (List<TestGenerationResult> list : result) {
			for (TestGenerationResult testGenerationResult : list) {
				Set<BranchInfo> uncoveredBranches = testGenerationResult.getUncoveredBranches();
				Set<BranchInfo> coveredBranches = testGenerationResult.getCoveredBranches();
				System.out.println(testGenerationResult);
				evoResult.targetClass = testGenerationResult.getClassUnderTest();
				evoResult.targetMethod = getTargetMethod(uncoveredBranches, coveredBranches);
				evoResult.uncoveredBranches = uncoveredBranches;
				evoResult.coveredBranches = coveredBranches;
				evoResult.branchCoverage = getBranchCoverage(coveredBranches, uncoveredBranches);
				System.out.println("branch coverage: " + evoResult.branchCoverage);
			}
		}
		return evoResult;
	}
	
	private static String getTargetMethod(Set<BranchInfo> uncoveredBranches, Set<BranchInfo> coveredBranches) {
		BranchInfo info;
		if (CollectionUtils.isNotEmpty(uncoveredBranches)) {
			info = uncoveredBranches.iterator().next();
		} else if (CollectionUtils.isNotEmpty(coveredBranches)) {
			info = coveredBranches.iterator().next();
		} else {
			return null;
		}
		return StringUtils.dotJoin(info.getClassName(), info.getMethodName());
	}

	private static double getBranchCoverage(Set<BranchInfo> coveredBranches, Set<BranchInfo> uncoveredBranches) {
		double covered = CollectionUtils.getSize(coveredBranches);
		double total = covered + CollectionUtils.getSize(uncoveredBranches);
		return covered / (double) total;
	}
	
	public static class EvosuiteResult {
		public String targetClass;
		public String targetMethod; // full name
		public Set<BranchInfo> uncoveredBranches;
		public Set<BranchInfo> coveredBranches;
		public double branchCoverage;
		public List<String> coverageInfo;
		
		
	}
}
