/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data.decision;

import java.util.ArrayList;
import java.util.List;

import cfgcoverage.jacoco.analysis.data.BranchCoveredType;
import cfgcoverage.jacoco.analysis.data.NodeCoverage;
import icsetlv.common.dto.BreakpointValue;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class CompositeNodeCoveredData implements INodeCoveredData {
	private List<SampleNodeCoveredData> coveredData;

	public CompositeNodeCoveredData(NodeCoverage coverage, List<BreakpointValue> newTestInputs) {
		coveredData = new ArrayList<SampleNodeCoveredData>();
		coveredData.add(new SampleNodeCoveredData(coverage, newTestInputs, 0));
	}
	
	@Override
	public void update(NodeCoverage coverage, int samplesFirstIdx, List<BreakpointValue> sampleTestInputs) {
		coveredData.add(new SampleNodeCoveredData(coverage, sampleTestInputs, samplesFirstIdx));
	}

	@Override
	public List<BreakpointValue> getFalseValues() {
		CompositeList<BreakpointValue> values = new CompositeList<BreakpointValue>(coveredData.size());
		for (SampleNodeCoveredData data : coveredData) {
			values.addAll(data.getFalseValues());
		}
		return values;
	}

	@Override
	public List<BreakpointValue> getTrueValues() {
		CompositeList<BreakpointValue> values = new CompositeList<BreakpointValue>(coveredData.size());
		for (SampleNodeCoveredData data : coveredData) {
			values.addAll(data.getTrueValues());
		}
		return values;
	}

	@Override
	public List<BreakpointValue> getMoreTimesValues() {
		CompositeList<BreakpointValue> values = new CompositeList<BreakpointValue>(coveredData.size());
		for (SampleNodeCoveredData data : coveredData) {
			values.addAll(data.getMoreTimesValues());
		}
		return values;
	}

	@Override
	public List<BreakpointValue> getOneTimeValues() {
		CompositeList<BreakpointValue> values = new CompositeList<BreakpointValue>(coveredData.size());
		for (SampleNodeCoveredData data : coveredData) {
			values.addAll(data.getOneTimeValues());
		}
		return values;
	}

	public CoveredBranches getCoveredBranches() {
		BranchCoveredType coveredType = BranchCoveredType.NONE;
		for (SampleNodeCoveredData data : coveredData) {
			coveredType = BranchCoveredType.append(coveredType, data.getCoveredBranches().getType());
		}
		return CoveredBranches.valueOf(coveredType);
	}

	public boolean areAllbranchesUncovered() {
		return getCoveredBranches() == CoveredBranches.NONE;
	}

	public void clearCache() {
		for (SampleNodeCoveredData data : coveredData) {
			data.clearCache();
		}
	}

	@Override
	public List<BreakpointValue> getAllInputValues() {
		List<BreakpointValue> allValues = new ArrayList<BreakpointValue>();
		for (SampleNodeCoveredData data : CollectionUtils.nullToEmpty(coveredData)) {
			allValues.addAll(data.getAllInputValues());
		}
		return allValues;
	}
}
