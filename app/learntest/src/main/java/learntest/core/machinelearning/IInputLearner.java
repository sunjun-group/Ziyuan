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
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import icsetlv.common.dto.BreakpointData;
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

	static Logger log = LoggerFactory.getLogger(PrecondDecisionLearner.class);
	
	DecisionProbes learn(DecisionProbes inputProbes, BreakpointData result) throws SavException;
	

	default void recordSample(DecisionProbes inputProbes, SamplingResult sampleResult) {
		
		System.out.println(sampleResult.getNewTestInputs().size() + " " + sampleResult.getNewTestInputs());
		
		for (DecisionNodeProbe nodeProbe : inputProbes.getNodeProbes()) {
			INodeCoveredData newData = sampleResult.getNewData(nodeProbe);
			log.info(nodeProbe.getNode().toString());
			log.info("true data after selective sampling " + newData.getTrueValues().size());
			log.info("false data after selective sampling " + newData.getFalseValues().size());
			recordSample(nodeProbe, newData.getTrueValues(), getTrueSample());
			recordSample(nodeProbe, newData.getFalseValues(), getFalseSample());
		}

		for (Entry<String, Collection<BreakpointValue>> entry : getTrueSample().entrySet()) {
			System.out.println("true : "+entry.getValue().size());
		}
		for (Entry<String, Collection<BreakpointValue>> entry : getFalseSample().entrySet()) {
			System.out.println("false : "+entry.getValue().size());
		}
	}

	default void recordSample(DecisionNodeProbe nodeProbe,
			Collection<BreakpointValue> collection, HashMap<String, Collection<BreakpointValue>> record) {
		if (!record.containsKey(nodeProbe.getNode().toString())) {
			record.put(nodeProbe.getNode().toString(), collection);
		}else {
			record.get(nodeProbe.getNode().toString()).addAll(collection);
		}
		
	}
	
	public HashMap<String, Collection<BreakpointValue>> getTrueSample();
	
	public HashMap<String, Collection<BreakpointValue>> getFalseSample();

}
