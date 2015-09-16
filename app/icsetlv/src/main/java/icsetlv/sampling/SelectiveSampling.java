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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import libsvm.core.Category;
import libsvm.core.FormulaProcessor;
import libsvm.core.Machine;
import libsvm.core.Machine.DataPoint;
import libsvm.extension.ISelectiveSampling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.SavRtException;
import sav.common.core.formula.Eq;
import sav.common.core.formula.Formula;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StringUtils;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT
 */
public class SelectiveSampling implements ISelectiveSampling {
	private static Logger log = LoggerFactory.getLogger(SelectiveSampling.class);
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
			/*
			 * we should not check if the datapoint already existed. because
			 * although the datapoint for this value already there, but the test
			 * result can be different
			 */
			List<BreakpointData> bkpData = mediator.instDebugAndCollectData(
					CollectionUtils.listOf(bkp), toInstrVarMap(valSet));
			if (bkpData.isEmpty()) {
				continue;
			}
			BreakpointData breakpointData = bkpData.get(0);
			Collection<? extends DataPoint> points = toDataPoints(allLabels, breakpointData);
			/*
			 * if new data point exist in both negative and positive set ->
			 * cannot divide, we can stop trying another point
			 */
			if (!points.isEmpty()) {
				log.debug(StringUtils.toStringNullToEmpty(valSet));
				log.debug(StringUtils.toStringNullToEmpty(points));
			}
			newPoints.addAll(points);
			if (points.size() > 1) {
				break;
			}
		}
		return newPoints;
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

	public Map<String, Object> toInstrVarMap(List<Eq<?>> assignments) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Eq<?> asgt : assignments) {
			map.put(asgt.getVar().getLabel(), asgt.getValue());
		}
		return map;
	}
}
