package learntest.sampling.javailp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.sf.javailp.Linear;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import net.sf.javailp.Solver;
import net.sf.javailp.SolverFactory;
import net.sf.javailp.SolverFactoryLpSolve;
import sav.strategies.dto.execute.value.ExecVar;

public class ProblemSolver {
	
	private static SolverFactory factory = new SolverFactoryLpSolve();
	private static Random random = new Random();
	static {
		factory.setParameter(Solver.VERBOSE, 0);
		factory.setParameter(Solver.TIMEOUT, 100);
	}
	
	public static List<Result> solveMultipleTimes(Problem problem, List<ExecVar> vars) {
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
	}
	
	public static Result generateRandomResultToConstraints(Problem problem, List<ExecVar> vars) {
		if (problem == null) {
			return null;
		}
		
		Linear obj = new Linear();
		for (ExecVar var : vars) {
			obj.add(random.nextBoolean() ? 1 : -1 * random.nextDouble(), var.getLabel());
		}
		problem.setObjective(obj, random.nextBoolean() ? OptType.MIN : OptType.MAX);
		
		Solver solver = factory.get();
		return solver.solve(problem);
	}

}
