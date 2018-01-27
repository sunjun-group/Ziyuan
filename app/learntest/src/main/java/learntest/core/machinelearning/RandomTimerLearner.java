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

import icsetlv.common.dto.BreakpointValue;
import learntest.core.LearningMediator;
import learntest.core.RunTimeInfo;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.data.sampling.SamplingResult;
import learntest.core.time.CovTimer;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.settings.SAVTimer;
import variable.Variable;

/**
 * @author LLT
 *	only do sampling randomly, not based on precondition.
 */
public class RandomTimerLearner implements IInputLearner {
	private static final Logger log = LoggerFactory.getLogger(RandomTimerLearner.class);
	private LearningMediator mediator;
	HashMap<String, Collection<BreakpointValue>> branchTrueRecord = new HashMap<>(), branchFalseRecord = new HashMap<>();
	private String logFile;
	private List<Pair<Integer, Double>> timeLine;
	
	public RandomTimerLearner(LearningMediator mediator,String logFile) {
		this.mediator = mediator;
		this.logFile = logFile;
		RunTimeInfo.createFile(logFile);
	}
	
	@Override
	public DecisionProbes learn(DecisionProbes inputProbes, Map<Integer, List<Variable>> relevantVarMap) throws SavException {
		CovTimer timer = new CovTimer(inputProbes, SAVTimer.getExecutionTime());
		timer.start();	
		
		DecisionProbes probes = inputProbes;
		SampleExecutor sampleExecutor = new SampleExecutor(mediator, inputProbes);

		SelectiveSampling<SamplingResult> selectiveSampling = new SelectiveSampling<SamplingResult>(sampleExecutor, inputProbes);
		try {
			while (!CovTimer.stopFlag) {
				int sampleTotal = 20;
				SamplingResult sampleResult = selectiveSampling.selectData(inputProbes.getOriginalVars(), null, null, sampleTotal); /** gentest and run test cases*/
				recordSample(inputProbes, sampleResult, logFile);
			}
		} finally {
			timer.close();
			timer.recordCovTimeLine(probes);
			timeLine= timer.getCovTimeLine();
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

	@Override
	public void cleanup() {
	}
	
	@Override
	public List<Pair<Integer, Double>> getCovTimeLine() {
		return timeLine;
	}
	
}
