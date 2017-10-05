/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data.decision;

import java.util.List;

import cfgcoverage.jacoco.analysis.data.NodeCoverage;
import icsetlv.common.dto.BreakpointValue;

/**
 * @author LLT
 *
 */
public interface INodeCoveredData {

	/** note : if node is loop header, only return values that never enter loop */
	List<BreakpointValue> getFalseValues();

	List<BreakpointValue> getTrueValues();

	List<BreakpointValue> getMoreTimesValues();

	List<BreakpointValue> getOneTimeValues();

	void update(NodeCoverage coverage, int samplesFirstIdx, List<BreakpointValue> sampleTestInputs);
	
}
