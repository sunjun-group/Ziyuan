package learntest.gentest;

import java.util.ArrayList;
import java.util.List;

import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import net.sf.javailp.Solver;
import net.sf.javailp.SolverFactory;
import net.sf.javailp.SolverFactoryLpSolve;
import sav.common.core.formula.Formula;

public class PathSolver {
	private SolverFactory factory;
	
	public PathSolver() {
		factory = new SolverFactoryLpSolve();
		factory.setParameter(Solver.VERBOSE, 0);
		factory.setParameter(Solver.TIMEOUT, 100);
	}

	public List<Result> solve(List<List<Formula>> paths) {
		List<Result> res = new ArrayList<Result>();
		for (List<Formula> path : paths) {
			solve(path, res);
		}
		return res;
	}

	private void solve(List<Formula> path, List<Result> res) {
		ConstraintVisitor visitor = new ConstraintVisitor();
		for (Formula constraint : path) {
			constraint.accept(visitor);
		}
		while (visitor.hasNextProblem()) {
			Problem problem = visitor.getProblem();
			Solver solver = factory.get();
			Result result = solver.solve(problem);
			if (result != null) {
				res.add(result);
				break;
			}
		}		
	}
	
}
