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

import icsetlv.common.dto.BreakpointValue;
import learntest.core.RunTimeInfo;
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

	DecisionProbes learn(DecisionProbes inputProbes, Map<Integer, List<Variable>> relevantVarMap)
			throws SavException;

	default void recordSample(DecisionProbes inputProbes, SamplingResult sampleResult, String logFile) {
		StringBuffer sBuffer = new StringBuffer();
		if (sampleResult == null) {
			log.info("sample result is null!!!");
			return;
		}
		sBuffer.append("record sample :=============================\n");
		for (DecisionNodeProbe nodeProbe : inputProbes.getNodeProbes()) {
			INodeCoveredData newData = sampleResult.getNewData(nodeProbe);
			Collection<BreakpointValue> trueV = newData.getTrueValues(), falseV = newData.getFalseValues();
			sBuffer.append(nodeProbe.getNode().toString() + "\n");
			sBuffer.append("	true data in selective sampling " + trueV.size() +trueV.toString()+ "\n");
			sBuffer.append("	false data in selective sampling " + falseV.size() +falseV.toString()+ "\n");
			recordSample(nodeProbe, trueV, getTrueSample());
			recordSample(nodeProbe, falseV, getFalseSample());
		}

		sBuffer.append("total sample now:=============================\n");
		for (Entry<String, Collection<BreakpointValue>> entry : getTrueSample().entrySet()) {
			sBuffer.append("true : " + entry.getKey() + " " + entry.getValue().size() + entry.getValue()+"\n");
		}
		for (Entry<String, Collection<BreakpointValue>> entry : getFalseSample().entrySet()) {
			sBuffer.append("false : " + entry.getKey() + " " + entry.getValue().size() + entry.getValue()+"\n");
		}
		
		RunTimeInfo.write(logFile, sBuffer.toString());
	}

	default void recordSample(DecisionProbes inputProbes, String logFile) {
		for (DecisionNodeProbe nodeProbe : inputProbes.getNodeProbes()) {
			Collection<BreakpointValue> trueV = nodeProbe.getTrueValues(), falseV = nodeProbe.getFalseValues();
			recordSample(nodeProbe, trueV, getTrueSample());
			recordSample(nodeProbe, falseV, getFalseSample());
		}
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("All sample :=============================\n");
		for (Entry<String, Collection<BreakpointValue>> entry : getTrueSample().entrySet()) {
			sBuffer.append("true : " + entry.getKey() + " " + entry.getValue().size() + entry.getValue()+"\n");
		}
		for (Entry<String, Collection<BreakpointValue>> entry : getFalseSample().entrySet()) {
			sBuffer.append("false : " + entry.getKey() + " " + entry.getValue().size() + entry.getValue()+"\n");
		}
		
		RunTimeInfo.write(logFile, sBuffer.toString());
	}

	default void recordSample(DecisionNodeProbe nodeProbe, Collection<BreakpointValue> collection,
			HashMap<String, Collection<BreakpointValue>> record) {
		if (!record.containsKey(nodeProbe.getNode().toString())) {
			record.put(nodeProbe.getNode().toString(), collection);
		} else {
			record.get(nodeProbe.getNode().toString()).addAll(collection);
		}

	}

	public HashMap<String, Collection<BreakpointValue>> getTrueSample();

	public HashMap<String, Collection<BreakpointValue>> getFalseSample();

	public String getLogFile(); // eclipse may crash because Log print too many characters, thus use a file to store detail content
}
