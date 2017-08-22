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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import icsetlv.common.dto.BreakpointData;
import icsetlv.common.dto.BreakpointValue;
import learntest.core.LearningMediator;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.data.sampling.SamplingResult;
import sav.common.core.SavException;
import variable.Variable;

/**
 * @author LLT
 *	only do sampling randomly, not based on precondition.
 */
public class RandomLearner implements IInputLearner {
	private static final Logger log = LoggerFactory.getLogger(RandomLearner.class);
	private LearningMediator mediator;
	private int maxTcs;
	HashMap<String, Collection<BreakpointValue>> branchTrueRecord = new HashMap<>(), branchFalseRecord = new HashMap<>();
	private String logFile;
	
	public RandomLearner(LearningMediator mediator, int maxTcs, String logFile) {
		this.mediator = mediator;
		this.maxTcs = maxTcs;
		this.logFile = logFile;
	}
	
	@Override
	public DecisionProbes learn(DecisionProbes inputProbes, BreakpointData bpdata, Map<Integer, List<Variable>> relevantVarMap) throws SavException {
		DecisionProbes probes = inputProbes;
		SampleExecutor sampleExecutor = new SampleExecutor(mediator, inputProbes);

		SelectiveSampling<SamplingResult> selectiveSampling = new SelectiveSampling<SamplingResult>(sampleExecutor, inputProbes);
		int tc = maxTcs - probes.getTotalTcs();
		int failToSelectSample = 0;
		while (tc > 0) {
			int sampleTotal = tc < 100 ? tc : 100;
			SamplingResult sampleResult = selectiveSampling.selectData(inputProbes.getOriginalVars(), null, null, sampleTotal); /** gentest and run test cases*/
			recordSample(inputProbes, sampleResult, logFile);
			
			int remainTc = maxTcs - probes.getTotalTcs();
			if (remainTc == tc) {
				if (failToSelectSample == 5) {
					log.warn("cannot select any more sample!");
					break;
				} else {
					failToSelectSample++;
				}
			} else {
				failToSelectSample = 0;
				tc = remainTc;
			}
		}
		
		return probes;
	}

	public HashMap<String, Collection<BreakpointValue>> getTrueSample(){
		return branchTrueRecord;
	}
	
	public HashMap<String, Collection<BreakpointValue>> getFalseSample(){
		return branchFalseRecord;
	}

	@Override
	public String getLogFile() {
		return logFile;
	}
	
	
}
