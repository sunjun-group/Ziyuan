package learntest.main;

import java.util.ArrayList;
import java.util.Collections;
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
import learntest.calculator.OrCategoryCalculator;
import learntest.cfg.traveller.CfgConditionManager;
import learntest.sampling.SelectiveSampling;
import learntest.svm.MyPositiveSeparationMachine;
import learntest.testcase.data.BreakpointData;
import learntest.testcase.data.LoopTimesData;
import libsvm.svm_model;
import libsvm.core.Category;
import libsvm.core.CategoryCalculator;
import libsvm.core.Divider;
import libsvm.core.FormulaProcessor;
import libsvm.core.KernelType;
import libsvm.core.Machine;
import libsvm.core.MachineType;
import libsvm.core.Machine.DataPoint;
import libsvm.core.Model;
import libsvm.core.Parameter;
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
	private Machine oneClass;
	private List<ExecVar> vars;
	private List<String> labels;
	private List<ExecVar> boolVars;
	private SelectiveSampling selectiveSampling;
	
	private CfgConditionManager manager;
	private List<Divider> curDividers;
	
	private final int MAX_ATTEMPT = 10;
	
	public DecisionLearner(SelectiveSampling selectiveSampling, CfgConditionManager manager) {
		machine = new MyPositiveSeparationMachine();
		machine.setDefaultParams();
		oneClass = new Machine();
		oneClass.setParameter(new Parameter().setMachineType(MachineType.ONE_CLASS)
				.setKernelType(KernelType.LINEAR).setEps(0.00001).setUseShrinking(false)
				.setPredictProbability(false).setC(Double.MAX_VALUE));
		this.selectiveSampling = selectiveSampling;
		this.manager = manager;
	}
	
	public void learn(List<BreakpointData> bkpsData) throws SavException {
		Collections.sort(bkpsData);
		Map<DecisionLocation, Pair<Formula, Formula>> decisions = new HashMap<DecisionLocation, Pair<Formula, Formula>>(); 
		for (BreakpointData bkpData : bkpsData) {
			log.info("Start to learn at " + bkpData.getLocation());
			if (bkpData.getFalseValues().isEmpty() && bkpData.getTrueValues().isEmpty()) {
				log.info("Missing data");
				continue;
			}
			if (vars == null && !collectAllVars(bkpData)) {
				log.info("Missing variables");
				continue;
			}
			Pair<Formula, Formula> res = learn(bkpData);
			manager.setCondition(bkpData.getLocation().getLineNo(), res, curDividers);
			decisions.put(bkpData.getLocation(), res);
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
		OrCategoryCalculator preconditions = manager.getPreConditions(bkpData.getLocation().getLineNo());
		oneClass.resetData();
		BreakpointData oneClassData = bkpData;
		int times = 0;
		boolean missTrue = oneClassData.getTrueValues().isEmpty();
		boolean missFalse = oneClassData.getFalseValues().isEmpty();
		while ((missFalse || missTrue) && times < MAX_ATTEMPT) {
			addDataPoints(vars, oneClassData.getTrueValues(), Category.POSITIVE, oneClass);
			addDataPoints(vars, oneClassData.getFalseValues(), Category.NEGATIVE, oneClass);
			oneClass.train();
			Formula boundary = oneClass.getLearnedLogic(new FormulaProcessor<ExecVar>(vars), true);
			BreakpointData newData = selectiveSampling.selectData(oneClassData.getLocation(), 
					boundary, oneClass.getDataLabels(), oneClass.getDataPoints());
			if (newData == null) {
				break;
			}
			missTrue &= newData.getTrueValues().isEmpty();
			missFalse &= newData.getFalseValues().isEmpty();
			oneClassData = newData;
			times ++;
		}
		if (missTrue) {
			log.info("Missing true branch data");
			return new Pair<Formula, Formula>(null, null);
		}
		if (missFalse) {
			log.info("Missing false branch data");
			return new Pair<Formula, Formula>(null, null);
		}
		if (bkpData.getTrueValues().isEmpty() || bkpData.getFalseValues().isEmpty()) {
			bkpData.merge(oneClassData);
		}

		times = 0;
		machine.resetData();
		addDataPoints(vars, bkpData.getTrueValues(), Category.POSITIVE, machine);
		addDataPoints(vars, bkpData.getFalseValues(), Category.NEGATIVE, machine);
		machine.train();
		Formula trueFlase = getLearnedFormula();
		while(times < MAX_ATTEMPT) {
			BreakpointData newData = selectiveSampling.selectData(bkpData.getLocation(), 
					trueFlase, machine.getDataLabels(), machine.getDataPoints());
			if (newData == null) {
				break;
			}
			bkpData.merge(newData);
			addDataPoints(vars, newData.getTrueValues(), Category.POSITIVE, machine);
			addDataPoints(vars, newData.getFalseValues(), Category.NEGATIVE, machine);
			machine.train();
			Formula tmp = getLearnedFormula();
			if (!tmp.equals(trueFlase)) {
				trueFlase = tmp;
			} else {
				break;
			}
			times ++;
		}
		curDividers = machine.getLearnedDividers();
		
		Formula oneMore = null;
		if (bkpData instanceof LoopTimesData) {
			oneMore = learn((LoopTimesData)bkpData);
		}
		
		return new Pair<Formula, Formula>(trueFlase, oneMore);
	}
	
	private Formula learn(LoopTimesData loopData) throws SavException {
		oneClass.resetData();
		int times = 0;
		LoopTimesData oneClassData = loopData;
		boolean missOnce = oneClassData.getOneTimeValues().isEmpty();
		boolean missMore = oneClassData.getMoreTimesValues().isEmpty();
		while ((missOnce || missMore) && times < MAX_ATTEMPT) {
			addDataPoints(vars, oneClassData.getMoreTimesValues(), Category.POSITIVE, oneClass);
			addDataPoints(vars, oneClassData.getOneTimeValues(), Category.NEGATIVE, oneClass);
			oneClass.train();
			Formula boundary = oneClass.getLearnedLogic(new FormulaProcessor<ExecVar>(vars), true);
			LoopTimesData newData = (LoopTimesData) selectiveSampling.selectData(oneClassData.getLocation(), 
					boundary, oneClass.getDataLabels(), oneClass.getDataPoints());
			if (newData == null) {
				break;
			}
			missOnce &= newData.getOneTimeValues().isEmpty();
			missMore &= newData.getMoreTimesValues().isEmpty();
			oneClassData = newData;
			times ++;
		}
		if (missOnce) {
			log.info("Missing once loop data");
			return null;
		} 
		if (missMore) {
			log.info("Missing more than once loop data");
			return null;
		}
		if (loopData.getOneTimeValues().isEmpty() || loopData.getMoreTimesValues().isEmpty()) {
			loopData.merge(oneClassData);
		}
		
		times = 0;
		machine.resetData();
		addDataPoints(vars, loopData.getMoreTimesValues(), Category.POSITIVE, machine);
		addDataPoints(vars, loopData.getOneTimeValues(), Category.NEGATIVE, machine);
		machine.train();
		Formula formula = getLearnedFormula();
		while(times < MAX_ATTEMPT) {
			LoopTimesData newData = (LoopTimesData) selectiveSampling.selectData(loopData.getLocation(), 
					formula, machine.getDataLabels(), machine.getDataPoints());	
			if (newData == null) {
				break;
			}
			addDataPoints(vars, newData.getMoreTimesValues(), Category.POSITIVE, machine);
			addDataPoints(vars, newData.getOneTimeValues(), Category.NEGATIVE, machine);
			machine.train();
			Formula tmp = getLearnedFormula();
			if (!tmp.equals(formula)) {
				formula = tmp;
			} else {
				break;
			}
			times ++;
		}
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
		oneClass.setDataLabels(labels);
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
	
	private void addDataPoints(List<ExecVar> vars, List<BreakpointValue> values, Category category, Machine machine) {
		for (BreakpointValue value : values) {
			addDataPoint(vars, value, category, machine);
		}
	}
	
	private void addDataPoint(List<ExecVar> vars, BreakpointValue bValue, Category category, Machine machine) {
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
