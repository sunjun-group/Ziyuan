/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data.decision;

import java.util.List;

import cfgcoverage.jacoco.analysis.data.BranchCoveredType;
import cfgcoverage.jacoco.analysis.data.BranchRelationship;
import cfgcoverage.jacoco.analysis.data.NodeCoverage;
import icsetlv.common.dto.BreakpointValue;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class SampleNodeCoveredData extends AbstractNodeCoveredData implements INodeCoveredData {
	private NodeCoverage nodeCoverage;
	private int samplesFirstIdx;
	private List<BreakpointValue> sampleTestInputs;
	
	public SampleNodeCoveredData(NodeCoverage coverage, List<BreakpointValue> sampleTestInputs, int samplesFirstIdx) {
		this.nodeCoverage = coverage;
		this.sampleTestInputs = sampleTestInputs;
		this.samplesFirstIdx = samplesFirstIdx;
	}

	public boolean isOnlyOneBranchCovered() {
		BranchCoveredType coveredType = nodeCoverage.getBranchCoveredType();
		return CollectionUtils.existIn(coveredType, BranchCoveredType.TRUE, BranchCoveredType.FALSE);
	}

	public CoveredBranches getCoveredBranches() {
		return CoveredBranches.valueOf(nodeCoverage.getBranchCoveredType());
	}

	public boolean areAllbranchesUncovered() {
		return nodeCoverage.getBranchCoveredType() == BranchCoveredType.NONE;
	}
	
	public void update(NodeCoverage coverage, int samplesFirstIdx, java.util.List<BreakpointValue> sampleTestInputs) {
		throw new UnsupportedOperationException();
	};

	@Override
	public List<BreakpointValue> getFalseValues() {
		if (falseValues == null) {
			falseValues = getBranchCoveredValue(nodeCoverage, sampleTestInputs, BranchRelationship.FALSE,
					samplesFirstIdx);
		}
		return falseValues;
	}

	@Override
	public List<BreakpointValue> getTrueValues() {
		if (trueValues == null) {
			trueValues = getBranchCoveredValue(nodeCoverage, sampleTestInputs, BranchRelationship.TRUE,
					samplesFirstIdx);
		}
		return trueValues;
	}

	@Override
	public List<BreakpointValue> getMoreTimesValues() {
		if (moreTimesValues == null) {
			moreTimesValues = getFreqCoveredValue(nodeCoverage, sampleTestInputs, samplesFirstIdx, true);
		}
		return moreTimesValues;
	}

	@Override
	public List<BreakpointValue> getOneTimeValues() {
		if (oneTimeValues == null) {
			oneTimeValues = getFreqCoveredValue(nodeCoverage, sampleTestInputs, samplesFirstIdx, false);
		}
		return oneTimeValues;
	}

	public void clearCache() {
		trueValues = null;
		falseValues = null;
		oneTimeValues = null;
		moreTimesValues = null;
	}
	
}
