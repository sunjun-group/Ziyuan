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
import java.util.List;
import java.util.Map;
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
import variable.Variable;

/**
 * @author LLT
 *
 */
public interface IInputLearner {

	static Logger log = LoggerFactory.getLogger(IInputLearner.class);
	
	DecisionProbes learn(DecisionProbes inputProbes, BreakpointData result, Map<Integer, List<Variable>> relevantVarMap) throws SavException;
	

	default void recordSample(DecisionProbes inputProbes, SamplingResult sampleResult) {
		if (sampleResult == null) {
			log.info("sample result is null!!!");
			return;
		}
		for (DecisionNodeProbe nodeProbe : inputProbes.getNodeProbes()) {
			INodeCoveredData newData = sampleResult.getNewData(nodeProbe);
			log.info(nodeProbe.getNode().toString());
			Collection<BreakpointValue> trueV = newData.getTrueValues(), falseV = newData.getFalseValues();
//			log.info("	true data after selective sampling " + trueV.size());
//			log.info("	false data after selective sampling " + falseV.size());
			recordSample(nodeProbe, trueV, getTrueSample());
			recordSample(nodeProbe, falseV, getFalseSample());
		}

		for (Entry<String, Collection<BreakpointValue>> entry : getTrueSample().entrySet()) {
			log.info("true : "+entry.getKey()+" "+entry.getValue().size());
		}
		for (Entry<String, Collection<BreakpointValue>> entry : getFalseSample().entrySet()) {
			log.info("false : "+entry.getKey()+" "+entry.getValue().size());
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
