package learntest;

import org.junit.Test;

import net.sf.javailp.Linear;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;
import net.sf.javailp.Solver;
import net.sf.javailp.SolverFactory;
import net.sf.javailp.SolverFactoryLpSolve;

public class IlpTest {

	@Test
	public void test() {
		Problem problem = new Problem();
		Linear linear = new Linear();
		linear.add(1, "x");
		problem.add(linear, ">=", 10);
		Linear obj = new Linear();
		obj.add(1, "x");
		obj.add(1, "y");
		problem.setObjective(obj, OptType.MIN);
		problem.setVarType("x", Integer.class);
		problem.setVarType("y", Integer.class);
		/*problem.setVarUpperBound("x", 2);
		problem.setVarLowerBound("x", -2);*/

		SolverFactory factory = new SolverFactoryLpSolve(); // use lp_solve
		factory.setParameter(Solver.VERBOSE, 0);
		factory.setParameter(Solver.TIMEOUT, 100);
		
		Solver solver = factory.get();
		System.out.println(solver.solve(problem));
	}
	
}
