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
import learntest.sampling.SelectiveSampling;
import learntest.svm.MyPositiveSeparationMachine;
import learntest.testcase.data.BreakpointData;
import learntest.testcase.data.LoopTimesData;
import libsvm.svm_model;
import libsvm.core.Category;
import libsvm.core.CategoryCalculator;
import libsvm.core.Divider;
import libsvm.core.FormulaProcessor;
import libsvm.core.Machine.DataPoint;
import libsvm.core.Model;
import libsvm.extension.ByDistanceNegativePointSelection;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.formula.AndFormula;
import sav.common.core.formula.Formula;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;

public class DecisionLearner implements CategoryCalculator {
	
	protected static Logger log = LoggerFactory.getLogger(DecisionLearner.class);
	private MyPositiveSeparationMachine machine;
	private List<ExecVar> vars;
	private List<String> labels;
	private List<ExecVar> boolVars;
	private SelectiveSampling selectiveSampling;
	
	public DecisionLearner(SelectiveSampling selectiveSampling) {
		machine = new MyPositiveSeparationMachine(new ByDistanceNegativePointSelection());
		machine.setDefaultParams();
		this.selectiveSampling = selectiveSampling;
	}
	
	public void learn(List<BreakpointData> bkpsData) throws SavException {
		Map<DecisionLocation, Pair<Formula, Formula>> decisions = new HashMap<DecisionLocation, Pair<Formula, Formula>>(); 
		for (BreakpointData bkpData : bkpsData) {
			log.info("Start to learn at " + bkpData.getLocation());
			if (bkpData.getFalseValues().isEmpty() && bkpData.getTrueValues().isEmpty()) {
				log.info("Missing data");
				continue;
			}
			if (bkpData.getFalseValues().isEmpty()) {
				log.info("Missing false branch data");
				continue;
			} else if (bkpData.getTrueValues().isEmpty()) {
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
		Set<Entry<DecisionLocation, Pair<Formula, Formula>>> entrySet = decisions.entrySet();
		for (Entry<DecisionLocation, Pair<Formula, Formula>> entry : entrySet) {
			System.out.println(entry.getKey());
			Pair<Formula, Formula> formulas = entry.getValue();
			System.out.println("True/False Decision: " + formulas.first());
			if (formulas.second() != null) {
				System.out.println("One/More Decision: " + formulas.second());
			}
		}
	}
	
	private Pair<Formula, Formula> learn(BreakpointData bkpData) throws SavException {
		machine.resetData();
		addDataPoints(vars, bkpData.getTrueValues(), Category.POSITIVE);
		addDataPoints(vars, bkpData.getFalseValues(), Category.NEGATIVE);
		machine.train();
		Formula trueFlase = getLearnedFormula();
		while(true) {
			BreakpointData newData = selectiveSampling.selectData(bkpData.getLocation(), 
					trueFlase, machine.getDataLabels(), machine.getDataPoints());
			if (newData == null) {
				break;
			}
			addDataPoints(vars, newData.getTrueValues(), Category.POSITIVE);
			addDataPoints(vars, newData.getFalseValues(), Category.NEGATIVE);
			double acc = machine.getModelAccuracy();
			if (acc == 1.0) {
				break;
			}
			machine.train();
			Formula tmp = getLearnedFormula();
			/*System.out.println(trueFlase);
			System.out.println(tmp);*/
			if (!tmp.equals(trueFlase)) {
				trueFlase = tmp;
			} else {
				break;
			}
		};
		
		Formula oneMore = null;
		if (bkpData instanceof LoopTimesData) {
			oneMore = learn((LoopTimesData)bkpData);
		}
		
		return new Pair<Formula, Formula>(trueFlase, oneMore);
	}
	
	private Formula learn(LoopTimesData loopData) throws SavException {
		if (loopData.getOneTimeValues().isEmpty()) {
			log.info("Missing once loop data");
			return null;
		} 
		if (loopData.getMoreTimesValues().isEmpty()) {
			log.info("Missing more than once loop data");
			return null;
		}
		machine.resetData();
		addDataPoints(vars, loopData.getMoreTimesValues(), Category.POSITIVE);
		addDataPoints(vars, loopData.getOneTimeValues(), Category.NEGATIVE);
		machine.train();
		Formula formula = getLearnedFormula();
		while(true) {
			LoopTimesData newData = (LoopTimesData) selectiveSampling.selectData(loopData.getLocation(), 
					formula, machine.getDataLabels(), machine.getDataPoints());	
			if (newData == null) {
				break;
			}
			addDataPoints(vars, newData.getMoreTimesValues(), Category.POSITIVE);
			addDataPoints(vars, newData.getOneTimeValues(), Category.NEGATIVE);
			double acc = machine.getModelAccuracy();
			if (acc == 1.0) {
				break;
			}
			machine.train();
			Formula tmp = getLearnedFormula();
			if (!tmp.equals(formula)) {
				formula = tmp;
			} else {
				break;
			}
		};
		return formula;
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

	private Formula getLearnedFormula() {
		Formula formula = null;
		List<svm_model> models = machine.getLearnedModels();
		final int numberOfFeatures = machine.getNumberOfFeatures();
		if (models != null && numberOfFeatures > 0) {			
			for (svm_model svmModel : models) {
				if (svmModel != null) {				
					final Divider explicitDivider = new Model(svmModel, numberOfFeatures).getExplicitDivider();
					Formula current = new FormulaProcessor<ExecVar>(vars).process(explicitDivider, machine.getDataLabels(), true);
					if (formula == null) {
						formula = current;
					} else {
						formula = new AndFormula(formula, current);
					}
				}
			}
		}
		return formula;
	}
	
	@Override
	public Category getCategory(DataPoint dataPoint) {
		return null;
	}

}
