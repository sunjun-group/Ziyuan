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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.tools.javac.util.Pair;

import icsetlv.common.dto.BreakpointValue;
import learntest.core.LearningMediator;
import learntest.core.commons.data.decision.DecisionNodeProbe;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.data.decision.INodeCoveredData;
import learntest.core.commons.data.sampling.SamplingResult;
import libsvm.core.Category;
import sav.common.core.SavException;

/**
 * @author LLT
 *	only do sampling randomly, not based on precondition.
 */
public class RandomLearner implements IInputLearner {
	private static final Logger log = LoggerFactory.getLogger(RandomLearner.class);
	private LearningMediator mediator;
	private int maxTcs;
	HashMap<DecisionNodeProbe, Collection<BreakpointValue>> branchTrueRecord = new HashMap<>(), branchFalseRecord = new HashMap<>();
	
	
	public RandomLearner(LearningMediator mediator, int maxTcs) {
		this.mediator = mediator;
		this.maxTcs = maxTcs;
	}
	
	@Override
	public DecisionProbes learn(DecisionProbes inputProbes) throws SavException {
		DecisionProbes probes = inputProbes;
		SampleExecutor sampleExecutor = new SampleExecutor(mediator, inputProbes);
		SelectiveSampling<SamplingResult> selectiveSampling = new SelectiveSampling<SamplingResult>(sampleExecutor, inputProbes);
		int tc = maxTcs - probes.getTotalTcs();
		int failToSelectSample = 0;
		while (tc > 0) {
			int sampleTotal = tc < 100 ? tc : 100;
			SamplingResult sampleResult = selectiveSampling.selectData(inputProbes.getOriginalVars(), null, null, sampleTotal); /** gentest and run test cases*/
			for (DecisionNodeProbe nodeProbe : inputProbes.getNodeProbes()) {
				INodeCoveredData newData = sampleResult.getNewData(nodeProbe);
				System.out.println(nodeProbe.getNode());
				log.info("true data after selective sampling" + newData.getTrueValues());
				log.info("false data after selective sampling" + newData.getFalseValues());
				recordSample(nodeProbe, sampleResult);
			}
			
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

	public HashMap<DecisionNodeProbe, Collection<BreakpointValue>> getTrueSample(){
		return branchTrueRecord;
	}
	
	public HashMap<DecisionNodeProbe, Collection<BreakpointValue>> getFalseSample(){
		return branchFalseRecord;
	}
	
}
