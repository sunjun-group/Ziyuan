/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.sampling;

import icsetlv.InvariantMediator;
import icsetlv.common.dto.BreakpointData;
import icsetlv.common.dto.ExecVar;

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

/**
 * @author LLT
 */
public class SelectiveSampling implements ISelectiveSampling {
	private InvariantMediator mediator;
	private FormulaProcessor<ExecVar> dividerProcessor;
	private BreakPoint bkp;
	
	public SelectiveSampling(InvariantMediator mediator) {
		this.mediator = mediator;
	}
	
	public void setup(BreakPoint bkp, List<ExecVar> allVars) {
		this.bkp = bkp;
		dividerProcessor = new FormulaProcessor<ExecVar>(allVars);
	}
	
	@Override
	public List<DataPoint> selectData(Machine machine) {
		List<DataPoint> datapoints = new ArrayList<DataPoint>(machine.getDataPoints());
		Formula divider = machine.getLearnedLogic(dividerProcessor, true);
		
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
		List<BreakpointData> bkpData = mediator.instDebugAndCollectData(
											CollectionUtils.listOf(bkp), toInstrVarMap(assignments));
		return bkpData.get(0).toDatapoints(allLabels);
	}

	private Map<String, Object> toInstrVarMap(List<Eq<?>> assignments) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Eq<?> asgt : assignments) {
			map.put(asgt.getVar().getLabel(), asgt.getValue());
		}
		return map;
	}
}
