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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import libsvm.core.Category;
import libsvm.core.FormulaProcessor;
import libsvm.core.Machine;
import libsvm.core.Machine.DataPoint;
import libsvm.extension.ISelectiveSampling;
import sav.common.core.Logger;
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
	private Logger<?> log = Logger.getDefaultLogger();
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
		/* TODO: the param round = false should be the same to the value in machine.selectiveSampling()
		 * => do something 
		 */
		Formula divider = machine.getLearnedLogic(dividerProcessor, false);
		
		try {
			return execute(divider, machine.getDataLabels(), machine.getDataPoints());
		} catch (SavException e) {
			throw new SavRtException(e);
		}
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
		List<DataPoint> newPoints = new ArrayList<Machine.DataPoint>();
		if (divider == null) {
			return newPoints;
		}
//		List<ExecVar> labels = divider.getReferencedVariables();
		/* check boolean variables */ 
		/* */
		Map<String, Pair<Double, Double>> minMax = calculateValRange(allLabels, datapoints);
		
		IlpSolver solver = new IlpSolver(minMax, true);
		divider.accept(solver);
		List<List<Eq<?>>> assignments = solver.getResult();
		log.debug("Instrument values: ");
		for (List<Eq<?>> valSet : assignments) {
			if (datapointExistAlready(valSet, datapoints, allLabels)) {
				continue;
			}
			List<BreakpointData> bkpData = mediator.instDebugAndCollectData(
					CollectionUtils.listOf(bkp), toInstrVarMap(valSet));
			BreakpointData breakpointData = bkpData.get(0);
			Collection<? extends DataPoint> points = toDataPoints(allLabels, breakpointData);
			/*
			 * if new data point exist in both negative and positive set ->
			 * cannot divide, we can stop trying another point
			 */
			log.debug(valSet);
			newPoints.addAll(points);
			if (points.size() > 1) {
				break;
			}
			/* only select 1 point 
			 * TODO LLT: just for test (performance reason)
			 * */
//			break;
		}
		return newPoints;
	}

	private boolean datapointExistAlready(List<Eq<?>> valSet,
			List<DataPoint> datapoints, List<String> allLabels) {
		double[] newVals = new double[allLabels.size()];
		for (Eq<?> ass : valSet) {
			int idx = allLabels.indexOf(ass.getVar().getLabel());
			newVals[idx] = (Integer) ass.getValue();
		}
		for (DataPoint dp : datapoints) {
			if (Arrays.equals(dp.getValues(), newVals)) {
				return true;
			}
		}
		return false;
	}

	private Collection<? extends DataPoint> toDataPoints(List<String> allLabels, BreakpointData breakpointData) {
		List<DataPoint> points = new ArrayList<Machine.DataPoint>();
		if (CollectionUtils.isNotEmpty(breakpointData.getPassValues())) {
			points.add(BreakpointData.toDataPoint(allLabels, breakpointData.getPassValues().get(0), Category.POSITIVE));
		}
		
		if (CollectionUtils.isNotEmpty(breakpointData.getFailValues())) {
			points.add(BreakpointData.toDataPoint(allLabels, breakpointData.getFailValues().get(0), Category.NEGATIVE));
		}
		return points;
	}

	private Map<String, Object> toInstrVarMap(List<Eq<?>> assignments) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Eq<?> asgt : assignments) {
			map.put(asgt.getVar().getLabel(), asgt.getValue());
		}
		return map;
	}
}
