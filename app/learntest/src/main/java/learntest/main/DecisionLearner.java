package learntest.main;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
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
import learntest.cfg.traveller.Branch;
import learntest.cfg.traveller.CfgConditionManager;
import learntest.cfg.traveller.DecisionBranch;
import learntest.cfg.traveller.LoopBranch;
import learntest.sampling.JavailpSelectiveSampling;
import learntest.sampling.jacop.StoreSearcher;
import learntest.testcase.data.BranchType;
import learntest.testcase.data.BreakpointData;
import learntest.testcase.data.LoopTimesData;
import libsvm.core.Category;
import libsvm.core.CategoryCalculator;
import libsvm.core.Divider;
import libsvm.core.Machine;
import libsvm.core.Machine.DataPoint;
import libsvm.extension.ByDistanceNegativePointSelection;
import libsvm.extension.NegativePointSelection;
import libsvm.extension.PositiveSeparationMachine;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.formula.Formula;
import sav.common.core.utils.CollectionUtils;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;

public class DecisionLearner implements CategoryCalculator {
	
	protected static Logger log = LoggerFactory.getLogger(DecisionLearner.class);
//	private MyPositiveSeparationMachine machine;
	// one class does not perform well
	//private Machine oneClass;
	private List<ExecVar> originVars;
	private List<ExecVar> vars;
	private List<String> labels;
	//private Set<ExecVar> boolVars;
	//private JacopSelectiveSampling selectiveSampling;
	private JavailpSelectiveSampling selectiveSampling;
	
	private CfgConditionManager cfgConditionManager;
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
	
	private List<Branch> coveredBranches = new ArrayList<>();
	
	//private double curBranchNumber = 1;
	/**
	 * The first integer indicates the line number of condition, the second integer indicates branch: 0-false, 1-true/once, 2-more
	 * See {@link BranchType}
	 */
	private List<Pair<Integer, Integer>> branchRecord;
	
	private boolean needFalse = true;
	private boolean needTrue = true;
	private boolean needOne = true;
	private boolean needMore = true;
	
	public DecisionLearner(/*JacopSelectiveSampling*/JavailpSelectiveSampling selectiveSampling, 
			CfgConditionManager manager, boolean random) {
//		machine = new MyPositiveSeparationMachine();
//		machine.setDefaultParams();
		/*oneClass = new Machine();
		oneClass.setParameter(new Parameter().setMachineType(MachineType.ONE_CLASS)
				.setKernelType(KernelType.LINEAR).setEps(0.00001).setUseShrinking(false)
				.setPredictProbability(false).setC(Double.MAX_VALUE));*/
		this.selectiveSampling = selectiveSampling;
		this.cfgConditionManager = manager;
		this.random = random;
	}
	
	public void learn(Map<DecisionLocation, BreakpointData> bkpDataMap) throws SavException, SAVExecutionTimeOutException {
		cfgConditionManager.updateRelevance(bkpDataMap);
		recordedTestInputs = new ArrayList<BreakpointValue>();
		branchRecord = new ArrayList<Pair<Integer,Integer>>();
		this.bkpDataMap = bkpDataMap;
		List<BreakpointData> bkpDatas = new ArrayList<BreakpointData>(bkpDataMap.values());
		Collections.sort(bkpDatas);
		
		System.out.print("total branches:" + cfgConditionManager.getTotalBranches());
		
		/**
		 * each decision location has two formula, one for true/false and one for loop
		 */
		Map<DecisionLocation, Pair<Formula, Formula>> decisions = new HashMap<DecisionLocation, Pair<Formula, Formula>>();
		for (BreakpointData bkpData : bkpDatas) {
			System.out.println("========Start to learn at " + bkpData.getLocation());
			if (bkpData.getFalseValues().isEmpty() && bkpData.getTrueValues().isEmpty()) {
				System.out.println("Cannot find any data at " + bkpData.getLocation());
				continue;
			}
			
			if (vars == null && !collectAllVars(bkpData)) {
				System.out.println("No variable collected at " + bkpData.getLocation());
				continue;
			}
			
			System.out.println("true data: " + bkpData.getTrueValues());
			System.out.println("false data: " + bkpData.getFalseValues());
			Pair<Formula, Formula> learnedClassifier = learn(bkpData);
			
			System.out.println("true or false classifier at " + bkpData.getLocation() + " is :" + learnedClassifier.first());
			cfgConditionManager.setPrecondition(bkpData.getLocation().getLineNo(), learnedClassifier, curDividers);
			
			updateCoverage(bkpData);
			System.out.println("coveredBranches: " + this.coveredBranches);
			
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
			
			File treeDir = new File("trees");
			if(!treeDir.exists()){
				treeDir.mkdirs();
			}
			
			File file = new File("trees/" + LearnTestConfig.getSimpleClassName() + "." 
					+ LearnTestConfig.testMethodName);
			FileWriter writer = new FileWriter(file);
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
			preconditions = cfgConditionManager.getPreConditions(bkpData.getLocation().getLineNo());
			preconditions.clearInvalidData(bkpData);
		}
		
		if (bkpData.getTrueValues().isEmpty() || bkpData.getFalseValues().isEmpty()) {			
			long startTime = System.currentTimeMillis();
			System.out.println("start selecting data for empty branch");
			Map<DecisionLocation, BreakpointData> selectMap = 
					selectiveSampling.selectDataForEmpty(bkpData.getLocation(), originVars,
							preconditions, null, bkpData.getTrueValues().isEmpty(), false);
			System.out.println("learn select data for empty time for " + bkpData.getLocation()
					+ ": " + (System.currentTimeMillis() - startTime) + "ms");
			
			System.currentTimeMillis();
			
			if (selectMap != null) {
				System.out.println("true data after selective for empty: " + selectMap.get(bkpData.getLocation()).getTrueValues());
				System.out.println("false data after selective for empty: " + selectMap.get(bkpData.getLocation()).getFalseValues());
				
				mergeMap(selectMap);
				cfgConditionManager.updateRelevance(bkpDataMap);
				if (bkpData.getTrueValues().isEmpty() || bkpData.getFalseValues().isEmpty()) {
					selectMap = selectiveSampling.selectDataForEmpty(bkpData.getLocation(), originVars, 
							preconditions, null, bkpData.getTrueValues().isEmpty(), false);
					if (selectMap != null) {
						mergeMap(selectMap);
					} else {
						mergeMap(selectiveSampling.getSelectResult());
					}
					cfgConditionManager.updateRelevance(bkpDataMap);
				}
			} else {
				mergeMap(selectiveSampling.getSelectResult());
				cfgConditionManager.updateRelevance(bkpDataMap);
			}
		}
		
		if (bkpData.getTrueValues().isEmpty()) {
			System.out.println("Missing true branch data");
			curDividers = null;
			return new Pair<Formula, Formula>(null, null);
		} else if (bkpData.getFalseValues().isEmpty()) {
			System.out.println("Missing false branch data");
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
		else{
			Formula trueFlaseFormula = generateTrueFalseFormula(bkpData, preconditions);
			
			Formula oneMoreFormula = null;
			if (bkpData instanceof LoopTimesData) {
				oneMoreFormula = generateLoopFormula((LoopTimesData)bkpData);
			} 
			
			return new Pair<Formula, Formula>(trueFlaseFormula, oneMoreFormula);			
		}
		
	}

	private int maxAttempt = 10;
	
	private Formula generateTrueFalseFormula(BreakpointData bkpData, OrCategoryCalculator preconditions)
			throws SAVExecutionTimeOutException, SavException {
		Formula trueFlaseFormula = null;
		if (cfgConditionManager.isRelevant(bkpData.getLocation().getLineNo())) {
			
			NegativePointSelection negative = new ByDistanceNegativePointSelection();
			PositiveSeparationMachine mcm = new PositiveSeparationMachine(negative);
			trueFlaseFormula = generateInitialFormula(bkpData, mcm);
			System.currentTimeMillis();
			double acc = mcm.getModelAccuracy();
			curDividers = mcm.getLearnedDividers();
			System.out.println("=============learned multiple cut: " + trueFlaseFormula);
			
			int time =0;
			while(trueFlaseFormula != null && time < maxAttempt && cfgConditionManager.isRelevant(bkpData.getLocation().getLineNo())) {
				long startTime = System.currentTimeMillis();				
				Map<DecisionLocation, BreakpointData> newMap = selectiveSampling.selectDataForModel(bkpData.getLocation(), 
						originVars, mcm.getDataPoints(), preconditions, mcm.getLearnedDividers());
				System.out.println("learn select data for model: " + (System.currentTimeMillis() - startTime) + "ms");
				System.currentTimeMillis();
				if (newMap == null) {
					break;
				}
				mergeMap(newMap);
				cfgConditionManager.updateRelevance(bkpDataMap);
				
				BreakpointData newData = newMap.get(bkpData.getLocation());
				if (newData == null) {
					break;
				}
				preconditions.clearInvalidData(newData);
				
				mcm.getLearnedModels().clear();
				addDataPoints(originVars, newData.getTrueValues(), Category.POSITIVE, mcm);
				addDataPoints(originVars, newData.getFalseValues(), Category.NEGATIVE, mcm);
				System.out.println("true data after selective sampling" + bkpData.getTrueValues());
				System.out.println("false data after selective sampling" + bkpData.getFalseValues());
				
				mcm.train();
				Formula tmp = mcm.getLearnedMultiFormula(originVars, getLabels());
				if (tmp == null) {
					break;
				}
				
				double accTmp = mcm.getModelAccuracy();
				acc = mcm.getModelAccuracy();
				if (!tmp.equals(trueFlaseFormula)) {
					trueFlaseFormula = tmp;
					curDividers = mcm.getLearnedDividers();
					acc = accTmp;
					
					if(acc == 1.0){
						break;
					}
				} else {
					break;
				}
				
				time++;
			}
		}
		
		return trueFlaseFormula;
	}

	private Formula generateInitialFormula(BreakpointData bkpData, PositiveSeparationMachine mcm)
			throws SAVExecutionTimeOutException {
		mcm.setDefaultParams();
		mcm.setDataLabels(labels);
		mcm.setDefaultParams();
		for(BreakpointValue value: bkpData.getTrueValues()){
			addDataPoint(originVars, value, Category.POSITIVE, mcm);
		}
		for(BreakpointValue value: bkpData.getFalseValues()){
			addDataPoint(originVars, value, Category.NEGATIVE, mcm);
		}
		mcm.train();
		Formula newFormula = mcm.getLearnedMultiFormula(originVars, getLabels());
		
		return newFormula;
	}
	
	private Formula generateLoopFormula(LoopTimesData loopData) throws SavException, SAVExecutionTimeOutException {
		needOne = true;
		needMore = true;
		
		OrCategoryCalculator preConditions = null;
		if (!random) {
			preConditions = cfgConditionManager.getPreConditions(loopData.getLocation().getLineNo());			
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
			cfgConditionManager.updateRelevance(bkpDataMap);
		}
		
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
		else{
			Formula loopFormula = generateConcreteLoopFormula(loopData, preConditions);
			return loopFormula;
		}
		
	}

	private Formula generateConcreteLoopFormula(LoopTimesData loopData, OrCategoryCalculator preConditions)
			throws SAVExecutionTimeOutException, SavException {
		Formula formula = null;
		if (cfgConditionManager.isRelevant(loopData.getLocation().getLineNo())) {
			
			NegativePointSelection negative = new ByDistanceNegativePointSelection();
			PositiveSeparationMachine mcm = new PositiveSeparationMachine(negative);
			formula = generateInitialFormula(loopData, mcm);
			
			int times = 0;
			double acc = mcm.getModelAccuracy();
			while(formula != null && times < maxAttempt && cfgConditionManager.isRelevant(loopData.getLocation().getLineNo())) {
				Map<DecisionLocation, BreakpointData> newMap = selectiveSampling.selectDataForModel(loopData.getLocation(), 
						originVars, mcm.getDataPoints(), preConditions, mcm.getLearnedDividers());
				if (newMap == null) {
					break;
				}
				mergeMap(newMap);
				cfgConditionManager.updateRelevance(bkpDataMap);
				LoopTimesData newData = (LoopTimesData) newMap.get(loopData.getLocation());
				if (newData == null) {
					break;
				}
				addDataPoints(originVars, newData.getMoreTimesValues(), Category.POSITIVE, mcm);
				addDataPoints(originVars, newData.getOneTimeValues(), Category.NEGATIVE, mcm);
				acc = mcm.getModelAccuracy();
				if (acc == 1.0) {
					break;
				}
				mcm.train();
				Formula tmp = mcm.getLearnedMultiFormula(originVars, getLabels());
				double accTmp = mcm.getModelAccuracy();
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
//		machine.setDataLabels(labels);
		//oneClass.setDataLabels(labels);
		cfgConditionManager.setVars(vars, originVars);
		
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
					
					DecisionBranch branch = new DecisionBranch(lineNo, false);
					if(!this.coveredBranches.contains(branch)){
						this.coveredBranches.add(branch);
					}
					
					branchRecord.add(new Pair<Integer, Integer>(lineNo, BranchType.FALSE));
					break;
				}
			}
			if (needFalse && !falseValues.isEmpty()) {
				recordedTestInputs.add(falseValues.get(0));
				needFalse = false;
				
				DecisionBranch branch = new DecisionBranch(lineNo, false);
				if(!this.coveredBranches.contains(branch)){
					this.coveredBranches.add(branch);
				}
				
				branchRecord.add(new Pair<Integer, Integer>(lineNo, BranchType.FALSE));
			}
		}
		if (needTrue) {
			List<BreakpointValue> trueValues = data.getTrueValues();
			for (BreakpointValue value : trueValues) {
				if (recordedTestInputs.contains(value)) {
					needTrue = false;

					DecisionBranch branch = new DecisionBranch(lineNo, true);
					if(!this.coveredBranches.contains(branch)){
						this.coveredBranches.add(branch);
					}
					
					branchRecord.add(new Pair<Integer, Integer>(lineNo, BranchType.TRUE));
					break;
				}
			}
			if (needTrue && !trueValues.isEmpty()) {
				recordedTestInputs.add(trueValues.get(0));
				needTrue = false;

				DecisionBranch branch = new DecisionBranch(lineNo, true);
				if(!this.coveredBranches.contains(branch)){
					this.coveredBranches.add(branch);
				}
				
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
					
					LoopBranch branch = new LoopBranch(lineNo, true);
					if(!this.coveredBranches.contains(branch)){
						this.coveredBranches.add(branch);
					}
					
					branchRecord.add(new Pair<Integer, Integer>(lineNo, BranchType.TRUE));
					break;
				}
			}
			if (needOne && !oneTimeValues.isEmpty()) {
				recordedTestInputs.add(oneTimeValues.get(0));
				needOne = false;

				LoopBranch branch = new LoopBranch(lineNo, true);
				if(!this.coveredBranches.contains(branch)){
					this.coveredBranches.add(branch);
				}
				
				branchRecord.add(new Pair<Integer, Integer>(lineNo, BranchType.TRUE));
			}
		}
		if (needMore) {
			List<BreakpointValue> moreTimesValues = data.getMoreTimesValues();
			for (BreakpointValue value : moreTimesValues) {
				if (recordedTestInputs.contains(value)) {
					needMore = false;

					LoopBranch branch = new LoopBranch(lineNo, false);
					if(!this.coveredBranches.contains(branch)){
						this.coveredBranches.add(branch);
					}

					branchRecord.add(new Pair<Integer, Integer>(lineNo, BranchType.MORE));
					break;
				}
			}
			if (needMore && !moreTimesValues.isEmpty()) {
				recordedTestInputs.add(moreTimesValues.get(0));
				needMore = false;

				LoopBranch branch = new LoopBranch(lineNo, false);
				if(!this.coveredBranches.contains(branch)){
					this.coveredBranches.add(branch);
				}
				
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
//			double value = bValue.getValue(vars.get(j).getLabel(), 0.0);
			for (int k = j; k < size; k++) {
//				lineVals[i ++] = value * bValue.getValue(vars.get(k).getLabel(), 0.0);
				lineVals[i ++] = 0.0;
			}
		}

		machine.addDataPoint(category, lineVals);
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
		Collection<BreakpointData> values = bkpDataMap.values();
		for (BreakpointData value : values) {
			needFalse = true;
			if (value instanceof LoopTimesData) {
				needTrue = false;
				needOne = true;
				needMore = true;
				updateCoverage(value);
				updateCoverage((LoopTimesData) value);
			} else {
				needTrue = true;
				updateCoverage(value);
			}
		}
		
		return this.coveredBranches.size() / ((double)cfgConditionManager.getTotalBranches().size());
	}

}
