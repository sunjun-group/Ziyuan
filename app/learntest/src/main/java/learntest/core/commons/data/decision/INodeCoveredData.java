/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data.decision;

import java.util.Collection;
import java.util.List;

import cfgcoverage.jacoco.analysis.data.NodeCoverage;
import icsetlv.common.dto.BreakpointValue;

/**
 * @author LLT
 *
 */
public interface INodeCoveredData {

	Collection<BreakpointValue> getFalseValues();

	Collection<BreakpointValue> getTrueValues();

	Collection<BreakpointValue> getMoreTimesValues();

	Collection<BreakpointValue> getOneTimeValues();

	void update(NodeCoverage coverage, int samplesFirstIdx, List<BreakpointValue> sampleTestInputs);
	
}
