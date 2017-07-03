/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.machinelearning;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import icsetlv.common.utils.BreakpointDataUtils;
import learntest.calculator.OrCategoryCalculator;
import learntest.core.commons.data.decision.DecisionNodeProbe;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.machinelearning.iface.ISampleExecutor;
import learntest.core.machinelearning.iface.ISampleResult;
import libsvm.core.Divider;
import libsvm.core.Machine.DataPoint;
import sav.common.core.SavException;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT
 *
 */
public class SelectiveSampling<T extends ISampleResult> {
	private static Logger log = LoggerFactory.getLogger(SelectiveSampling.class);
	private ISampleExecutor<T> sampleExecutor;
	private IlpSelectiveSampling selectiveSampling;
	
	public SelectiveSampling(ISampleExecutor<T> sampleExecutor, DecisionProbes decisionProbes) {
		this.sampleExecutor = sampleExecutor;
		selectiveSampling = new IlpSelectiveSampling(decisionProbes.getOriginalVars(),
				BreakpointDataUtils.toDataPoint(decisionProbes.getOriginalVars(), decisionProbes.getTestInputs()));
	}

	public T selectData(List<ExecVar> vars, OrCategoryCalculator precondition, List<Divider> divider, int maxTcs)
			throws SavException, SAVExecutionTimeOutException {
		List<double[]> data = selectiveSampling.selectData(vars, precondition, divider, maxTcs);
		return runData(data, vars);
	}
	
	public T selectData(List<ExecVar> vars, OrCategoryCalculator precondition, List<Divider> divider)
			throws SavException, SAVExecutionTimeOutException {
		List<double[]> data = selectiveSampling.selectData(vars, precondition, divider);
		return runData(data, vars);
	}

	private T runData(List<double[]> data, List<ExecVar> vars) throws SavException {
		return sampleExecutor.runSamples(data, vars);
	}

	public T selectDataForModel(DecisionNodeProbe nodeProbe, List<ExecVar> originalVars, List<DataPoint> dataPoints,
			OrCategoryCalculator preconditions, List<Divider> learnedDividers) throws SavException {
		List<double[]> data = selectiveSampling.selectDataForModel(nodeProbe, originalVars, dataPoints, preconditions,
				learnedDividers);
		return runData(data, originalVars);
	}
	
}
