package learntest.sampling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import icsetlv.common.dto.BreakpointValue;
import learntest.breakpoint.data.DecisionLocation;
import learntest.calculator.OrCategoryCalculator;
import learntest.core.machinelearning.SampleExecutor;
import learntest.sampling.javailp.ProblemBuilder;
import learntest.sampling.javailp.ProblemSolver;
import learntest.testcase.TestcasesExecutorwithLoopTimes;
import learntest.testcase.data.BreakpointData;
import learntest.testcase.data.LoopTimesData;
import learntest.util.Settings;
import libsvm.core.Divider;
import libsvm.core.Machine.DataPoint;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import net.sf.javailp.ResultImpl;
import sav.common.core.SavException;
import sav.common.core.formula.Eq;
import sav.common.core.utils.Randomness;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;

public class JavailpSelectiveSampling {
	private static Logger log = LoggerFactory.getLogger(JavailpSelectiveSampling.class);
	private static final int MAX_MORE_SELECTED_SAMPLE = 4;
	private static final int MIN_MORE_SELECTED_DATA = 5;

	private TestcasesExecutorwithLoopTimes tcExecutor;
//	private Map<String, Pair<Double, Double>> minMax;
	private List<Result> prevDatas;
	private Map<DecisionLocation, BreakpointData> selectResult;
	
	private int numPerExe = 100;
	private int timesLimit = 20;
	
	private int delta;
	
	public JavailpSelectiveSampling(TestcasesExecutorwithLoopTimes tcExecutor) {
		this.tcExecutor = tcExecutor;
		prevDatas = new ArrayList<Result>();
	}
	
	public void addPrevValues(List<BreakpointValue> values) {
		if (values == null) {
			return;
		}
		for (BreakpointValue value : values) {
			Result result = new ResultImpl();
			Set<String> labels = value.getAllLabels();
			for (String label : labels) {
				result.put(label, value.getValue(label, 0.0));
			}
			prevDatas.add(result);
		}
		delta = values.size() - 1;
	}

	public Map<DecisionLocation, BreakpointData> selectDataForEmpty(DecisionLocation target, 
			List<ExecVar> originVars, 
			OrCategoryCalculator precondition, 
			List<Divider> current, 
			boolean trueOrFalse, 
			boolean isLoop) throws SavException, SAVExecutionTimeOutException {
		System.currentTimeMillis();
		
		tcExecutor.setTarget(null);
		for (int i = 0; i < 3; i++) {
			List<List<Eq<?>>> assignments = new ArrayList<List<Eq<?>>>();
			List<Problem> problems = ProblemBuilder.buildTrueValueProblems(originVars, precondition, current, true);
			if (problems.isEmpty()) {
				return null;
			}
			int num = numPerExe / problems.size() + 1;
			for (Problem problem : problems) {
				List<Result> results = ProblemSolver.calculateRanges(problem, originVars);
				for (Result result : results) {
					checkNonduplicateResult(result, originVars, prevDatas, assignments);
				}
				problem.setObjective(problem.getObjective(), OptType.MAX);
				results = ProblemSolver.solveMultipleTimes(problem, num);
				for (Result result : results) {
					checkNonduplicateResult(result, originVars, prevDatas, assignments);
				}
				
				problem.setObjective(problem.getObjective(), OptType.MIN);
				results = ProblemSolver.solveMultipleTimes(problem, num);
				for (Result result : results) {
					checkNonduplicateResult(result, originVars, prevDatas, assignments);
				}
			}
			
//			log.debug("Iteartion " + (i+1) + " running " + assignments.size() + " test cases");
//			long t = System.currentTimeMillis();
			runData(assignments);
//			log.debug("Done: running " + assignments.size() + " test cases, takes " + (System.currentTimeMillis()-t) + "ms");
			
			if (selectResult == null) {
				continue;
			}
			BreakpointData selectData = selectResult.get(target);
			if (!isLoop) {
				if (trueOrFalse && !selectData.getTrueValues().isEmpty()) {
					return selectResult;
				}
				if (!trueOrFalse && !selectData.getFalseValues().isEmpty()) {
					return selectResult;
				}
			} else {
				LoopTimesData loopTimesData = (LoopTimesData) selectData;
				if (trueOrFalse && !loopTimesData.getMoreTimesValues().isEmpty()) {
					return selectResult;
				}
				if (!trueOrFalse && !loopTimesData.getOneTimeValues().isEmpty()) {
					return selectResult;
				}
			}
		}
		return null;
	}
	
	public Map<DecisionLocation, BreakpointData> selectDataForModel(DecisionLocation target, 
			List<ExecVar> originVars, 
			List<DataPoint> datapoints,
			OrCategoryCalculator preconditions, 
			List<Divider> learnedFormulas) throws SavException, SAVExecutionTimeOutException {
		List<List<Eq<?>>> assignments = new ArrayList<List<Eq<?>>>();
		List<Result> results = new ArrayList<Result>();
		
		/**
		 * generate data point on the border of divider 
		 */
		for (Divider learnedFormula : learnedFormulas) {
			
			List<Problem> problems = ProblemBuilder.buildProblemWithPreconditions(originVars, preconditions, false);
			if (!problems.isEmpty() && learnedFormula != null) {
				for (Problem problem : problems) {
					ProblemBuilder.addOnBorderConstaints(problem, learnedFormula, originVars);
				}
			}
			
			for (Problem problem : problems) {
				ProblemSolver.generateRandomObjective(problem, originVars);
				Result result = ProblemSolver.solve(problem);
				if (result != null) {
					boolean isDuplicateWithResult = isDuplicate(result, originVars, prevDatas);
					
					if (!isDuplicateWithResult) {
						prevDatas.add(result);
					}
					
					List<Eq<?>> assignment = getAssignments(result, originVars);
					assignments.add(assignment);
					results.add(result);
				}
			}
		}
		
		/**
		 * randomly generate more data points on svm model. 
		 */
		assignments = generateRandomPointsWithPrecondition(preconditions, originVars, datapoints, assignments, results, 3);
		
		for(int i=0; i<Settings.selectiveNumber; i++){
			extendWithHeuristics(results, assignments, originVars);			
		}
		
		
		if(assignments.size() > 100){
			int size = assignments.size();
			for(int i=100; i<size; i++){
				assignments.remove(100);
			}			
		}
		
		selectData(target, assignments);
		System.currentTimeMillis();
		return selectResult;
	}

	private List<List<Eq<?>>> generateRandomPointsWithPrecondition(OrCategoryCalculator preconditions, List<ExecVar> originVars, List<DataPoint> datapoints,
			List<List<Eq<?>>> assignments, List<Result> results, int toBeGeneratedDataNum) {
		
		int trialNumThreshold = 100;
		List<Result> dataPoints = new ArrayList<>();
		
		if (originVars.size() > 1) {
			
			for(int i = 0; dataPoints.size()<toBeGeneratedDataNum && i<trialNumThreshold; i++){
				List<Problem> pList = ProblemBuilder.buildProblemWithPreconditions(originVars, preconditions, true);
				
				if(pList.isEmpty()){
					break;
				}
				
//				Problem p = ProblemBuilder.buildVarBoundContraint(originVars);
				Problem p = pList.get(0);
				ProblemSolver.generateRandomObjective(p, originVars);
				
				for(int reducedVarNum=0; reducedVarNum<=originVars.size(); reducedVarNum++){
					List<Eq<Number>> samples = generateRandomVariableAssignment(originVars, reducedVarNum);
					int bound=5;
					int k=0;
					while(samples.isEmpty() && k<bound){
						samples = generateRandomVariableAssignment(originVars, reducedVarNum);
					}
					
					if(!samples.isEmpty()){
						ProblemBuilder.addEqualConstraints(p, samples);
						Result res = ProblemSolver.solve(p);
						
						if (res != null && checkNonduplicateResult(res, originVars, prevDatas, assignments)) {
							results.add(res);
							dataPoints.add(res);
						}
						
						break;
					}
				}
			}
		}
		
		return assignments;
	}

	/**
	 * slight moving existing data points.
	 * @param results
	 * @param assignments
	 * @param originVars
	 */
	private void extendWithHeuristics(List<Result> results, List<List<Eq<?>>> assignments, 
			List<ExecVar> originVars) {
		
		double selectiveBound = 5;
		Random random = new Random();
		double offset = random.nextDouble()*selectiveBound;
		
		for (Result result : results) {
			Result rightPoint = new ResultImpl();
			Result leftPoint = new ResultImpl();
			
			for (ExecVar var : originVars) {
				String label = var.getLabel();
				Number value = result.get(label);
				
				switch (var.getType()) {
					case INTEGER:
						rightPoint.put(label, value.intValue() + (int)offset);
						leftPoint.put(label, value.intValue() - (int)offset);
						break;
					case CHAR:
						rightPoint.put(label, value.intValue() + 1);
						leftPoint.put(label, value.intValue() - 1);
						break;
					case BYTE:
						rightPoint.put(label, value.byteValue() + 1);
						leftPoint.put(label, value.byteValue() - 1);
						break;
					case DOUBLE:
						rightPoint.put(label, value.doubleValue() + offset);
						leftPoint.put(label, value.doubleValue() - offset);
						break;
					case FLOAT:
						rightPoint.put(label, value.floatValue() + (float)offset);
						leftPoint.put(label, value.floatValue() - (float)offset);
						break;
					case LONG:
						rightPoint.put(label, value.longValue() + (long)offset);
						leftPoint.put(label, value.longValue() - (long)offset);
						break;
					case SHORT:
						rightPoint.put(label, value.shortValue() + (short)offset);
						leftPoint.put(label, value.shortValue() - (short)offset);
						break;
					case BOOLEAN:
						rightPoint.put(label, 1 - value.intValue());
						leftPoint.put(label, 1 - value.intValue());
						break;
					default:
						break;
				}
			}
			
			checkNonduplicateResult(rightPoint, originVars, prevDatas, assignments);
			checkNonduplicateResult(leftPoint, originVars, prevDatas, assignments);
		}
	}
	
	//special checker for triangle
	private boolean isValid(Result result) {
		int x = result.get("x").intValue();
		int y = result.get("y").intValue();
		int z = result.get("z").intValue();
		return 20 >= x && x >= y && y >= z && z >= 1;
	}

	public Map<DecisionLocation, BreakpointData> getSelectResult() {
		return selectResult;
	}

	private void runData(List<List<Eq<?>>> assignments) throws SavException, SAVExecutionTimeOutException {
		if (assignments.isEmpty()) {
			return;
		}
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		for (List<Eq<?>> valSet : assignments) {
			if (!valSet.isEmpty()) {
				Map<String, Object> varMap = toInstrVarMap(valSet);
				list.add(varMap);
			}
		}
		
		tcExecutor.duplicateTestCases(list.size());
		tcExecutor.setVarMaps(list);
		
		if(list != null){
			int size = list.size();
			log.debug("Running " + size + " data points...");			
		}
		
		tcExecutor.run(); 
		selectResult = tcExecutor.getResult();
	}
	
	private void selectData(DecisionLocation target, 
			List<List<Eq<?>>> assignments) throws SavException, SAVExecutionTimeOutException {
		if (assignments.isEmpty()) {
			selectResult = null;
			return;
		}
		tcExecutor.setTarget(null);
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		for (List<Eq<?>> valSet : assignments) {
			if (!valSet.isEmpty()) {
				list.add(toInstrVarMap(valSet));
			}
		}
		tcExecutor.duplicateTestCases(list.size());
		tcExecutor.setVarMaps(list);
		
		if(list != null){
			int size = list.size();
			log.debug("Running " + size + " data points...");			
		}
		
		tcExecutor.run();
		selectResult = tcExecutor.getResult();
	}
	
	private Map<String, Object> toInstrVarMap(List<Eq<?>> assignments) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Eq<?> asgt : assignments) {
			map.put(asgt.getVar().getLabel(), asgt.getValue());
		}
		return map;
	}

	private boolean checkNonduplicateResult(Result result, List<ExecVar> vars, List<Result> prevDatas, List<List<Eq<?>>> assignments) {
		boolean isDuplicateWithResult = isDuplicate(result, vars, prevDatas);
		
		if (!isDuplicateWithResult) {
			prevDatas.add(result);
			List<Eq<?>> assignment = getAssignments(result, vars);
			assignments.add(assignment);
		}
		
		return !isDuplicateWithResult;
	}
	
	private boolean isDuplicate(Result result, List<ExecVar> vars, List<Result> prevDatas){
		for (Result r : prevDatas) {
			if (duplicate(result, r, vars)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * randomly reduce some vars to generate equation constraints.
	 */
	private List<Eq<Number>> generateRandomVariableAssignment(List<ExecVar> vars, int reducedVarNum) {
		List<Eq<Number>> atoms = new ArrayList<Eq<Number>>();
		
		if(vars.size()<=reducedVarNum){
			return atoms;
		}
		
		int remainedVars = vars.size();
		
		Random random = new Random();
		for (ExecVar var : vars) {
			if((remainedVars-reducedVarNum) == 0){
				continue;
			}
			
			if(reducedVarNum > 0){
				if(random.nextDouble()>=0.5){
					reducedVarNum--;
					remainedVars--;
					continue;
				}
			}
			
			Number value = Randomness.nextInt(-Settings.bound, Settings.bound);
			if (var.getType() == ExecVarType.BOOLEAN) {
				if (value.intValue() > 0) {
					atoms.add(new Eq<Number>(var, 1));
				} else {
					atoms.add(new Eq<Number>(var, 0));
				}
			} else {
				atoms.add(new Eq<Number>(var, value));
			}
			
			remainedVars--;
		}
		
		return atoms;
	}

	private boolean duplicate(Result r1, Result r2, List<ExecVar> vars) {
		if (r1 == null && r2 == null) {
			return true;
		}
		if (r1 == null || r2 == null) {
			return false;
		}
		for (ExecVar var : vars) {
			String label = var.getLabel();
			if (r1.containsVar(label) ^ r2.containsVar(label)) {
				return false;
			}
			//TODO compare value according to variable type
			if (r1.containsVar(label) && !r1.get(label).equals( r2.get(label))) {
				return false;
			}
		}
		return true;
	}
	
	private List<Eq<?>> getAssignments(Result result, List<ExecVar> vars) {
		List<Eq<?>> assignments = new ArrayList<Eq<?>>();
		for (ExecVar var : vars) {
			if (result.containsVar(var.getLabel())) {
				Number number = result.get(var.getLabel());
				switch (var.getType()) {
					case INTEGER:
					case BYTE:
					case CHAR:
					case DOUBLE:
					case FLOAT:
					case LONG:
					case SHORT:
						assignments.add(new Eq<Number>(var, number));
						break;
					case BOOLEAN:
						if (number.intValue() > 0) {
							assignments.add(new Eq<Boolean>(var, true));
						} else {
							assignments.add(new Eq<Boolean>(var, false));
						}
						break;
					default:
						break;
				}
			}
		}
		return assignments;
	}
	
	public int getTotalNum() {
		return prevDatas.size() - delta;
	}

}
