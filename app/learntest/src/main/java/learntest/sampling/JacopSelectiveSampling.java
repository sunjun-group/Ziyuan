package learntest.sampling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jacop.core.Domain;
import org.jacop.core.Store;
/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;*/
import org.jacop.floats.core.FloatDomain;
import org.jacop.floats.core.FloatIntervalDomain;

import learntest.breakpoint.data.DecisionLocation;
import learntest.calculator.OrCategoryCalculator;
import learntest.sampling.jacop.StoreBuilder;
import learntest.sampling.jacop.StoreSearcher;
import learntest.testcase.TestcasesExecutorwithLoopTimes;
import learntest.testcase.data.BreakpointData;
import learntest.testcase.data.LoopTimesData;
import libsvm.core.Divider;
import libsvm.core.Machine.DataPoint;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.formula.Eq;
import sav.common.core.utils.Randomness;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;

public class JacopSelectiveSampling {
	//private static Logger log = LoggerFactory.getLogger(SelectiveSampling.class);
	private static final int MAX_MORE_SELECTED_SAMPLE = 4;
	private static final int MIN_MORE_SELECTED_DATA = 5;
	private TestcasesExecutorwithLoopTimes tcExecutor;
	private Map<String, Pair<Double, Double>> minMax;
	
	private Map<DecisionLocation, BreakpointData> selectResult;
	
	private List<Domain[]> prevDatas;
	
	private int numPerExe = 100;
	private int timesLimit = 20;
	
	//private long startTime;
	
	public JacopSelectiveSampling(TestcasesExecutorwithLoopTimes tcExecutor) {
		this.tcExecutor = tcExecutor;
		prevDatas = new ArrayList<Domain[]>();
	}
	
	public void addPrevValues(List<Domain[]> values) throws SAVExecutionTimeOutException {
		for (Domain[] value : values) {
			if (value == null) {
				continue;
			}
			boolean flag = true;
			for (Domain[] domains : prevDatas) {
				if (StoreSearcher.duplicate(domains, value)) {
					flag = false;
					break;
				}
			}
			if (flag) {
				prevDatas.add(value);
			}
		}
	}
	
	public Map<DecisionLocation, BreakpointData> selectDataForModel(DecisionLocation target, 
			List<ExecVar> originVars, List<DataPoint> datapoints,
			OrCategoryCalculator precondition, List<Divider> dividers) throws SavException, SAVExecutionTimeOutException {
		List<List<Eq<?>>> assignments = new ArrayList<List<Eq<?>>>();
		
		//startTime = System.currentTimeMillis();
		List<Domain[]> solutions = new ArrayList<Domain[]>();
		List<Store> basics = StoreBuilder.build(null, originVars, precondition, dividers, false);
		for (Store basic : basics) {
			Domain[] solution = StoreSearcher.solve(basic);
			if (solution != null) {
				boolean flag = true;
				for (Domain[] domains : prevDatas) {
					if (StoreSearcher.duplicate(domains, solution)) {
						flag = false;
						break;
					}
				}
				if (flag) {
					assignments.add(getAssignments(solution, originVars));
					solutions.add(solution);
					prevDatas.add(solution);
				}
			}
		}
		/*List<Store> basics = StoreBuilder.build(null, originVars, precondition, dividers);
		for (Store basic : basics) {
			Domain[] solution = StoreSearcher.minSolve(basic);
			if (solution != null) {
				boolean flag = true;
				for (Domain[] domains : solutions) {
					if (StoreSearcher.duplicate(domains, solution)) {
						flag = false;
						break;
					}
				}
				if (flag) {
					assignments.add(getAssignments(solution, originVars));
					solutions.add(solution);
				}
			}
		}			
		
		basics = StoreBuilder.build(null, originVars, precondition, dividers);
		for (Store basic : basics) {
			Domain[] solution = StoreSearcher.maxSolve(basic);
			if (solution != null) {
				boolean flag = true;
				for (Domain[] domains : solutions) {
					if (StoreSearcher.duplicate(domains, solution)) {
						flag = false;
						break;
					}
				}
				if (flag) {
					assignments.add(getAssignments(solution, originVars));
					solutions.add(solution);
				}				
			}
		}*/
			
		for (Divider divider : dividers) {
			List<Store> stores = StoreBuilder.build(divider, originVars, precondition, dividers, false);
			if (!stores.isEmpty()) {
				for (Store store : stores) {
					Domain[] solution = StoreSearcher.solve(store);
					if (solution != null) {
						boolean flag = true;
						for (Domain[] domains : prevDatas) {
							if (StoreSearcher.duplicate(domains, solution)) {
								flag = false;
								break;
							}
						}
						if (flag) {
							assignments.add(getAssignments(solution, originVars));
							solutions.add(solution);
							prevDatas.add(solution);
						}
					}
				}				
			}
		}
		
		int size = dividers.size();
		for (int i = 0; i < size; i++) {
			List<Divider> list = new ArrayList<Divider>(dividers);
			list.remove(i);
			List<Store> stores = StoreBuilder.build(null, originVars, precondition, list, false);
			Divider divider = dividers.get(i);
			for (Store store : stores) {
				StoreBuilder.addOpposite(store, divider);
				Domain[] solution = StoreSearcher.solve(store);
				if (solution != null) {
					boolean flag = true;
					for (Domain[] domains : prevDatas) {
						if (StoreSearcher.duplicate(domains, solution)) {
							flag = false;
							break;
						}
					}
					if (flag) {
						assignments.add(getAssignments(solution, originVars));
						solutions.add(solution);
						prevDatas.add(solution);
					}
				}
			}
		}
		
		/*for (int i = 0; i < size; i++) {
			List<Divider> list = new ArrayList<Divider>(dividers);
			list.remove(i);
			List<Store> stores = StoreBuilder.build(null, originVars, precondition, list);
			Divider divider = dividers.get(i);
			for (Store store : stores) {
				StoreBuilder.addOpposite(store, divider);
				Domain[] solution = StoreSearcher.maxSolve(store);
				if (solution != null) {
					boolean flag = true;
					for (Domain[] domains : solutions) {
						if (StoreSearcher.duplicate(domains, solution)) {
							flag = false;
							break;
						}
					}
					if (flag) {
						assignments.add(getAssignments(solution, originVars));
						solutions.add(solution);
					}
				}
			}
		}*/
		
		Random random = new Random();
		minMax = calculateValRange(originVars, datapoints);
		if (originVars.size() > 1) {
			for (Divider divider : dividers) {
				List<Store> stores = StoreBuilder.build(divider, originVars, precondition, dividers, false);
				for (Store store : stores) {
					for (int i = 0; i < MAX_MORE_SELECTED_SAMPLE; i++) {
						int idx = random.nextInt(originVars.size());
						List<Eq<Number>> samples = selectSample(originVars, 
								solutions.isEmpty() ? null 
								: solutions.get(random.nextInt(solutions.size())), idx);
						if (samples.isEmpty()) {
							continue;
						}
						StoreBuilder.addConstraints(store, samples);
						Domain[] solution = StoreSearcher.solve(store);
						if (solution != null) {
							boolean flag = true;
							for (Domain[] domains : prevDatas) {
								if (StoreSearcher.duplicate(domains, solution)) {
									flag = false;
									break;
								}
							}
							if (flag) {
								assignments.add(getAssignments(solution, originVars));
								solutions.add(solution);
								prevDatas.add(solution);
							}
						}
					}
				}				
			}
		}
		
		int times = 0;
		while (solutions.size() < MIN_MORE_SELECTED_DATA && times ++ < MIN_MORE_SELECTED_DATA) {
			List<Store> stores = StoreBuilder.build(null, originVars, precondition, dividers, false);
			//List<Domain[]> more = StoreSearcher.solve(stores, MIN_MORE_SELECTED_DATA);
			List<Domain[]> more = StoreSearcher.solve(stores);
			//List<Domain[]> more = StoreSearcher.solveAll(stores);
			for (Domain[] solution : more) {
				if (solution != null) {
					boolean flag = true;
					for (Domain[] domains : prevDatas) {
						if (StoreSearcher.duplicate(domains, solution)) {
							flag = false;
							break;
						}
					}
					if (flag) {
						assignments.add(getAssignments(solution, originVars));
						solutions.add(solution);
						prevDatas.add(solution);
						if (solutions.size() >= MIN_MORE_SELECTED_DATA) {
							break;
						}
					}
				}
			}
		}		
		//System.out.println("selectDataForModel constraints solving time: " + (System.currentTimeMillis() - startTime) + " ms");
		
		//startTime = System.currentTimeMillis();
		extendWithHeuristics(solutions, assignments, originVars);
		//System.out.println("selectDataForModel extend with heuristics time: " + (System.currentTimeMillis() - startTime) + " ms");
		
		//startTime = System.currentTimeMillis();
		selectData(target, assignments);
		//System.out.println("selectDataForModel test cases execution time: " + (System.currentTimeMillis() - startTime) + " ms, "
				//+ assignments.size() + "test cases");
		return selectResult;
	}

	private void extendWithHeuristics(List<Domain[]> solutions, List<List<Eq<?>>> assignments, List<ExecVar> originVars) throws SAVExecutionTimeOutException {
		/*int xIdx = 0;
		int yIdx = 0;
		int zIdx = 0;
		int idx = 0;
		for (ExecVar execVar : originVars) {
			char label = execVar.getLabel().charAt(0);
			switch (label) {
				case 'x':
					xIdx = idx;
					break;
				case 'y':
					yIdx = idx;
					break;
				case 'z':
					zIdx = idx;
					break;
			}
			idx ++;
		}*/
		List<Domain[]> tmp = new ArrayList<Domain[]>();
		for (Domain[] domains : solutions) {
			for(int i = 0; i < StoreSearcher.length; i ++) {
				double value = ((FloatDomain) domains[i]).min();
				if (value > -StoreBuilder.max) {
					Domain[] new1 = new Domain[domains.length];
					if (originVars.get(i).getType() == ExecVarType.INTEGER) {
						new1[i] = new FloatIntervalDomain(value - 1, value - 1);
					} else if (originVars.get(i).getType() == ExecVarType.BOOLEAN) {
						new1[i] = new FloatIntervalDomain(1 - value, 1 - value);
					} else {
						new1[i] = new FloatIntervalDomain(value - 0.01, value - 0.01);
					}
					for (int j = 0; j < i; j++) {
						new1[j] = domains[j];
					}
					for (int j = i + 1; j < new1.length; j++) {
						new1[j] = domains[j];
					}
					tmp.add(new1);
				}
				if (value < StoreBuilder.max) {
					Domain[] new1 = new Domain[domains.length];
					if (originVars.get(i).getType() == ExecVarType.INTEGER) {
						new1[i] = new FloatIntervalDomain(value + 1, value + 1);
					} else if (originVars.get(i).getType() == ExecVarType.BOOLEAN) {
						new1[i] = new FloatIntervalDomain(1 - value, 1 - value);
					} else {
						new1[i] = new FloatIntervalDomain(value + 0.01, value + 0.01);
					}
					for (int j = 0; j < i; j++) {
						new1[j] = domains[j];
					}
					for (int j = i + 1; j < new1.length; j++) {
						new1[j] = domains[j];
					}
					tmp.add(new1);
				}
			}
			/*int x = ((IntDomain) domains[xIdx]).min();
			int y = ((IntDomain) domains[yIdx]).min();
			int z = ((IntDomain) domains[zIdx]).min();
			for (int i = 0; i < domains.length; i++) {
				int value = ((IntDomain) domains[i]).min();
				if (value > 1 && (i == zIdx || (i == xIdx && value > y) || (i == yIdx && value > z))) {
					Domain[] new1 = new Domain[domains.length];
					new1[i] = new BoundDomain(value - 1, value - 1);
					for (int j = 0; j < i; j++) {
						new1[j] = domains[j];
					}
					for (int j = i + 1; j < new1.length; j++) {
						new1[j] = domains[j];
					}
					tmp.add(new1);
				}
				if (value < 200 && (i == xIdx || (i == yIdx && value < x) || (i == zIdx && value < y))) {
					Domain[] new1 = new Domain[domains.length];
					new1[i] = new BoundDomain(value + 1, value + 1);
					for (int j = 0; j < i; j++) {
						new1[j] = domains[j];
					}
					for (int j = i + 1; j < new1.length; j++) {
						new1[j] = domains[j];
					}
					tmp.add(new1);
				}
			}*/
		}
		for (Domain[] solution : tmp) {
			boolean flag = true;
			for (Domain[] domains : prevDatas) {
				if (StoreSearcher.duplicate(domains, solution)) {
					flag = false;
					break;
				}
			}
			if (flag) {
				assignments.add(getAssignments(solution, originVars));
				solutions.add(solution);
				prevDatas.add(solution);
			}
		}
	}
	
	public Map<DecisionLocation, BreakpointData> randomSelectData(List<ExecVar> originVars) 
			throws SAVExecutionTimeOutException, SavException {
		tcExecutor.setTarget(null);
		List<List<Eq<?>>> assignments = new ArrayList<List<Eq<?>>>();
		//for (int j = 0; j < numPerExe; j++) {
			List<Store> stores = StoreBuilder.build(originVars, null, null, false);
			List<Domain[]> solutions = StoreSearcher.solve(stores, numPerExe);
			//List<Domain[]> solutions = StoreSearcher.solveAll(stores);
			for (Domain[] solution : solutions) {
				/*boolean flag = true;
				for (Domain[] domains : prevDatas) {
					if (StoreSearcher.duplicate(domains, solution)) {
						flag = false;
						break;
					}
				}
				if (!flag) {
					continue;
				}*/
				prevDatas.add(solution);
				assignments.add(getAssignments(solution, originVars));
			}	
		//}
		//cnt ++;
		selectData(assignments);
		return selectResult;
	}

	public Map<DecisionLocation, BreakpointData> selectDataForEmpty(DecisionLocation target, 
			List<ExecVar> originVars, 
			OrCategoryCalculator precondition, 
			List<Divider> current, 
			boolean trueOrFalse, 
			boolean isLoop) throws SavException, SAVExecutionTimeOutException {
		
		tcExecutor.setTarget(null);

		//parameters for Randoop
		/*int timesLimit = 2000;
		int numPerExe = 10;*/
		//parameters for L2T
		/*int timesLimit = 200;
		int numPerExe = 100;*/
		
		//int cnt = 0;
		for (int i = 0; i < timesLimit; i++) {
			List<List<Eq<?>>> assignments = new ArrayList<List<Eq<?>>>();
			/*for (int j = 0; j < numPerExe; j++) {
				List<Store> stores = StoreBuilder.build(originVars, precondition, current, true);
				List<Domain[]> solutions = StoreSearcher.solve(stores);
				//List<Domain[]> solutions = StoreSearcher.solveAll(stores);
				for (Domain[] solution : solutions) {
					boolean flag = true;
					for (Domain[] domains : prevDatas) {
						if (StoreSearcher.duplicate(domains, solution)) {
							flag = false;
							break;
						}
					}
					if (!flag) {
						continue;
					}
					prevDatas.add(solution);
					assignments.add(getAssignments(solution, originVars));
				}	
			}*/
			List<Store> stores = StoreBuilder.build(originVars, precondition, current, true);
			List<Domain[]> solutions = StoreSearcher.solve(stores, numPerExe);
			for (Domain[] solution : solutions) {
				/*boolean flag = true;
				for (Domain[] domains : prevDatas) {
					if (StoreSearcher.duplicate(domains, solution)) {
						flag = false;
						break;
					}
				}
				if (!flag) {
					continue;
				}*/
				prevDatas.add(solution);
				assignments.add(getAssignments(solution, originVars));
			}
			//cnt ++;
			selectData(assignments);
			if (selectResult == null) {
				continue;
			}
			BreakpointData selectData = selectResult.get(target);
			if (!isLoop) {
				if (trueOrFalse && !selectData.getTrueValues().isEmpty()) {
					//System.out.println(cnt);
					//selectData(getAssignments(solution, originVars), null);
					return selectResult;
				}
				if (!trueOrFalse && !selectData.getFalseValues().isEmpty()) {
					//System.out.println(cnt);
					//selectData(getAssignments(solution, originVars), null);
					return selectResult;
				}
			} else {
				LoopTimesData loopTimesData = (LoopTimesData) selectData;
				if (trueOrFalse && !loopTimesData.getMoreTimesValues().isEmpty()) {
					//System.out.println(cnt);
					//selectData(getAssignments(solution, originVars), null);
					return selectResult;
				}
				if (!trueOrFalse && !loopTimesData.getOneTimeValues().isEmpty()) {
					//System.out.println(cnt);
					//selectData(getAssignments(solution, originVars), null);
					return selectResult;
				}
			}
		}
		//System.out.println(cnt);
		return null;
	}
	
	public Map<DecisionLocation, BreakpointData> getSelectResult() {
		return selectResult;
	}

	private void selectData(List<List<Eq<?>>> assignments/*, DecisionLocation target*/) throws SavException, SAVExecutionTimeOutException {
		if (assignments.isEmpty()) {
			return;
		}
		//tcExecutor.setTarget(/*target*/null);
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
		/*Map<DecisionLocation, BreakpointData> result = tcExecutor.getResult();
		mergeMap(result);
		return result.get(target);*/
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
				//tcExecutor.setVarMap(toInstrVarMap(valSet));
				//tcExecutor.run();
				/*selectResult = tcExecutor.getResult();
				if (result.isEmpty()) {
					continue;
				}
				BreakpointData breakpointData = result.get(0);
				if(bkpData == null) {
					bkpData = breakpointData;
				} else if (!bkpData.merge(breakpointData)) {
					log.error("Wrong location: " + breakpointData.getLocation());
				}*/
			}
		}
		tcExecutor.setTcNum(list.size());
		tcExecutor.setVarMaps(list);
		tcExecutor.run();
		selectResult = tcExecutor.getResult();
	}
	
	private List<Eq<?>> getAssignments(Domain[] solution, List<ExecVar> originVars){
		List<Eq<?>> assignments = new ArrayList<Eq<?>>();
		int idx = 0;
		for (ExecVar var : originVars) {
			Number number = ((FloatDomain) solution[idx ++]).min();
			switch (var.getType()) {				
				case DOUBLE:
				case FLOAT:
				case LONG:
					assignments.add(new Eq<Number>(var, number));
					break;
				case INTEGER:
				case BYTE:
				case CHAR:
				case SHORT:
					assignments.add(new Eq<Number>(var, number.intValue()));
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
		return assignments;
	}
	
	private Map<String, Object> toInstrVarMap(List<Eq<?>> assignments) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Eq<?> asgt : assignments) {
			map.put(asgt.getVar().getLabel(), asgt.getValue());
		}
		return map;
	}
	
	private Map<String, Pair<Double, Double>> calculateValRange(
			List<ExecVar> vars, List<DataPoint> dataPoints) {
		Map<String, Pair<Double, Double>> minMax = new HashMap<String, Pair<Double,Double>>();
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
		return minMax;
	}
	
	private List<Eq<Number>> selectSample(List<ExecVar> vars, Domain[] solution, int idx) {
		List<Eq<Number>> atoms = new ArrayList<Eq<Number>>();
		int i = 0;
		for (ExecVar var : vars) {
			if (i == idx) {
				i ++;
				continue;
			}
			Number value = null;
			Pair<Double, Double> range = minMax.get(var.getLabel());
			if (range != null && ((range.b.intValue() - range.a.intValue()) > 0)) {
				value = Randomness.nextInt(range.a.intValue(), range.b.intValue());
			} else if (solution == null) {
				continue;
			} else {
				value = ((FloatDomain)solution[i]).min();
			}
			if (var.getType() == ExecVarType.BOOLEAN) {
				if (value.intValue() > 0) {
					atoms.add(new Eq<Number>(var, 1));
				} else {
					atoms.add(new Eq<Number>(var, 0));
				}
			} else if (var.getType() == ExecVarType.INTEGER) {
				atoms.add(new Eq<Number>(var, value.intValue()));
			}else {
				atoms.add(new Eq<Number>(var, value));
			}
			i ++;
		}
		return atoms;
	}
	
	/*public void setNumLimit(int limit) {
		if (limit <= 100) {
			numPerExe = limit;
			timesLimit = 1;
		} else {
			if (limit > 10000) {
				limit = 10000;
			}
			numPerExe = 100;
			timesLimit = limit / 100;
			if (limit - 100 * timesLimit >= 50) {
				timesLimit += 1;
			}
		}
	}*/
	
	public int getTotalNum() {
		return prevDatas.size();
	}
}
