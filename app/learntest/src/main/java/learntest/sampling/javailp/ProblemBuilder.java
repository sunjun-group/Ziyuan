package learntest.sampling.javailp;

import java.util.ArrayList;
import java.util.List;

import learntest.calculator.MultiNotDividerBasedCategoryCalculator;
import learntest.calculator.OrCategoryCalculator;
import learntest.sampling.PathRandom;
import learntest.util.Settings;
import libsvm.core.CategoryCalculator;
import libsvm.core.Divider;
import libsvm.extension.MultiDividerBasedCategoryCalculator;
import net.sf.javailp.Constraint;
import net.sf.javailp.Linear;
import net.sf.javailp.Operator;
import net.sf.javailp.Problem;
import sav.common.core.formula.Eq;
import sav.strategies.dto.execute.value.ExecVar;

public class ProblemBuilder {
	
	/**
	 * Generate some data points on the onBorder *and* on the true side of trueBorder, with regard to preconditions
	 */
	public static List<Problem> buildOnBorderTrueValueProblems(Divider equalConstraint, List<ExecVar> vars, 
			OrCategoryCalculator precondition, List<Divider> greaterThanConstraints, boolean random) {
		List<Problem> problems = buildTrueValueProblems(vars, precondition, greaterThanConstraints, random);
		
		if (!problems.isEmpty() && equalConstraint != null) {
			for (Problem problem : problems) {
				addOnBorderConstaints(problem, equalConstraint, vars);
			}
		}
		return problems;
	}
	
	public static void addOnBorderConstaints(Problem problem, Divider object, List<ExecVar> vars) {
		double[] thetas = object.getThetas();
		Linear linear = new Linear();
		int num = Math.min(thetas.length, vars.size());
		for (int i = 0; i < num; i++) {
			linear.add(thetas[i], vars.get(i).getLabel());
		}
		Constraint constraint = new Constraint(linear, Operator.EQ, object.getTheta0());
		problem.add(constraint);
	}
	
	public static void addOppositeConstraint(Problem problem, Divider divider, List<ExecVar> vars) {
		double[] thetas = divider.getThetas();
		Linear linear = new Linear();
		int num = Math.min(thetas.length, vars.size());
		for (int i = 0; i < num; i++) {
			linear.add(thetas[i], vars.get(i).getLabel());
		}
		Constraint constraint = new Constraint(linear, Operator.LE, divider.getTheta0());
		problem.add(constraint);
	}
	
	public static void addEqualConstraints(Problem problem, List<Eq<Number>> constraints) {
		for (Eq<Number> eq : constraints) {
			Linear linear = new Linear();
			linear.add(1, eq.getVar().getLabel());
			Constraint constraint = new Constraint(linear, Operator.EQ, eq.getValue());
			problem.add(constraint);
		}
	}
	
	public static List<Problem> buildFalseValueProblems(List<ExecVar> vars, OrCategoryCalculator preconditions, 
			List<Divider> contraints, boolean random) {
		List<Problem> problems = new ArrayList<Problem>();
		if (preconditions == null) {
			Problem pro = buildFalseValueProblemWithConstraints(vars, contraints, null, random); 
			problems.add(pro);
			return problems;
		}
		
		List<List<CategoryCalculator>> precons = preconditions.getCalculators();
		if (precons.isEmpty()) {
			Problem pro = buildFalseValueProblemWithConstraints(vars, contraints, null, random); 
			problems.add(pro);
			return problems;
		}
		
		/**
		 * otherwise, calculate the or-relation of precondition.
		 */
		for (List<CategoryCalculator> precon : precons) {
			List<Divider> dividers = contraints == null ? new ArrayList<Divider>() 
					: new ArrayList<Divider>(contraints);
			List<List<Divider>> notDividers = new ArrayList<List<Divider>>();
			for (CategoryCalculator calculator : precon) {
				if (calculator instanceof MultiDividerBasedCategoryCalculator) {
					dividers.addAll(((MultiDividerBasedCategoryCalculator) calculator).getDividers());
				} else if (calculator instanceof MultiNotDividerBasedCategoryCalculator) {
					unfold((MultiNotDividerBasedCategoryCalculator) calculator, notDividers);
				}
			}
			if (notDividers.isEmpty()) {
				if (!dividers.isEmpty()) {
					problems.add(buildTrueValueProblemWithConstraints(vars, dividers, null, random));
				}
			}			
			for (List<Divider> nots : notDividers) {
				problems.add(buildTrueValueProblemWithConstraints(vars, dividers, nots, random));
			}
		}
		return problems;
	}
	
	public static List<Problem> buildProblemWithPreconditions(List<ExecVar> vars, OrCategoryCalculator preconditions, 
			boolean random) {
		List<List<CategoryCalculator>> precons = preconditions.getCalculators();
		List<Problem> problems = new ArrayList<Problem>();
		
		for (List<CategoryCalculator> precon : precons) {
			List<Divider> dividers =  new ArrayList<Divider>();
			List<List<Divider>> notDividers = new ArrayList<List<Divider>>();
			for (CategoryCalculator calculator : precon) {
				if (calculator instanceof MultiDividerBasedCategoryCalculator) {
					dividers.addAll(((MultiDividerBasedCategoryCalculator) calculator).getDividers());
				} else if (calculator instanceof MultiNotDividerBasedCategoryCalculator) {
					unfold((MultiNotDividerBasedCategoryCalculator) calculator, notDividers);
				}
			}
			if (notDividers.isEmpty()) {
				if (!dividers.isEmpty()) {
					problems.add(buildTrueValueProblemWithConstraints(vars, dividers, null, random));
				}
			}			
			for (List<Divider> nots : notDividers) {
				problems.add(buildTrueValueProblemWithConstraints(vars, dividers, nots, random));
			}
		}
		
		if(problems.isEmpty()){
			Problem problem = buildVarBoundContraint(vars);
			problems.add(problem);
		}
		
		return problems;
	}

	public static List<Problem> buildTrueValueProblems(List<ExecVar> vars, OrCategoryCalculator preconditions, 
			List<Divider> contraints, boolean random) {
		List<Problem> problems = new ArrayList<Problem>();
		if (preconditions == null) {
			Problem pro = buildTrueValueProblemWithConstraints(vars, contraints, null, random); 
			problems.add(pro);
			return problems;
		}
		
		List<List<CategoryCalculator>> precons = preconditions.getCalculators();
		if (precons.isEmpty()) {
			Problem pro = buildTrueValueProblemWithConstraints(vars, contraints, null, random); 
			problems.add(pro);
			return problems;
		}
		
		/**
		 * otherwise, calculate the or-relation of precondition.
		 */
		for (List<CategoryCalculator> precon : precons) {
			List<Divider> dividers = contraints == null ? new ArrayList<Divider>() 
					: new ArrayList<Divider>(contraints);
			List<List<Divider>> notDividers = new ArrayList<List<Divider>>();
			for (CategoryCalculator calculator : precon) {
				if (calculator instanceof MultiDividerBasedCategoryCalculator) {
					dividers.addAll(((MultiDividerBasedCategoryCalculator) calculator).getDividers());
				} else if (calculator instanceof MultiNotDividerBasedCategoryCalculator) {
					unfold((MultiNotDividerBasedCategoryCalculator) calculator, notDividers);
				}
			}
			if (notDividers.isEmpty()) {
				if (!dividers.isEmpty()) {
					problems.add(buildTrueValueProblemWithConstraints(vars, dividers, null, random));
				}
			}			
			for (List<Divider> nots : notDividers) {
				problems.add(buildTrueValueProblemWithConstraints(vars, dividers, nots, random));
			}
		}
		return problems;
	}
	
	private static void unfold(MultiNotDividerBasedCategoryCalculator calculator, 
			List<List<Divider>> res) {
		if (calculator == null) {
			return;
		}
		List<Divider> dividers = calculator.getDividers();
		if (dividers == null || dividers.isEmpty()) {
			return;
		}
		if (res.isEmpty()) {
			for (Divider divider : dividers) {
				List<Divider> list = new ArrayList<Divider>();
				list.add(divider);
				res.add(list);
			}
		} else {
			List<List<Divider>> copy = new ArrayList<List<Divider>>(res);
			res.clear();
			for (List<Divider> list : copy) {
				for (Divider divider : dividers) {
					List<Divider> tmp = new ArrayList<Divider>(list);
					tmp.add(divider);
					res.add(tmp);
				}
			}
		}
	}
	
	/**
	 * dividers for "greater than" constraints and non-dividers for "less than" constraints
	 * 
	 * @param vars
	 * @param dividers
	 * @param notDividers
	 * @param random
	 * @return
	 */
	private static Problem buildFalseValueProblemWithConstraints(List<ExecVar> vars, List<Divider> dividers, 
			List<Divider> notDividers, boolean random) {
		Problem problem = buildVarBoundContraint(vars);
		if (random) {
			PathRandom randomPath = PathRandom.randomPath(dividers, notDividers);
			dividers = randomPath.getDividers();
			notDividers = randomPath.getNotDividers();
		}
		if (dividers != null) {
			for (Divider divider : dividers) {
				double[] thetas = divider.getThetas();
				Linear linear = new Linear();
				int num = Math.min(thetas.length, vars.size());
				for (int i = 0; i < num; i++) {
					linear.add(thetas[i], vars.get(i).getLabel());
				}
				Constraint constraint = new Constraint(linear, Operator.LE, divider.getTheta0());
				problem.add(constraint);
			}
		}
		if (notDividers != null) {
			for (Divider divider : notDividers) {
				double[] thetas = divider.getThetas();
				Linear linear = new Linear();
				int num = Math.min(thetas.length, vars.size());
				for (int i = 0; i < num; i++) {
					linear.add(thetas[i], vars.get(i).getLabel());
				}
				//bug: should be LT instead of LE
				Constraint constraint = new Constraint(linear, Operator.GE, divider.getTheta0());
				problem.add(constraint);
			}
		}
		return problem;
	}
	
	
	/**
	 * dividers for "greater than" constraints and non-dividers for "less than" constraints
	 * 
	 * @param vars
	 * @param dividers
	 * @param notDividers
	 * @param random
	 * @return
	 */
	private static Problem buildTrueValueProblemWithConstraints(List<ExecVar> vars, List<Divider> dividers, 
			List<Divider> notDividers, boolean random) {
		Problem problem = buildVarBoundContraint(vars);
		if (random) {
			PathRandom randomPath = PathRandom.randomPath(dividers, notDividers);
			dividers = randomPath.getDividers();
			notDividers = randomPath.getNotDividers();
		}
		if (dividers != null) {
			for (Divider divider : dividers) {
				double[] thetas = divider.getThetas();
				Linear linear = new Linear();
				int num = Math.min(thetas.length, vars.size());
				for (int i = 0; i < num; i++) {
					linear.add(thetas[i], vars.get(i).getLabel());
				}
				Constraint constraint = new Constraint(linear, Operator.GE, divider.getTheta0());
				problem.add(constraint);
			}
		}
		if (notDividers != null) {
			for (Divider divider : notDividers) {
				double[] thetas = divider.getThetas();
				Linear linear = new Linear();
				int num = Math.min(thetas.length, vars.size());
				for (int i = 0; i < num; i++) {
					linear.add(thetas[i], vars.get(i).getLabel());
				}
				//bug: should be LT instead of LE
				Constraint constraint = new Constraint(linear, Operator.LE, divider.getTheta0());
				problem.add(constraint);
			}
		}
		return problem;
	}
	
	public static Problem buildVarBoundContraint(List<ExecVar> vars) {
		
		int bound = Settings.bound;
		
		Problem problem = new Problem();
		for (ExecVar var : vars) {
			switch (var.getType()) {
				case BOOLEAN:
					problem.setVarType(var.getLabel(), Integer.class);
					problem.setVarLowerBound(var.getLabel(), 0);
					problem.setVarUpperBound(var.getLabel(), 1);
					break;
				case BYTE:
					problem.setVarType(var.getLabel(), Byte.class);
					/*problem.setVarLowerBound(var.getLabel(), Byte.MIN_VALUE);
					problem.setVarUpperBound(var.getLabel(), Byte.MAX_VALUE);*/
					problem.setVarLowerBound(var.getLabel(), -100);
					problem.setVarUpperBound(var.getLabel(), 100);
					break;
				case CHAR:
					problem.setVarType(var.getLabel(), Character.class);
					/*problem.setVarLowerBound(var.getLabel(), (int)Character.MIN_VALUE);
					problem.setVarUpperBound(var.getLabel(), (int)Character.MAX_VALUE);*/
					problem.setVarLowerBound(var.getLabel(), -100);
					problem.setVarUpperBound(var.getLabel(), 100);
					break;
				case DOUBLE:
					problem.setVarType(var.getLabel(), Double.class);
					/*problem.setVarLowerBound(var.getLabel(), Double.MIN_VALUE);
					problem.setVarUpperBound(var.getLabel(), Double.MAX_VALUE);*/
					problem.setVarLowerBound(var.getLabel(), -bound);
					problem.setVarUpperBound(var.getLabel(), bound);
					break;
				case FLOAT:
					problem.setVarType(var.getLabel(), Float.class);
					/*problem.setVarLowerBound(var.getLabel(), Float.MIN_VALUE);
					problem.setVarUpperBound(var.getLabel(), Float.MAX_VALUE);*/
					problem.setVarLowerBound(var.getLabel(), -bound);
					problem.setVarUpperBound(var.getLabel(), bound);
					break;
				case LONG:
					problem.setVarType(var.getLabel(), Long.class);
					/*problem.setVarLowerBound(var.getLabel(), Long.MIN_VALUE);
					problem.setVarUpperBound(var.getLabel(), Long.MAX_VALUE);*/
					problem.setVarLowerBound(var.getLabel(), -100);
					problem.setVarUpperBound(var.getLabel(), 100);
					break;
				case SHORT:
					problem.setVarType(var.getLabel(), Short.class);
					/*problem.setVarLowerBound(var.getLabel(), Short.MIN_VALUE);
					problem.setVarUpperBound(var.getLabel(), Short.MAX_VALUE);*/
					problem.setVarLowerBound(var.getLabel(), -100);
					problem.setVarUpperBound(var.getLabel(), 100);
					break;
				default:
					problem.setVarType(var.getLabel(), Integer.class);
					/*problem.setVarLowerBound(var.getLabel(), Integer.MIN_VALUE);
					problem.setVarUpperBound(var.getLabel(), Integer.MAX_VALUE);*/
					problem.setVarLowerBound(var.getLabel(), -bound);
					problem.setVarUpperBound(var.getLabel(), bound);
					break;
			}
		}
		return problem;
	}

}
