package learntest.main;

import java.io.File;
import java.io.FileWriter;
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
import learntest.sampling.JacopSelectiveSampling;
import learntest.sampling.jacop.StoreSearcher;
import learntest.svm.MyPositiveSeparationMachine;
import learntest.testcase.data.BranchType;
import learntest.testcase.data.BreakpointData;
import learntest.testcase.data.LoopTimesData;
import libsvm.svm_model;
import libsvm.core.Category;
import libsvm.core.CategoryCalculator;
import libsvm.core.Divider;
import libsvm.core.FormulaProcessor;
import libsvm.core.Machine;
import libsvm.core.Machine.DataPoint;
import libsvm.core.Model;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.formula.AndFormula;
import sav.common.core.formula.Formula;
import sav.common.core.utils.CollectionUtils;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;

public class DecisionLearner implements CategoryCalculator {
	
	protected static Logger log = LoggerFactory.getLogger(DecisionLearner.class);
	private MyPositiveSeparationMachine machine;
	// one class does not perform well
	//private Machine oneClass;
	private List<ExecVar> originVars;
	private List<ExecVar> vars;
	private List<String> labels;
	//private Set<ExecVar> boolVars;
	private JacopSelectiveSampling selectiveSampling;
	
	private CfgConditionManager manager;
	private List<Divider> curDividers;
	
	private Map<DecisionLocation, BreakpointData> bkpDataMap;
	
	/**
	 * recorded test cases
	 */
	private List<BreakpointValue> recordedTestInputs;
	
	//private final int MAX_ATTEMPT = 10;
	//private final int MAX_TO_RECORD = 5;
	
	//private long startTime;
	
	private boolean random = true;
	
	private double curBranchNumber = 1;
	/**
	 * The first integer indicates the line number of condition, the second integer indicates branch: 0-false, 1-true/once, 2-more
	 * See {@link BranchType}
	 */
	private List<Pair<Integer, Integer>> branchRecord;
	
	private boolean needFalse = true;
	private boolean needTrue = true;
	private boolean needOne = true;
	private boolean needMore = true;
	
	public DecisionLearner(JacopSelectiveSampling selectiveSampling, CfgConditionManager manager, boolean random) {
		machine = new MyPositiveSeparationMachine();
		machine.setDefaultParams();
		/*oneClass = new Machine();
		oneClass.setParameter(new Parameter().setMachineType(MachineType.ONE_CLASS)
				.setKernelType(KernelType.LINEAR).setEps(0.00001).setUseShrinking(false)
				.setPredictProbability(false).setC(Double.MAX_VALUE));*/
		this.selectiveSampling = selectiveSampling;
		this.manager = manager;
		this.random = random;
	}
	
	public void learn(Map<DecisionLocation, BreakpointData> bkpDataMap) throws SavException, SAVExecutionTimeOutException {
		manager.updateRelevance(bkpDataMap);
		recordedTestInputs = new ArrayList<BreakpointValue>();
		branchRecord = new ArrayList<Pair<Integer,Integer>>();
		this.bkpDataMap = bkpDataMap;
		List<BreakpointData> bkpDatas = new ArrayList<BreakpointData>(bkpDataMap.values());
		Collections.sort(bkpDatas);
		/**
		 * each decision location has two formula, one for true/false and one for loop
		 */
		Map<DecisionLocation, Pair<Formula, Formula>> decisions = new HashMap<DecisionLocation, Pair<Formula, Formula>>();
		for (BreakpointData bkpData : bkpDatas) {
			System.out.println("Start to learn at " + bkpData.getLocation());
			if (bkpData.getFalseValues().isEmpty() && bkpData.getTrueValues().isEmpty()) {
				System.out.println("Cannot find any data at " + bkpData.getLocation());
				continue;
			}
			
			if (vars == null && !collectAllVars(bkpData)) {
				System.out.println("No variable collected at " + bkpData.getLocation());
				continue;
			}
			
			Pair<Formula, Formula> learnedClassifier = learn(bkpData);
			
			System.out.println("true or false classifier at " + bkpData.getLocation() + " is :" + learnedClassifier.first());
			manager.setCondition(bkpData.getLocation().getLineNo(), learnedClassifier, curDividers);
			
			/**
			 * used for debug
			 */
			decisions.put(bkpData.getLocation(), learnedClassifier);
		}
		
		if (!random) {
			logLearningProcessInFile(decisions);
		}		
	}

	private void logLearningProcessInFile(Map<DecisionLocation, Pair<Formula, Formula>> decisions) {
		Set<Entry<DecisionLocation, Pair<Formula, Formula>>> entrySet = decisions.entrySet();
		try {
			FileWriter writer = new FileWriter(new File("trees/" + LearnTestConfig.getSimpleClassName() + "." 
					+ LearnTestConfig.testMethodName));
			for (Entry<DecisionLocation, Pair<Formula, Formula>> entry : entrySet) {
				writer.write(entry.getKey() + "\n");
				Pair<Formula, Formula> formulas = entry.getValue();
				writer.write("True/False Decision: " + formulas.first() + "\n");
				if (entry.getKey().isLoop()) {
					writer.write("One/More Decision: " + formulas.second() + "\n");
				}
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Pair<Formula, Formula> learn(BreakpointData bkpData) throws SavException, SAVExecutionTimeOutException {
		needFalse = true;
		
		/**
		 * It means if begins to learn loop times data, the true branch must have been satisfied.
		 */
		if(bkpData instanceof LoopTimesData){
			needTrue = false;
		}
		else{
			needTrue = true;
		}
		
		OrCategoryCalculator preconditions = null;
		if (!random) {
			preconditions = manager.getPreConditions(bkpData.getLocation().getLineNo());
			/**
			 * TODO left by Lin Yun
			 * a design flaw, this method should not be put inside OrCategoryCalculator.
			 * 
			 */
			preconditions.clearInvalidData(bkpData);
		}
		
		if (bkpData.getTrueValues().isEmpty() || bkpData.getFalseValues().isEmpty()) {			
			long startTime = System.currentTimeMillis();
			Map<DecisionLocation, BreakpointData> selectMap = selectiveSampling.selectDataForEmpty(bkpData.getLocation(), originVars,
					preconditions, null, bkpData.getTrueValues().isEmpty(), false);
			System.out.println("learn select data for empty time: " + (System.currentTimeMillis() - startTime) + "ms");
			
			if (selectMap != null) {
				mergeMap(selectMap);
				manager.updateRelevance(bkpDataMap);
				//updateCoverage(bkpData);
				if (bkpData.getTrueValues().isEmpty() || bkpData.getFalseValues().isEmpty()) {
					selectMap = selectiveSampling.selectDataForEmpty(bkpData.getLocation(), originVars, 
							preconditions, null, bkpData.getTrueValues().isEmpty(), false);
					if (selectMap != null) {
						mergeMap(selectMap);
					} else {
						mergeMap(selectiveSampling.getSelectResult());
					}
					manager.updateRelevance(bkpDataMap);
					//updateCoverage(bkpData);
				}
				//bkpData = bkpDataMap.get(bkpData.getLocation());
			} else {
				mergeMap(selectiveSampling.getSelectResult());
				manager.updateRelevance(bkpDataMap);
			}
		}
		
		updateCoverage(bkpData);
				
		if (bkpData.getTrueValues().isEmpty()) {
			log.info("Missing true branch data");
			curDividers = null;
			return new Pair<Formula, Formula>(null, null);
		} else if (bkpData.getFalseValues().isEmpty()) {
			log.info("Missing false branch data");
			curDividers = null;
			Formula oneMoreFormula = null;
			if (bkpData instanceof LoopTimesData) {
				oneMoreFormula = generateLoopFormula((LoopTimesData)bkpData);
			}
			return new Pair<Formula, Formula>(null, oneMoreFormula);
		}
		
		/**
		 * TODO Left by Lin Yun
		 * I am not sure whether I can extract the code out there.
		 */
		if (random) {
			List<BreakpointValue> falseValues = bkpData.getFalseValues();
			for (BreakpointValue value : falseValues) {
				if (!recordedTestInputs.contains(value)) {
					recordedTestInputs.add(value);
				}
			}
			
			if (bkpData instanceof LoopTimesData) {
				generateLoopFormula((LoopTimesData)bkpData);
			} else {
				List<BreakpointValue> trueValues = bkpData.getTrueValues();
				for (BreakpointValue value : trueValues) {
					if (!recordedTestInputs.contains(value)) {
						recordedTestInputs.add(value);
					}
				}
			}
			
			return new Pair<Formula, Formula>(null, null);
		}
		
		Formula trueFlaseFormula = generateTrueFalseFormula(bkpData, preconditions);
		
		Formula oneMoreFormula = null;
		if (bkpData instanceof LoopTimesData) {
			oneMoreFormula = generateLoopFormula((LoopTimesData)bkpData);
		} 
		
		return new Pair<Formula, Formula>(trueFlaseFormula, oneMoreFormula);
	}

	private Formula generateTrueFalseFormula(BreakpointData bkpData, OrCategoryCalculator preconditions)
			throws SAVExecutionTimeOutException, SavException {
		Formula trueFlaseFormula = null;
		
		if (manager.isRelevant(bkpData.getLocation().getLineNo())) {
			
			int times = 0;
			machine.resetData();
			addDataPoints(originVars, bkpData.getTrueValues(), Category.POSITIVE, machine);
			addDataPoints(originVars, bkpData.getFalseValues(), Category.NEGATIVE, machine);
			//startTime = System.currentTimeMillis();
			machine.train();
			//System.out.println("learn model training time: " + (System.currentTimeMillis() - startTime) + " ms");
			trueFlaseFormula = getLearnedFormula();
			double acc = machine.getModelAccuracy();
			curDividers = machine.getLearnedDividers();
			while(trueFlaseFormula != null /*&& times < MAX_ATTEMPT*/ && manager.isRelevant(bkpData.getLocation().getLineNo())) {
				long startTime = System.currentTimeMillis();				
				Map<DecisionLocation, BreakpointData> newMap = selectiveSampling.selectDataForModel(bkpData.getLocation(), 
						originVars, machine.getDataPoints(), preconditions, machine.getLearnedDividers());
				System.out.println("learn select data: " + (System.currentTimeMillis() - startTime) + "ms");
				
				if (newMap == null) {
					break;
				}
				mergeMap(newMap);
				manager.updateRelevance(bkpDataMap);
				
				BreakpointData newData = newMap.get(bkpData.getLocation());
				if (newData == null) {
					break;
				}
				preconditions.clearInvalidData(newData);
				//manager.updateRelevance(bkpData);
				addDataPoints(originVars, newData.getTrueValues(), Category.POSITIVE, machine);
				addDataPoints(originVars, newData.getFalseValues(), Category.NEGATIVE, machine);
				
				//startTime = System.currentTimeMillis();
				acc = machine.getModelAccuracy();
				machine.train();
				//System.out.println("learn model training time: " + (System.currentTimeMillis() - startTime) + " ms");
				Formula tmp = getLearnedFormula();
				double accTmp = machine.getModelAccuracy();
				if (tmp == null) {
					trueFlaseFormula = null;
					curDividers = machine.getLearnedDividers();
					break;
				}
				if (!tmp.equals(trueFlaseFormula) && accTmp > acc) {
					trueFlaseFormula = tmp;
					curDividers = machine.getLearnedDividers();
					acc = accTmp;
				} else {
					break;
				}
				times ++;
			}
		}
		
		return trueFlaseFormula;
	}
	
	private Formula generateLoopFormula(LoopTimesData loopData) throws SavException, SAVExecutionTimeOutException {
		needOne = true;
		needMore = true;
		
		OrCategoryCalculator preConditions = null;
		if (!random) {
			preConditions = manager.getPreConditions(loopData.getLocation().getLineNo());			
		}
		
		if (loopData.getOneTimeValues().isEmpty() || loopData.getMoreTimesValues().isEmpty()) {
			//startTime = System.currentTimeMillis();
			Map<DecisionLocation, BreakpointData> selectMap 
				= selectiveSampling.selectDataForEmpty(loopData.getLocation(), originVars,
					preConditions, curDividers, loopData.getMoreTimesValues().isEmpty(), true);
			//System.out.println("learn select data for empty time: " + (System.currentTimeMillis() - startTime) + "ms");
			if (selectMap != null) {
				mergeMap(selectMap);			
			} else {
				mergeMap(selectiveSampling.getSelectResult());
			}
			manager.updateRelevance(bkpDataMap);
			//updateCoverage(loopData);	
			//loopData = (LoopTimesData) bkpDataMap.get(loopData.getLocation());
		}
		
		updateCoverage(loopData);
		
		if (loopData.getOneTimeValues().isEmpty()) {
			log.info("Missing once loop data");
			return null;
		} else if (loopData.getMoreTimesValues().isEmpty()) {
			log.info("Missing more than once loop data");
			return null;
		}
		
		if (random) {
			List<BreakpointValue> choices = loopData.getOneTimeValues();
			for (BreakpointValue value : choices) {
				if (!recordedTestInputs.contains(value)) {
					recordedTestInputs.add(value);
				}
			}
			choices = loopData.getMoreTimesValues();
			for (BreakpointValue value : choices) {
				if (!recordedTestInputs.contains(value)) {
					recordedTestInputs.add(value);
				}
			}
			return null;
		}
		
		Formula formula = null;
		if (/*!manager.isEnd(loopData.getLocation().getLineNo())*/
				manager.isRelevant(loopData.getLocation().getLineNo())) {
			
			int times = 0;
			machine.resetData();
			addDataPoints(originVars, loopData.getMoreTimesValues(), Category.POSITIVE, machine);
			addDataPoints(originVars, loopData.getOneTimeValues(), Category.NEGATIVE, machine);
			//startTime = System.currentTimeMillis();
			machine.train();
			//System.out.println("learn model training time: " + (System.currentTimeMillis() - startTime) + " ms");
			formula = getLearnedFormula();
			double acc = machine.getModelAccuracy();
			while(formula != null && /*times < MAX_ATTEMPT &&*/ manager.isRelevant(loopData.getLocation().getLineNo())) {
				/*LoopTimesData newData = (LoopTimesData) selectiveSampling.selectData(loopData.getLocation(), 
						formula, machine.getDataLabels(), machine.getDataPoints());	*/
				Map<DecisionLocation, BreakpointData> newMap = selectiveSampling.selectDataForModel(loopData.getLocation(), 
						originVars, machine.getDataPoints(), preConditions, machine.getLearnedDividers());
				if (newMap == null) {
					break;
				}
				mergeMap(newMap);
				manager.updateRelevance(bkpDataMap);
				LoopTimesData newData = (LoopTimesData) newMap.get(loopData.getLocation());
				if (newData == null) {
					break;
				}
				//manager.updateRelevance(loopData);
				addDataPoints(originVars, newData.getMoreTimesValues(), Category.POSITIVE, machine);
				addDataPoints(originVars, newData.getOneTimeValues(), Category.NEGATIVE, machine);
				/*if (machine.getModelAccuracy() == 1.0) {
					break;
				}*/
				//startTime = System.currentTimeMillis();
				acc = machine.getModelAccuracy();
				machine.train();
				//System.out.println("learn model training time: " + (System.currentTimeMillis() - startTime) + " ms");
				Formula tmp = getLearnedFormula();
				double accTmp = machine.getModelAccuracy();
				if (tmp == null) {
					break;
				}
				if (!tmp.equals(formula) && accTmp > acc) {
					formula = tmp;
					acc = accTmp;
				} else {
					break;
				}
				times ++;
			}
			
		}	
		
		return formula;
	}
	
	/**
	 * collect all the runtime variable values, if the no runtime variable
	 * value is collected, the method return false, otherwise, return true.
	 * 
	 * @param bkpData
	 * @return
	 */
	private boolean collectAllVars(BreakpointData bkpData) {
		Set<ExecVar> allVars = new HashSet<ExecVar>();
		for (ExecValue bkpVal : bkpData.getFalseValues()) {
			collectExecVar(bkpVal.getChildren(), allVars);
		}
		for (ExecValue bkpVal : bkpData.getTrueValues()) {
			collectExecVar(bkpVal.getChildren(), allVars);
		}
		originVars = new ArrayList<ExecVar>(allVars);
		StoreSearcher.length = originVars.size();
		//calculateNums();
		if (originVars.isEmpty()) {
			return false;
		}
		
		mappingVars();
		
		labels = extractLabels(vars);
		machine.setDataLabels(labels);
		//oneClass.setDataLabels(labels);
		manager.setVars(vars, originVars);
		
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

	/*private void calculateNums() {
		int num = 1;
		for (ExecVar var : originVars) {
			switch (var.getType()) {
				case BOOLEAN:
					num *= 2;
					break;
				case INTEGER:
					num *= 400;
					break;
				case BYTE:
					num *= 200;
					break;
				case CHAR:
					num *= 200;
					break;
				case DOUBLE:
					num *= 4000;
					break;
				case FLOAT:
					num *= 2000;
					break;
				case LONG:
					num *= 2000;
					break;
				case SHORT:
					num *= 200;
					break;
				default:
					break;
			}
		}
		selectiveSampling.setNumLimit(num / 10);
	}*/
	
	/*private Set<ExecVar> extractBoolVars(List<ExecVar> allVars) {
		Set<ExecVar> result = new HashSet<ExecVar>();
		for (ExecVar var : allVars) {
			if (var.getType() == ExecVarType.BOOLEAN) {
				result.add(var);
			}
		}
		return result;
	}*/
	
	/**
	 * create new variables for polynomial classification
	 */
	private void mappingVars() {
		vars = new ArrayList<ExecVar>(originVars);
		int size = originVars.size();
		for (int i = 0; i < size; i++) {
			ExecVar var = originVars.get(i);
			for (int j = i; j < size; j++) {
				vars.add(new ExecVar(var.getLabel() + " * " + originVars.get(j).getLabel(), 
						ExecVarType.INTEGER));
			}
		}
	}
	
	private List<String> extractLabels(List<ExecVar> allVars) {
		List<String> labels = new ArrayList<String>(allVars.size());
		for (ExecVar var : allVars) {
			labels.add(var.getVarId());
		}
		return labels;
	}
	
	private void updateCoverage(BreakpointData data) {
		int lineNo = data.getLocation().getLineNo();
		if (needFalse) {
			List<BreakpointValue> falseValues = data.getFalseValues();
			for (BreakpointValue value : falseValues) {
				if (recordedTestInputs.contains(value)) {
					needFalse = false;
					curBranchNumber += 1;
					branchRecord.add(new Pair<Integer, Integer>(lineNo, BranchType.FALSE));
					break;
				}
			}
			if (needFalse && !falseValues.isEmpty()) {
				recordedTestInputs.add(falseValues.get(0));
				needFalse = false;
				curBranchNumber += 1;
				branchRecord.add(new Pair<Integer, Integer>(lineNo, BranchType.FALSE));
			}
		}
		if (needTrue) {
			List<BreakpointValue> trueValues = data.getTrueValues();
			for (BreakpointValue value : trueValues) {
				if (recordedTestInputs.contains(value)) {
					needTrue = false;
					curBranchNumber += 1;
					branchRecord.add(new Pair<Integer, Integer>(lineNo, BranchType.TRUE));
					break;
				}
			}
			if (needTrue && !trueValues.isEmpty()) {
				recordedTestInputs.add(trueValues.get(0));
				needTrue = false;
				curBranchNumber += 1;
				branchRecord.add(new Pair<Integer, Integer>(lineNo, BranchType.TRUE));
			}
		}
		
		printCoverageInfo();
	}
	
	private void printCoverageInfo() {
		for(Pair<Integer, Integer> pair: branchRecord){
			System.out.println("Branch: line " + pair.a + ", " + BranchType.getBranchType(pair.b));
		}
		
	}

	private void updateCoverage(LoopTimesData data) {
		int lineNo = data.getLocation().getLineNo();
		if (needOne) {
			List<BreakpointValue> oneTimeValues = data.getOneTimeValues();
			for (BreakpointValue value : oneTimeValues) {
				if (recordedTestInputs.contains(value)) {
					needOne = false;
					curBranchNumber += 1;
					branchRecord.add(new Pair<Integer, Integer>(lineNo, BranchType.TRUE));
					break;
				}
			}
			if (needOne && !oneTimeValues.isEmpty()) {
				recordedTestInputs.add(oneTimeValues.get(0));
				needOne = false;
				curBranchNumber += 1;
				branchRecord.add(new Pair<Integer, Integer>(lineNo, BranchType.TRUE));
			}
		}
		if (needMore) {
			List<BreakpointValue> moreTimesValues = data.getMoreTimesValues();
			for (BreakpointValue value : moreTimesValues) {
				if (recordedTestInputs.contains(value)) {
					needMore = false;
					curBranchNumber += 1;
					branchRecord.add(new Pair<Integer, Integer>(lineNo, BranchType.MORE));
					break;
				}
			}
			if (needMore && !moreTimesValues.isEmpty()) {
				recordedTestInputs.add(moreTimesValues.get(0));
				needMore = false;
				curBranchNumber += 1;
				branchRecord.add(new Pair<Integer, Integer>(lineNo, BranchType.MORE));
			}
		}
	}
	
	private void addDataPoints(List<ExecVar> vars, List<BreakpointValue> values, Category category, Machine machine) {
		for (BreakpointValue value : values) {
			addDataPoint(vars, value, category, machine);
		}
	}
	
	private void addDataPoint(List<ExecVar> vars, BreakpointValue bValue, Category category, Machine machine) {
		double[] lineVals = new double[labels.size()];
		int i = 0;
		for (ExecVar var : vars) {
			final Double value = bValue.getValue(var.getLabel(), 0.0);
			lineVals[i++] = value;
		}
		int size = vars.size();
		for (int j = 0; j < size; j++) {
			double value = bValue.getValue(vars.get(j).getLabel(), 0.0);
			for (int k = j; k < size; k++) {
				//lineVals[i ++] = value * bValue.getValue(vars.get(k).getLabel(), 0.0);
				lineVals[i ++] = 0.0;
			}
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

	public List<String> getLabels() {
		return labels;
	}

	public List<ExecVar> getOriginVars() {
		return originVars;
	}
	
	private void mergeMap(Map<DecisionLocation, BreakpointData> newMap) {
		//startTime = System.currentTimeMillis();
		if (newMap == null) {
			return;
		}
		Set<DecisionLocation> locations = bkpDataMap.keySet();
		for (DecisionLocation location : locations) {
			bkpDataMap.get(location).merge(newMap.get(location));
		}
		//System.out.println("learn merge map time: " + (System.currentTimeMillis() - startTime) + " ms");
		return;
	}

	public List<BreakpointValue> getRecords() {
		return recordedTestInputs;
	}

	public double getCoverage() {
		return curBranchNumber / manager.getTotalBranch();
	}

}
