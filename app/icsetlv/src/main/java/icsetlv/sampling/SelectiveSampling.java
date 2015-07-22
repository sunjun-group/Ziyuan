/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.sampling;

import icsetlv.DefaultValues;
import icsetlv.InvariantLearner;
import icsetlv.common.dto.BkpInvariantResult;
import icsetlv.common.dto.BreakpointData;
import icsetlv.common.dto.ExecVar;
import icsetlv.variable.TestcasesExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import libsvm.core.FormulaProcessor;
import libsvm.core.Machine;
import libsvm.core.Machine.DataPoint;
import libsvm.extension.ISelectiveSampling;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.SavRtException;
import sav.common.core.formula.Eq;
import sav.common.core.formula.Formula;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.BreakPoint;
import sav.strategies.vm.VMConfiguration;

/**
 * @author LLT
 */
public class SelectiveSampling implements ISelectiveSampling {
	private TestcasesExecutor tcExecutor;
	private BkpInstrument instrument;
	private FormulaProcessor<ExecVar> dividerProcessor;
	
	public SelectiveSampling(List<ExecVar> allVars) {
		dividerProcessor = new FormulaProcessor<ExecVar>(allVars);
	}
	
	@Override
	public List<DataPoint> selectData(Machine machine) {
		List<DataPoint> datapoints = new ArrayList<DataPoint>(machine.getDataPoints());
		Formula divider = machine.getLearnedLogic(dividerProcessor);
		
		try {
			datapoints.addAll(execute(divider, machine.getDataLabels(), machine.getDataPoints()));
		} catch (SavException e) {
			throw new SavRtException(e);
		}
		return datapoints;
	}
	
	private Map<String, Pair<Double, Double>> calculateValRange(
			List<String> dataLabels, List<DataPoint> dataPoints) {
		Map<String, Pair<Double, Double>> minMax = new HashMap<String, Pair<Double,Double>>();
		for (DataPoint dp : dataPoints) {
			for (int i = 0; i < dataLabels.size(); i++) {
				double val = dp.getValue(i);
				String label = dataLabels.get(i);
				Pair<Double, Double> mm = minMax.get(label);
				if (mm == null) {
					mm = new Pair<Double, Double>(val, val);
					minMax.put(label, mm);
				}
				/* min */
				if (mm.a.doubleValue() > val) {
					mm.a = val;
				}
				/* max */
				if (mm.b.doubleValue() < val) {
					mm.b = val;
				}
			}
		}
		return minMax;
	}

	public List<DataPoint> execute(Formula divider, List<String> allLabels, List<DataPoint> datapoints) throws SavException {
		List<ExecVar> labels = divider.getReferencedVariables();
		/* check boolean variables */
		/* */
		Map<String, Pair<Double, Double>> minMax = calculateValRange(allLabels, datapoints);
		
		IlpSolver solver = new IlpSolver(minMax);
		divider.accept(solver);
		List<Eq<?>> assignments = solver.getResult();
		BreakPoint newBkp = instrument.instrument(assignments);
		List<BreakpointData> bkpData = debugTestAndCollectData(CollectionUtils.listOf(newBkp));
		return bkpData.get(0).toDatapoints(allLabels);
	}

	public List<BkpInvariantResult> learn(VMConfiguration config, List<String> allTests,
			List<BreakPoint> bkps) throws SavException {
		List<BreakpointData> bkpsData = debugTestAndCollectData(config, allTests, bkps);
		InvariantLearner learner = new InvariantLearner(new Machine());
		return learner.learn(bkpsData);
	}

	private List<BreakpointData> debugTestAndCollectData(VMConfiguration config,
			List<String> allTests, List<BreakPoint> bkps) throws SavException {
		ensureTcExecutor();
		tcExecutor.setup(config, allTests);
		return debugTestAndCollectData(bkps);
	}
	
	private List<BreakpointData> debugTestAndCollectData(List<BreakPoint> bkps)
			throws SavException {
		tcExecutor.run(bkps);
		return tcExecutor.getResult();
	}
	
	public void ensureTcExecutor() {
		if (tcExecutor == null) {
			tcExecutor = new TestcasesExecutor(DefaultValues.DEBUG_VALUE_RETRIEVE_LEVEL); 
		}
	}
	
	public void setTcExecutor(TestcasesExecutor tcExecutor) {
		this.tcExecutor = tcExecutor;
	}
	
	public void setInstrument(BkpInstrument instrument) {
		this.instrument = instrument;
	}
}
