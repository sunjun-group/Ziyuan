/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.analysis.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class NodeCoverage {
	protected CfgCoverage cfgCoverage;
	protected CfgNode cfgNode;
	/* map between testcase idx and covered frequency */
	protected Map<Integer, Integer> coveredTcs;
	/* we keep node idx to refers to branch node 
	 * in case a false branch is missing, we use next node idx to map with coverage of missing branch.
	 * */
	protected Map<Integer, List<Integer>> coveredBranches;
	
	public NodeCoverage(CfgCoverage cfgCoverage, CfgNode cfgNode) {
		this.cfgCoverage = cfgCoverage;
		this.cfgNode = cfgNode;
		coveredTcs = new HashMap<Integer, Integer>();
		coveredBranches = new HashMap<Integer, List<Integer>>();
	}

	/**
	 * update the map that keeps which branch is covered by which testcases
	 * @param coveredBranch
	 * @param testIdx
	 */
	public void updateCoveredBranchesForTc(CfgNode coveredBranch, int testIdx) {
		int branchId = coveredBranch.getIdx();
		updateCoveredBranchesForTc(testIdx, branchId);
	}

	public void updateCoveredBranchesForTc(int testIdx, int branchId) {
		List<Integer> coverTcs = coveredBranches.get(branchId);
		if (CollectionUtils.isEmpty(coverTcs)) {
			coverTcs = new ArrayList<Integer>();
			coveredBranches.put(branchId, coverTcs);
		}
		CollectionUtils.addIfNotNullNotExist(coverTcs, testIdx);
	}
	
	public void setCovered(int testIndx, int count) {
		Integer coveredCount = coveredTcs.get(testIndx);
		coveredCount = coveredCount == null ? count : coveredCount + count;
		coveredTcs.put(testIndx, coveredCount);
	}

	public boolean isCovered(int testIdx) {
		if (coveredTcs.containsKey(testIdx)) {
			return true;
		}
		int unDupTestIdx = getUnDuplicatedTestIdx(testIdx);
		return (unDupTestIdx != testIdx) && coveredTcs.containsKey(unDupTestIdx);
	}
	
	public boolean isCovered() {
		return !coveredTcs.isEmpty();
	}
	
	public int getCoveredFreq(int testIdx) {
		int unDupTestIdx = getUnDuplicatedTestIdx(testIdx);
		Integer coveredCount = coveredTcs.get(unDupTestIdx);
		if (coveredCount != null && coveredCount > 0) {
			return coveredCount;
		}
		return 0;
	}
	
	private int getUnDuplicatedTestIdx(int testIdx) {
		if (cfgCoverage.getDupTcs() == null || cfgCoverage.getDupTcs().containsKey(testIdx)) {
			return testIdx;
		}
		for (Entry<Integer, List<Integer>> entry : cfgCoverage.getDupTcs().entrySet()) {
			if (entry.getValue().contains(testIdx)) {
				return entry.getKey();
			}
		}
		return testIdx;
	}

	public CfgCoverage getCfgCoverage() {
		return cfgCoverage;
	}

	public CfgNode getCfgNode() {
		return cfgNode;
	}
	
	public List<Integer> getAllCoveredTcs() {
		List<Integer> allCoveredTcs = new ArrayList<Integer>(coveredTcs.keySet());
		for (Integer testIdx : coveredTcs.keySet()) {
			allCoveredTcs.addAll(cfgCoverage.getDupTcs(testIdx));
		}
		return allCoveredTcs;
	}
	
	public int getCoveredTcsTotal() {
		int total = coveredTcs.size();
		for (Integer testIdx : coveredTcs.keySet()) {
			total += cfgCoverage.getDupTcs(testIdx).size();
		}
		return total;
	}
	
	Map<Integer, Integer> getUndupCoveredTcs() {
		return coveredTcs;
	}
	
	public Iterator<int[]> coveredTcsIterator() {
		return new Iterator<int[]>() {
			Iterator<Integer> it = coveredTcs.keySet().iterator();
			int coveredFreq;
			Iterator<Integer> dupIt = null;

			public boolean hasNext() {
				if (dupIt != null) {
					return dupIt.hasNext();
				}
				return it.hasNext();
			}

			/**
			 * return next[2] in which next[0] is testCase idx, and next[1] is covered freq.
			 * */
			public int[] next() {
				int[] next = new int[2];
				if (dupIt != null) {
					next[0] = dupIt.next();
					next[1] = coveredFreq;
					if (!dupIt.hasNext()) {
						dupIt = null;
					}
				} else {
					Integer tcIdx = it.next();
					next[0] = tcIdx;
					next[1] = coveredTcs.get(tcIdx);
					List<Integer> dupTcs = cfgCoverage.getDupTcs(tcIdx);
					if (!dupTcs.isEmpty()) {
						dupIt = dupTcs.iterator();
						coveredFreq = next[1];
					}
				}
				return next;
			}
		};
	}
	
	/**
	 * @return map is always not null
	 */
	public Collection<Integer> getCoveredBranches() {
		return coveredBranches.keySet();
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getCoveredTcsForBranch(int branchIdx) {
		List<Integer> unDupTcs = coveredBranches.get(branchIdx);
		if (CollectionUtils.isEmpty(unDupTcs)) {
			return Collections.EMPTY_LIST;
		}
		List<Integer> tcs = new ArrayList<Integer>(unDupTcs);
		for (int tcIdx : unDupTcs) {
			tcs.addAll(cfgCoverage.getDupTcs(tcIdx));
		}
		return tcs;
	}
	
	Map<Integer, List<Integer>> getUnDupCoveredBranches() {
		return coveredBranches;
	}
	
	@Override
	public String toString() {
		return "NodeCoverage [" + cfgNode + ", coveredTcs=" + coveredTcs + ", coveredBranches="
				+ coveredBranches + "]";
	}

	public List<Integer> getCoveredBranches(int testIdx) {
		int unDupTestIdx = getUnDuplicatedTestIdx(testIdx);
		List<Integer> result = new ArrayList<Integer>(getCoveredBranches().size());
		for (Entry<Integer, List<Integer>> entry : coveredBranches.entrySet()) {
			if (entry.getValue().contains(unDupTestIdx)) {
				result.add(entry.getKey());
			}
		}
		
		return result;
	}

	public void setCfgCoverage(CfgCoverage cfgCoverage) {
		this.cfgCoverage = cfgCoverage;
	}
}
