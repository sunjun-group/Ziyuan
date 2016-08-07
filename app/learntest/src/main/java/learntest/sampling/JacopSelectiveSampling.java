package learntest.sampling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jacop.core.Domain;
import org.jacop.core.IntDomain;
import org.jacop.core.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.breakpoint.data.DecisionLocation;
import learntest.calculator.OrCategoryCalculator;
import learntest.sampling.jacop.StoreBuilder;
import learntest.sampling.jacop.StoreSearcher;
import learntest.testcase.TestcasesExecutorwithLoopTimes;
import learntest.testcase.data.BreakpointData;
import libsvm.core.Divider;
import libsvm.core.Machine.DataPoint;
import net.sf.javailp.Result;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.formula.Eq;
import sav.common.core.utils.Randomness;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;

public class JacopSelectiveSampling {
	private static Logger log = LoggerFactory.getLogger(SelectiveSampling.class);
	private static final int MAX_MORE_SELECTED_SAMPLE = 4;
	private TestcasesExecutorwithLoopTimes tcExecutor;
	private Map<String, Pair<Double, Double>> minMax;
	
	public JacopSelectiveSampling(TestcasesExecutorwithLoopTimes tcExecutor) {
		this.tcExecutor = tcExecutor;
	}
	
	public BreakpointData selectData(DecisionLocation target, 
			List<ExecVar> originVars, List<DataPoint> datapoints,
			List<Divider> dividers) throws SavException {
		List<List<Eq<?>>> assignments = new ArrayList<List<Eq<?>>>();
		
		List<Domain[]> solutions = new ArrayList<Domain[]>();
		Store basic = StoreBuilder.build(null, originVars, dividers);
		if (basic != null) {
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
			
			basic = StoreBuilder.build(null, originVars, dividers);
			solution = StoreSearcher.maxSolve(basic);
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
		for (Divider divider : dividers) {
			Store store = StoreBuilder.build(divider, originVars, dividers);
			if (store != null) {
				Domain[] solution = StoreSearcher.solve(store);
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
		}
		
		Random random = new Random();
		minMax = calculateValRange(originVars, datapoints);
		if (originVars.size() > 1) {
			for (Divider divider : dividers) {
				Store store = StoreBuilder.build(divider, originVars, dividers);
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
						for (Domain[] domains : solutions) {
							if (StoreSearcher.duplicate(domains, solution)) {
								flag = false;
								break;
							}
						}
						if (flag) {
							assignments.add(getAssignments(solution, originVars));
						}
					}
				}
			}
		}
		return selectData(target, assignments);
	}

	public BreakpointData selectData(DecisionLocation target, 
			List<ExecVar> originVars, 
			OrCategoryCalculator precondition, 
			List<Divider> current) throws SavException {
		List<Store> stores = StoreBuilder.build(originVars, precondition, current);
		List<Domain[]> solutions = StoreSearcher.solve(stores, 5);
		List<List<Eq<?>>> assignments = new ArrayList<List<Eq<?>>>();
		for (Domain[] solution : solutions) {
			assignments.add(getAssignments(solution, originVars));
		}		
		return selectData(target, assignments);
	}

	private BreakpointData selectData(DecisionLocation target, 
			List<List<Eq<?>>> assignments) throws SavException {
		if (assignments.isEmpty()) {
			return null;
		}
		BreakpointData bkpData = null;
		tcExecutor.setTarget(target);
		for (List<Eq<?>> valSet : assignments) {
			if (!valSet.isEmpty()) {
				tcExecutor.setVarMap(toInstrVarMap(valSet));
				tcExecutor.run();
				List<BreakpointData> result = tcExecutor.getResult();
				if (result.isEmpty()) {
					continue;
				}
				BreakpointData breakpointData = result.get(0);
				if(bkpData == null) {
					bkpData = breakpointData;
				} else if (!bkpData.merge(breakpointData)) {
					log.error("Wrong location: " + breakpointData.getLocation());
				}
			}
		}	
		return bkpData;
	}
	
	private List<Eq<?>> getAssignments(Domain[] solution, List<ExecVar> originVars){
		List<Eq<?>> assignments = new ArrayList<Eq<?>>();
		int idx = 0;
		for (ExecVar var : originVars) {
			Number number = ((IntDomain) solution[idx ++]).min();
			switch (var.getType()) {
				case PRIMITIVE:
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
				value = ((IntDomain)solution[i]).min();
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
}
