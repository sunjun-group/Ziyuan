package learntest.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import icsetlv.common.dto.BreakpointValue;
import learntest.breakpoint.data.DecisionLocation;
import learntest.testcase.data.BreakpointData;
import learntest.testcase.data.LoopTimesData;
import libsvm.core.Category;
import libsvm.core.CategoryCalculator;
import libsvm.core.FormulaProcessor;
import libsvm.core.Machine.DataPoint;
import libsvm.extension.PositiveSeparationMachine;
import libsvm.extension.RandomNegativePointSelection;
import sav.common.core.Pair;
import sav.common.core.formula.Formula;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;

public class DecisionLearner implements CategoryCalculator {
	
	protected static Logger log = LoggerFactory.getLogger(DecisionLearner.class);
	private PositiveSeparationMachine machine = new PositiveSeparationMachine(new RandomNegativePointSelection());
	private List<ExecVar> vars;
	private List<String> labels;
	private List<ExecVar> boolVars;
	
	public void learn(List<BreakpointData> bkpsData) {
		Map<DecisionLocation, Pair<String, String>> decisions = new HashMap<DecisionLocation, Pair<String, String>>(); 
		for (BreakpointData bkpData : bkpsData) {
			log.info("Start to learn at " + bkpData.getLocation());
			if (bkpData.getFalseValues().isEmpty() && bkpData.getTrueValues().isEmpty()) {
				log.info("Missing data");
				continue;
			}
			if (bkpData.getFalseValues().isEmpty()) {
				log.info("Missing false branch data");
				continue;
			}
			else if (bkpData.getTrueValues().isEmpty()) {
				log.info("Missing true branch data");
				continue;
			} else {
				if (vars == null && !collectAllVars(bkpData)) {
					log.info("Missing variables");
					break;
				}
				decisions.put(bkpData.getLocation(), learn(bkpData));
			}
		}
		Set<Entry<DecisionLocation, Pair<String, String>>> entrySet = decisions.entrySet();
		for (Entry<DecisionLocation, Pair<String, String>> entry : entrySet) {
			System.out.println(entry.getKey());
			Pair<String, String> formulas = entry.getValue();
			System.out.println("True/False Decision: " + formulas.first());
			if (formulas.second() != null) {
				System.out.println("One/More Decision: " + formulas.second());
			}
		}
	}
	
	private Pair<String, String> learn(BreakpointData bkpData) {
		machine.resetData();
		addDataPoints(vars, bkpData.getTrueValues(), Category.POSITIVE);
		addDataPoints(vars, bkpData.getFalseValues(), Category.NEGATIVE);
		machine.train();
		//Formula trueFlase = machine.getLearnedLogic(new FormulaProcessor<ExecVar>(vars), true);
		String trueFlase = machine.getLearnedLogic(true);
		String oneMore = null;
		if (bkpData instanceof LoopTimesData) {
			oneMore = learn((LoopTimesData)bkpData);
		}
		return new Pair<String, String>(trueFlase, oneMore);
	}
	
	private String learn(LoopTimesData loopData) {
		machine.resetData();
		addDataPoints(vars, loopData.getMoreTimesValues(), Category.POSITIVE);
		addDataPoints(vars, loopData.getOneTimeValues(), Category.NEGATIVE);
		machine.train();
		//return machine.getLearnedLogic(new FormulaProcessor<ExecVar>(vars), true);
		return machine.getLearnedLogic(true);
	}
	
	private boolean collectAllVars(BreakpointData bkpData) {
		Set<ExecVar> allVars = new HashSet<ExecVar>();
		for (ExecValue bkpVal : bkpData.getFalseValues()) {
			collectExecVar(bkpVal.getChildren(), allVars);
		}
		for (ExecValue bkpVal : bkpData.getTrueValues()) {
			collectExecVar(bkpVal.getChildren(), allVars);
		}
		vars = new ArrayList<ExecVar>(allVars);
		if (vars.isEmpty()) {
			return false;
		}
		boolVars = extractBoolVars(vars);
		vars.removeAll(boolVars);
		labels = extractLabels(vars);
		machine.setDataLabels(labels);
		machine.setDefaultParams();
		return true;
	}
	
	private void collectExecVar(List<ExecValue> vals, Set<ExecVar> vars) {
		if (CollectionUtils.isEmpty(vals)) {
			return;
		}
		for (ExecValue val : vals) {
			if (val == null || CollectionUtils.isEmpty(val.getChildren())) {
				String varId = val.getVarId();
				vars.add(new ExecVar(varId, val.getType()));
			}
			collectExecVar(val.getChildren(), vars);
		}
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
	
	private List<String> extractLabels(List<ExecVar> allVars) {
		List<String> labels = new ArrayList<String>(allVars.size());
		for (ExecVar var : allVars) {
			labels.add(var.getVarId());
		}
		return labels;
	}
	
	private void addDataPoints(List<ExecVar> vars, List<BreakpointValue> values, Category category) {
		for (BreakpointValue value : values) {
			addDataPoint(vars, value, category);
		}
	}
	
	private void addDataPoint(List<ExecVar> vars, BreakpointValue bValue, Category category) {
		double[] lineVals = new double[vars.size()];
		int i = 0;
		for (ExecVar var : vars) {
			final Double value = bValue.getValue(var.getLabel(), 0.0);
			lineVals[i++] = value;
		}

		machine.addDataPoint(category, lineVals);
	}

	@Override
	public Category getCategory(DataPoint dataPoint) {
		return null;
	}

}
