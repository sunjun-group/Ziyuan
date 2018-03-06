/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.machinelearning;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import icsetlv.common.utils.BreakpointDataUtils;
import learntest.core.commons.data.decision.DecisionNodeProbe;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.machinelearning.calculator.OrCategoryCalculator;
import learntest.core.machinelearning.iface.ISampleExecutor;
import learntest.core.machinelearning.iface.ISampleResult;
import learntest.core.machinelearning.sampling.IlpSelectiveSampling;
import libsvm.core.Divider;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT
 *
 */
public class SelectiveSampling<T extends ISampleResult> {
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
		
		StringBuffer buf = new StringBuffer();
		for(double[] d: data){
			buf.append("[");
			for(double val: d){
				buf.append(val + ", ");				
			}
			buf.append("]");
		}
		buf.append("\n");
		
		return runData(data, vars);
	}
	
	public T selectData(List<ExecVar> vars, OrCategoryCalculator precondition, List<Divider> divider)
			throws SavException, SAVExecutionTimeOutException {
		List<double[]> data = selectiveSampling.selectData(vars, precondition, divider);
		return runData(data, vars);
	}

	public T runData(List<double[]> data, List<ExecVar> vars) throws SavException {
		if (CollectionUtils.isEmpty(data)) {
			return null;
		}
		return sampleExecutor.runSamples(data, vars);
	}

	public T selectDataForModel(DecisionNodeProbe nodeProbe, List<ExecVar> originalVars, 
			OrCategoryCalculator preconditions, List<Divider> learnedDividers) throws SavException {
		List<double[]> data = selectiveSampling.selectDataForModel(nodeProbe, originalVars, preconditions,
				learnedDividers);
		return runData(data, originalVars);
	}
	
	public T selectDataForModel(DecisionNodeProbe nodeProbe, List<ExecVar> originalVars, 
			OrCategoryCalculator preconditions, List<Divider> learnedDividers, boolean seperateDividers) throws SavException {
		List<double[]> data = new LinkedList<>();
		if (seperateDividers) {
			for (Divider divider : learnedDividers) {
				data.addAll(selectiveSampling.selectDataForModel(nodeProbe, originalVars, preconditions,
						Arrays.asList(divider)));
			}
		}else {
			data = selectiveSampling.selectDataForModel(nodeProbe, originalVars, preconditions,
					learnedDividers);
		}
		return runData(data, originalVars);
	}
	
}
