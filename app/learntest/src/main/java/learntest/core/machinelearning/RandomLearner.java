/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.machinelearning;

import learntest.core.LearningMediator;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.data.sampling.SamplingResult;
import sav.common.core.SavException;

/**
 * @author LLT
 *	only do sampling randomly, not based on precondition.
 */
public class RandomLearner implements IInputLearner {
	private LearningMediator mediator;
	private int maxTcs;
	
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
		while (tc > 0) {
			int sampleTotal = tc < 100 ? tc : 100;
			selectiveSampling.selectData(inputProbes.getOriginalVars(), null, null, sampleTotal);
			tc = maxTcs - probes.getTotalTcs();
		}
		
		return probes;
	}
	
}
