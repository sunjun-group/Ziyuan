package learntest.sampling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import icsetlv.common.dto.BreakpointValue;
import learntest.breakpoint.data.DecisionLocation;
import learntest.calculator.OrCategoryCalculator;
import learntest.sampling.javailp.ProblemBuilder;
import learntest.sampling.javailp.ProblemSolver;
import learntest.testcase.TestcasesExecutorwithLoopTimes;
import learntest.testcase.data.BreakpointData;
import learntest.testcase.data.LoopTimesData;
import libsvm.core.Divider;
import libsvm.core.Machine.DataPoint;
import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import net.sf.javailp.ResultImpl;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.formula.Eq;
import sav.common.core.utils.Randomness;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;

public class JavailpSelectiveSampling {
	
	private static final int MAX_MORE_SELECTED_SAMPLE = 4;
	private static final int MIN_MORE_SELECTED_DATA = 5;

	private TestcasesExecutorwithLoopTimes tcExecutor;
	private Map<String, Pair<Double, Double>> minMax;
	private List<Result> prevDatas;
	private Map<DecisionLocation, BreakpointData> selectResult;
	
	private int numPerExe = 100;
	private int timesLimit = 20;
	
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
	}

	public Map<DecisionLocation, BreakpointData> selectDataForEmpty(DecisionLocation target, 
			List<ExecVar> originVars, 
			OrCategoryCalculator precondition, 
			List<Divider> current, 
			boolean trueOrFalse, 
			boolean isLoop) throws SavException, SAVExecutionTimeOutException {
		tcExecutor.setTarget(null);
		for (int i = 0; i < timesLimit; i++) {
			List<List<Eq<?>>> assignments = new ArrayList<List<Eq<?>>>();
			while (assignments.size() < numPerExe) {
				/**
				 * TODO, Lin Yun, this random strategy is wierd, it's better to figure out another way.
				 */
				List<Problem> problems = ProblemBuilder.build(originVars, precondition, current, true);
				ProblemBuilder.addRandomConstraint(problems, originVars);
				//List<Result> results = ProblemSolver.solve(problems, originVars, numPerExe);
				for (Problem problem : problems) {
					List<Result> results = ProblemSolver.solveMultipleTimes(problem, originVars);
					for (Result result : results) {
						checkNonduplicateResult(result, originVars, prevDatas, assignments);
					}				
				}
			}			
			runData(assignments);
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
			OrCategoryCalculator precondition, 
			List<Divider> dividers) throws SavException, SAVExecutionTimeOutException {
		List<List<Eq<?>>> assignments = new ArrayList<List<Eq<?>>>();
		List<Result> results = new ArrayList<Result>();
		
		List<Problem> basics = ProblemBuilder.build(null, originVars, precondition, dividers, false);
		/**
		 * generate a basic data point according to precondition
		 */
		for (Problem basic : basics) {
			Result res = ProblemSolver.generateRandomResultToConstraints(basic, originVars);
			if (res != null && checkNonduplicateResult(res, originVars, prevDatas, assignments)) {
				results.add(res);
			}
		}
		
		/**
		 * generate data point on the svm model
		 */
		for (Divider divider : dividers) {
			List<Problem> problems = ProblemBuilder.build(divider, originVars, precondition, dividers, false);
			for (Problem problem : problems) {
				Result res = ProblemSolver.generateRandomResultToConstraints(problem, originVars);
				if (res != null && checkNonduplicateResult(res, originVars, prevDatas,assignments)) {
					results.add(res);
				}
			}
		}
		
		/**
		 * generate data point on the opposite side of svm model
		 */
		int size = dividers.size();
		for (int i = 0; i < size; i++) {
			List<Divider> list = new ArrayList<Divider>(dividers);
			Divider divider = list.remove(i);
			List<Problem> problems = ProblemBuilder.build(null, originVars, precondition, list, false);
			for (Problem problem : problems) {
				ProblemBuilder.addOpposite(problem, divider, originVars);
				Result res = ProblemSolver.generateRandomResultToConstraints(problem, originVars);
				if (res != null && checkNonduplicateResult(res, originVars, prevDatas,assignments)) {
					results.add(res);
				}
			}
		}
		
		/**
		 * fix some variables to a random number, then solve other variables.
		 */
		Random random = new Random();
		calculateValRange(originVars, datapoints);
		if (originVars.size() > 1) {
			for (Divider divider : dividers) {
				List<Problem> probelms = ProblemBuilder.build(divider, originVars, precondition, dividers, false);
				for (Problem problem : probelms) {
					for (int i = 0; i < MAX_MORE_SELECTED_SAMPLE; i++) {
						int idx = random.nextInt(originVars.size());
						List<Eq<Number>> samples = selectSample(originVars, results.isEmpty() ? null 
								: results.get(random.nextInt(results.size())), idx);
						if (samples.isEmpty()) {
							continue;
						}
						ProblemBuilder.addConstraints(problem, samples);
						Result res = ProblemSolver.generateRandomResultToConstraints(problem, originVars);
						if (res != null && checkNonduplicateResult(res, originVars, prevDatas,assignments)) {
							results.add(res);
						}
					}
				}
			}
		}
		
		/**
		 * generate more data points on the positive side of svm model.
		 */
		int times = 0;
		while (results.size() < MIN_MORE_SELECTED_DATA && times ++ < MIN_MORE_SELECTED_DATA) {
			List<Problem> problems = ProblemBuilder.build(null, originVars, precondition, dividers, false);
			for (Problem problem : problems) {
				Result res = ProblemSolver.generateRandomResultToConstraints(problem, originVars);
				if (res != null && checkNonduplicateResult(res, originVars, prevDatas,assignments)) {
					results.add(res);
					if (results.size() >= MIN_MORE_SELECTED_DATA) {
						break;
					}
				}
			}
		}
		
		extendWithHeuristics(results, assignments, originVars);
		selectData(target, assignments);
		return selectResult;
	}

	/**
	 * slight moving existing data points.
	 * @param results
	 * @param assignments
	 * @param originVars
	 */
	private void extendWithHeuristics(List<Result> results, List<List<Eq<?>>> assignments, 
			List<ExecVar> originVars) {
		for (Result result : results) {
			int idx = 0;
			for (ExecVar var : originVars) {
				String label = var.getLabel();
				Number value = result.get(label);
				
				//value + 1
				Result r1 = new ResultImpl();
				//value - 1
				Result r2 = new ResultImpl();
				switch (var.getType()) {
					case INTEGER:
					case CHAR:
						r1.put(label, value.intValue() + 1);
						r2.put(label, value.intValue() - 1);
						break;
					case BYTE:
						r1.put(label, value.byteValue() + 1);
						r2.put(label, value.byteValue() - 1);
						break;
					case DOUBLE:
						r1.put(label, value.doubleValue() + 1);
						r2.put(label, value.doubleValue() - 1);
						break;
					case FLOAT:
						r1.put(label, value.floatValue() + 1);
						r2.put(label, value.floatValue() - 1);
						break;
					case LONG:
						r1.put(label, value.longValue() + 1);
						r2.put(label, value.longValue() - 1);
						break;
					case SHORT:
						r1.put(label, value.shortValue() + 1);
						r2.put(label, value.shortValue() - 1);
						break;
					case BOOLEAN:
						r1.put(label, 1 - value.intValue());
						r2.put(label, 1 - value.intValue());
						break;
					default:
						break;
				}
				
				int i = 0;
				for (ExecVar execVar : originVars) {
					if (i == idx) {
						i ++;
						continue;
					}
					String tmp = execVar.getLabel();
					r1.put(tmp, result.get(tmp));
					r2.put(tmp, result.get(tmp));
					i ++;
				}
				
				checkNonduplicateResult(r1, originVars, prevDatas,assignments);
				checkNonduplicateResult(r2, originVars, prevDatas,assignments);
				idx ++;
			}
		}
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
		tcExecutor.setTcNum(list.size());
		tcExecutor.setVarMaps(list);
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
		tcExecutor.setTcNum(list.size());
		tcExecutor.setVarMaps(list);
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
	
	private void calculateValRange(List<ExecVar> vars, List<DataPoint> dataPoints) {
		minMax = new HashMap<String, Pair<Double,Double>>();
		for (DataPoint dp : dataPoints) {
			for (int i = 0; i < vars.size(); i++) {
				double val = dp.getValue(i);
				String label = vars.get(i).getLabel();
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
	}
	
	private List<Eq<Number>> selectSample(List<ExecVar> vars, Result result, int idx) {
		List<Eq<Number>> atoms = new ArrayList<Eq<Number>>();
		int i = 0;
		for (ExecVar var : vars) {
			if (i == idx) {
				i ++;
				continue;
			}
			
			Number value = null;
			String label = var.getLabel();
			Pair<Double, Double> range = minMax.get(label);
			if (range != null && ((range.b.intValue() - range.a.intValue()) > 0)) {
				value = Randomness.nextInt(range.a.intValue(), range.b.intValue());
			} else if (result == null || !result.containsVar(label)) {
				i ++;
				continue;
			} else {
				value = result.get(label);
			}
			
			if (var.getType() == ExecVarType.BOOLEAN) {
				if (value.intValue() > 0) {
					atoms.add(new Eq<Number>(var, 1));
				} else {
					atoms.add(new Eq<Number>(var, 0));
				}
			} else {
				atoms.add(new Eq<Number>(var, value));
			}
			i ++;
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
		return prevDatas.size();
	}

}
