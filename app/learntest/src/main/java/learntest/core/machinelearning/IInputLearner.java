/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.machinelearning;

import java.util.Collection;
import java.util.HashMap;

import icsetlv.common.dto.BreakpointValue;
import learntest.core.commons.data.decision.DecisionNodeProbe;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.data.decision.INodeCoveredData;
import learntest.core.commons.data.sampling.SamplingResult;
import sav.common.core.SavException;

/**
 * @author LLT
 *
 */
public interface IInputLearner {

	DecisionProbes learn(DecisionProbes inputProbes) throws SavException;
	

	default void recordSample(DecisionNodeProbe nodeProbe, SamplingResult sampleResult) {
		INodeCoveredData newData = sampleResult.getNewData(nodeProbe);
		recordSample(nodeProbe, newData.getTrueValues(), getTrueSample());
		recordSample(nodeProbe, newData.getFalseValues(), getFalseSample());
		
	}

	default void recordSample(DecisionNodeProbe nodeProbe,
			Collection<BreakpointValue> collection, HashMap<DecisionNodeProbe, Collection<BreakpointValue>> record) {
		if (!record.containsKey(nodeProbe)) {
			record.put(nodeProbe, collection);
		}else {
			record.get(nodeProbe).addAll(collection);
		}
		
	}
	
	public HashMap<DecisionNodeProbe, Collection<BreakpointValue>> getTrueSample();
	
	public HashMap<DecisionNodeProbe, Collection<BreakpointValue>> getFalseSample();

}
