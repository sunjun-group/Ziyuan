package learntest.sampling.javailp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import libsvm.core.Divider;
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
import sav.common.core.utils.SingleTimer;
import sav.strategies.dto.execute.value.ExecVar;

public class ProblemSolver {
	private static Logger log = LoggerFactory.getLogger(ProblemSolver.class);
	private static SolverFactory factory = new SolverFactoryLpSolve();
	private static Random random = new Random();
	private static Map<ExecVar, Pair<Number, Number>> minMax;
	static {
		factory.setParameter(Solver.VERBOSE, 0);
		factory.setParameter(Solver.TIMEOUT, 100);
	}
	
	public static List<Result> calculateRanges(Problem problem, List<ExecVar> vars) {
		List<Result> resultList = new ArrayList<Result>();
		if (problem == null) {
			return resultList;
		}
		minMax = new HashMap<ExecVar, Pair<Number,Number>>();
		Linear linear = new Linear();
		for (ExecVar var : vars) {
			Number min = null;
			Number max = null;
			String label = var.getLabel();

			linear.add(1, label);
			Linear obj = new Linear();
			obj.add(1, label);
			
			problem.setObjective(obj, OptType.MIN);
			Solver solver = factory.get();
			Result result = solver.solve(problem);
			if (result != null) {
				resultList.add(result);
				min = result.get(label);
			}
			
			problem.setObjective(obj, OptType.MAX);
			solver = factory.get();
			result = solver.solve(problem);
			if (result != null) {
				resultList.add(result);
				max = result.get(label);
			}
			
			if (min != null && max != null && !min.equals(max)) {
				minMax.put(var, new Pair<Number, Number>(min, max));
			}
		}
		problem.setObjective(linear);
		return resultList;
	}
	
	public static List<Result> solveMultipleTimes(Problem problem, int times) {
		List<Result> res = new ArrayList<Result>();
		if (problem == null || minMax.isEmpty()) {
			return res;
		}
		Set<ExecVar> vars = minMax.keySet();
		int size = vars.size();
		while (times > 0) {
			for (ExecVar var : vars) {
				addRandomConstraint(problem, var);
				Solver solver = factory.get();
				Result result = solver.solve(problem);
				if (result != null) {
					res.add(result);
				}
				List<Constraint> constraints = problem.getConstraints();
				constraints.remove(constraints.size() - 1);
			}
			times -= size;
		}
		return res;
	}

	private static void addRandomConstraint(Problem problem, ExecVar var) {
		Linear linear = new Linear();
		linear.add(1, var.getLabel());
		Pair<Number, Number> range = minMax.get(var);
		double rand = 0;
		Number number = null;
		switch (var.getType()) {
			case DOUBLE:
				rand = random.nextDouble();
				number = range.first().doubleValue() * (1 - rand) + range.second().doubleValue() * rand;
				break;
			case FLOAT:
				rand = random.nextFloat();
				number = range.first().floatValue() * (1 - rand) + range.second().floatValue() * rand;
				break;
			case BOOLEAN:
				number = random.nextBoolean() ? 1 : 0;
				break;
			case CHAR:
			case LONG:
			case BYTE:
			case SHORT:
			default:
				rand = random.nextDouble();
				number = range.first().longValue() * (1 - rand) + range.second().longValue() * rand;
				number = number.longValue();
				break;
		}
		problem.add(new Constraint(linear, Operator.EQ, number));
	}
	
	/*public static List<Result> solveMultipleTimes(Problem problem, List<ExecVar> vars) {
		List<Result> res = new ArrayList<Result>();
		if (problem == null) {
			return res;
		}
		for (ExecVar var : vars) {
			Linear obj = new Linear();
			obj.add(1, var.getLabel());
			
			problem.setObjective(obj, OptType.MIN);
			Solver solver = factory.get();
			Result result = solver.solve(problem);
			if (result != null) {
				res.add(result);
			}
			
			problem.setObjective(obj, OptType.MAX);
			solver = factory.get();
			result = solver.solve(problem);
			if (result != null) {
				res.add(result);
			}
		}
		return res;
	}*/
	
	public static void generateRandomObjective(Problem problem, List<ExecVar> vars) {
		if (problem == null) {
			return;
		}
		
		Linear obj = new Linear();
		for (ExecVar var : vars) {
			obj.add(random.nextBoolean() ? 1 : -1 * random.nextDouble(), var.getLabel());
		}
		problem.setObjective(obj, random.nextBoolean() ? OptType.MIN : OptType.MAX);
	}

	public static Result solve(Problem problem){
		SingleTimer timer = SingleTimer.start("ilpSolver");
		Solver solver = factory.get();
		Result result = solver.solve(problem);
		timer.logResults(log, 2000l);
		return result;
	}
	
	public static Result solve(List<ExecVar> vars, List<Divider> preconditions){
		return null;
	}
}
