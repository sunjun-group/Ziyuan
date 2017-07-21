package learntest.backup.sampling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import icsetlv.sampling.IlpSolver;
import learntest.backup.breakpoint.data.DecisionLocation;
import learntest.backup.testcase.TestcasesExecutorwithLoopTimes;
import learntest.backup.testcase.data.BreakpointData;
import learntest.core.machinelearning.calculator.OrCategoryCalculator;
import libsvm.core.Divider;
import libsvm.core.Machine.DataPoint;
import mosek.Env.solveform;
import net.sf.javailp.Constraint;
import net.sf.javailp.Linear;
import net.sf.javailp.Operator;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import net.sf.javailp.Solver;
import net.sf.javailp.SolverFactory;
import net.sf.javailp.SolverFactoryLpSolve;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.formula.Eq;
import sav.common.core.formula.Formula;
import sav.strategies.dto.execute.value.ExecVar;

public class SelectiveSampling{
	private static Logger log = LoggerFactory.getLogger(SelectiveSampling.class);
	private TestcasesExecutorwithLoopTimes tcExecutor;

	private SolverFactory factory;
	private Random random;
	
	public SelectiveSampling(TestcasesExecutorwithLoopTimes tcExecutor) {
		this.tcExecutor = tcExecutor;
		factory = new SolverFactoryLpSolve();
		factory.setParameter(Solver.VERBOSE, 0);
		factory.setParameter(Solver.TIMEOUT, 100);
		random = new Random();
	}

//	public BreakpointData selectData(DecisionLocation target, Formula formula, 
//			List<String> labels, List<DataPoint> datapoints) throws SavException {
//		BreakpointData bkpData = null;		
//		if (formula == null) {
//			return bkpData;
//		}
//
//		tcExecutor.setTarget(target);
//		Map<String, Pair<Double, Double>> minMax = calculateValRange(labels, datapoints);
//		
//		IlpSolver solver = new IlpSolver(minMax, true);
//		formula.accept(solver);
//		List<List<Eq<?>>> assignments = solver.getResult();
//		log.debug("Instrument values: ");
//		for (List<Eq<?>> valSet : assignments) {
//			if (!valSet.isEmpty()) {
//				tcExecutor.setVarMap(toInstrVarMap(valSet));
//				tcExecutor.run();
//				List<BreakpointData> result = tcExecutor.getResult();
//				if (result.isEmpty()) {
//					continue;
//				}
//				BreakpointData breakpointData = result.get(0);
//				if(bkpData == null) {
//					bkpData = breakpointData;
//				} else if (!bkpData.merge(breakpointData)) {
//					log.error("Wrong location: " + breakpointData.getLocation());
//				}
//			}
//		}		
//		return bkpData;
//	}
	
//	public BreakpointData selectData(DecisionLocation target, OrCategoryCalculator preconditions, 
//			List<Divider> dividers) throws SavException {
//		List<List<Constraint>> constraints = preconditions.getConstraints();
//		List<ExecVar> vars = preconditions.getVars();
//		if (dividers != null) {
//			List<Constraint> cur = new ArrayList<Constraint>();
//			for (Divider divider : dividers) {
//				Linear linear = new Linear();
//				double[] thetas = divider.getThetas();
//				double theta0 = divider.getTheta0();
//				int size = Math.min(vars.size(), thetas.length);
//				for (int i = 0; i < size; i++) {
//					linear.add(thetas[i], vars.get(i).getLabel());
//				}
//				Constraint constraint = new Constraint(linear, Operator.GE, theta0);
//				cur.add(constraint);
//			}
//			for (List<Constraint> list : constraints) {
//				list.addAll(cur);
//			}
//		}
//		List<List<Eq<?>>> assignments = new ArrayList<List<Eq<?>>>();
//		for (List<Constraint> list : constraints) {
//			assignments.addAll(solve(list, vars));
//		}
//		if (assignments.isEmpty()) {
//			return null;
//		}
//		BreakpointData bkpData = null;
//		tcExecutor.setTarget(target);
//		for (List<Eq<?>> valSet : assignments) {
//			if (!valSet.isEmpty()) {
//				tcExecutor.setVarMap(toInstrVarMap(valSet));
//				tcExecutor.run();
//				List<BreakpointData> result = tcExecutor.getResult();
//				if (result.isEmpty()) {
//					continue;
//				}
//				BreakpointData breakpointData = result.get(0);
//				if(bkpData == null) {
//					bkpData = breakpointData;
//				} else if (!bkpData.merge(breakpointData)) {
//					log.error("Wrong location: " + breakpointData.getLocation());
//				}
//			}
//		}	
//		return bkpData;
//	}
	
	private List<List<Eq<?>>> solve(List<Constraint> list, List<ExecVar> vars) {
		List<List<Eq<?>>> assignments = new ArrayList<List<Eq<?>>>();
		for (int i = 0; i < 5; i++) {
			Problem problem = new Problem();
			for (Constraint constraint : list) {
				problem.add(constraint);
			}
			Linear obj = new Linear();
			for (ExecVar var : vars) {
				obj.add((random.nextBoolean() ? 1 : -1) * (random.nextInt(10) + 1), var.getLabel());
				setTypeAndBound(problem, var);
			}
			problem.setObjective(obj, OptType.MIN);
			Solver solver = factory.get();
			Result result = solver.solve(problem);
			if (result != null) {
				assignments.add(getAssignments(result, vars));
			}
		}
		return assignments;
	}

	private List<Eq<?>> getAssignments(Result result, List<ExecVar> vars) {
		List<Eq<?>> assignments = new ArrayList<Eq<?>>();
		for (ExecVar var : vars) {
			if (result.containsVar(var.getLabel())) {
				Number number = result.get(var.getLabel());
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
		}
		return assignments;
	}

	private void setTypeAndBound(Problem problem, ExecVar var) {
		switch (var.getType()) {
			case BOOLEAN:
				problem.setVarType(var.getLabel(), Integer.class);
				problem.setVarLowerBound(var.getLabel(), 0);
				problem.setVarUpperBound(var.getLabel(), 1);
				break;
			case BYTE:
				problem.setVarType(var.getLabel(), Byte.class);
				problem.setVarLowerBound(var.getLabel(), -100);
				problem.setVarUpperBound(var.getLabel(), 100);
				break;
			case CHAR:
				problem.setVarType(var.getLabel(), Character.class);
				problem.setVarLowerBound(var.getLabel(), 100);
				problem.setVarUpperBound(var.getLabel(), 100);
				break;
			case DOUBLE:
				problem.setVarType(var.getLabel(), Double.class);
				problem.setVarLowerBound(var.getLabel(), -2000d);
				problem.setVarUpperBound(var.getLabel(), 2000d);
				break;
			case FLOAT:
				problem.setVarType(var.getLabel(), Float.class);
				problem.setVarLowerBound(var.getLabel(), -1000f);
				problem.setVarUpperBound(var.getLabel(), 1000f);
				break;
			case LONG:
				problem.setVarType(var.getLabel(), Long.class);
				problem.setVarLowerBound(var.getLabel(), -1000l);
				problem.setVarUpperBound(var.getLabel(), 1000l);
				break;
			case SHORT:
				problem.setVarType(var.getLabel(), Short.class);
				problem.setVarLowerBound(var.getLabel(), -100);
				problem.setVarUpperBound(var.getLabel(), 100);
				break;
			default:
				problem.setVarType(var.getLabel(), Integer.class);
				problem.setVarLowerBound(var.getLabel(), -200);
				problem.setVarUpperBound(var.getLabel(), 200);
				break;
		}
	}

	private Map<String, Pair<Double, Double>> calculateValRange(
			List<String> dataLabels, List<DataPoint> dataPoints) {
		Map<String, Pair<Double, Double>> minMax = new HashMap<String, Pair<Double,Double>>();
		for (DataPoint dp : dataPoints) {
			for (int i = 0; i < dataLabels.size(); i++) {
				double val = dp.getValue(i);
				String label = dataLabels.get(i);
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
	
	private Map<String, Object> toInstrVarMap(List<Eq<?>> assignments) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Eq<?> asgt : assignments) {
			map.put(asgt.getVar().getLabel(), asgt.getValue());
		}
		return map;
	}

}
