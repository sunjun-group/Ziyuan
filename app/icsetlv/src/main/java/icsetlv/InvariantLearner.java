/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv;

import icsetlv.common.dto.BkpInvariantResult;
import icsetlv.common.dto.BreakpointData;
import icsetlv.common.dto.BreakpointValue;
import icsetlv.common.dto.ExecValue;
import icsetlv.common.dto.ExecVar;
import icsetlv.common.dto.ExecVarType;
import icsetlv.sampling.SelectiveSampling;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import libsvm.core.Category;
import libsvm.core.FormulaProcessor;
import libsvm.core.Machine;
import libsvm.extension.ISelectiveSampling;

import org.apache.log4j.Logger;

import sav.common.core.formula.Formula;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.BreakPoint;

/**
 * @author LLT
 *
 */
public class InvariantLearner {
	private static final Logger LOGGER = Logger.getLogger(InvariantLearner.class);
	private InvariantMediator mediator;
	private Machine machine;
	
	public InvariantLearner(InvariantMediator mediator) {
		this.mediator = mediator;
		machine = mediator.getMachine();
	}

	public List<BkpInvariantResult> learn(List<BreakpointData> bkpsData) {
		List<BkpInvariantResult> result = new ArrayList<BkpInvariantResult>();
		for (BreakpointData bkpData : bkpsData) {
			LOGGER.info("Start to learn at " + bkpData.getBkp());
			if (bkpData.getPassValues().isEmpty() && bkpData.getFailValues().isEmpty()) {
				continue;
			}
			Formula formula = null;
			if (bkpData.getFailValues().isEmpty()) {
				LOGGER.info("This line is likely not a bug!");
				formula = Formula.TRUE;
			} else if (bkpData.getPassValues().isEmpty()) {
				LOGGER.info("This line is likely a bug!");
				formula = Formula.FALSE;
			} else {
				/* collect variable labels */
				List<ExecVar> allVars = collectAllVars(bkpData);
				if (allVars.isEmpty()) {
					continue;
				}

				ISelectiveSampling handler = getSelectiveSampling(bkpData.getBkp(), allVars);
				machine.setSelectiveSamplingHandler(handler);

				formula = learn(bkpData, allVars);
			}
			result.add(new BkpInvariantResult(bkpData.getBkp(), formula));
		}
		return result;
	}

	public ISelectiveSampling getSelectiveSampling(BreakPoint breakpoint, List<ExecVar> allVars) {
		SelectiveSampling handler = new SelectiveSampling(mediator);
		handler.setup(breakpoint, allVars);
		return handler;
	}
	
	/**
	 * apply svm 
	 */
	private Formula learn(BreakpointData bkpData, List<ExecVar> allVars) {
		mediator.logBkpData(bkpData, allVars);
		/* handle boolean variables first */
		Formula formula = learnFromBoolVars(extractBoolVars(allVars), bkpData);
		if (formula != null) {
			return formula;
		}
		/* find divider for all variables */
		// Configure data for SVM machine
		machine.resetData();
		List<String> allLables = extractLabels(allVars);
		machine.setDataLabels(allLables);
		addDataPoints(bkpData.getPassValues(), bkpData.getFailValues());
		machine.train();
		return machine.getLearnedLogic(new FormulaProcessor<ExecVar>(allVars), true);
	}
	
	private void addDataPoints(List<BreakpointValue> passValues,
			List<BreakpointValue> failValues) {
		for (BreakpointValue bValue : passValues) {
			addDataPoint(bValue, Category.POSITIVE);
		}

		for (BreakpointValue bValue : failValues) {
			addDataPoint(bValue, Category.NEGATIVE);
		}
	}

	private void addDataPoint(BreakpointValue bValue, Category category) {
		double[] lineVals = new double[machine.getNumberOfFeatures()];
		int i = 0;
		if (machine.isGeneratedDataLabels()) {
			// Actually we do not use this case at the moment but I still add this for completeness
			lineVals = bValue.getAllValues();
		} else {
			for (String variableName : machine.getDataLabels()) {
				final Double value = bValue.getValue(variableName, 0.0);
				lineVals[i++] = value;
			}
		}

		machine.addDataPoint(category, lineVals);
	}

	private List<String> extractLabels(List<ExecVar> allVars) {
		List<String> labels = new ArrayList<String>(allVars.size());
		for (ExecVar var : allVars) {
			labels.add(var.getVarId());
		}
		return labels;
	}

	private List<ExecVar> collectAllVars(BreakpointData bkpData) {
		Set<ExecVar> allVars = new HashSet<ExecVar>();
		for (ExecValue bkpVal : bkpData.getFailValues()) {
			collectExecVar(bkpVal.getChildren(), allVars);
		}
		for (ExecValue bkpVal : bkpData.getPassValues()) {
			collectExecVar(bkpVal.getChildren(), allVars);
		}
		return new ArrayList<ExecVar>(allVars);
	}
	
	private void collectExecVar(List<ExecValue> vals, Set<ExecVar> vars) {
		if (CollectionUtils.isEmpty(vals)) {
			return;
		}
		for (ExecValue val : vals) {
			String varId = val.getVarId();
			vars.add(new ExecVar(varId, val.getType()));
			collectExecVar(val.getChildren(), vars);
		}
	}

	private Formula learnFromBoolVars(List<ExecVar> boolVars, BreakpointData bkpData) {
		BooleanDivider divider = new BooleanDivider();
		return divider.divide(boolVars, bkpData);
	}

	private List<ExecVar> extractBoolVars(List<ExecVar> allVars) {
		List<ExecVar> result = new ArrayList<ExecVar>();
		for (ExecVar var : allVars) {
			if (var.getType() == ExecVarType.BOOLEAN) {
				result.add(var);
			}
		}
		return result;
	}

}
