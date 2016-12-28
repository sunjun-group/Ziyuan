package learntest.sampling.javailp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import learntest.calculator.MultiNotDividerBasedCategoryCalculator;
import learntest.calculator.OrCategoryCalculator;
import learntest.sampling.PathRandom;
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
	
	private static Random random = new Random();
	
	public static List<Problem> build(Divider object, List<ExecVar> vars, 
			OrCategoryCalculator calculator, List<Divider> dividers, boolean random) {
		List<Problem> problems = build(vars, calculator, dividers, random);
		if (!problems.isEmpty() && object != null) {
			for (Problem problem : problems) {
				addTarget(problem, object, vars);
			}
		}
		return problems;
	}
	
	private static void addTarget(Problem problem, Divider object, List<ExecVar> vars) {
		double[] thetas = object.getThetas();
		Linear linear = new Linear();
		int num = Math.min(thetas.length, vars.size());
		for (int i = 0; i < num; i++) {
			linear.add(thetas[i], vars.get(i).getLabel());
		}
		Constraint constraint = new Constraint(linear, Operator.EQ, object.getTheta0());
		problem.add(constraint);
	}
	
	public static void addOpposite(Problem problem, Divider divider, List<ExecVar> vars) {
		double[] thetas = divider.getThetas();
		Linear linear = new Linear();
		int num = Math.min(thetas.length, vars.size());
		for (int i = 0; i < num; i++) {
			linear.add(thetas[i], vars.get(i).getLabel());
		}
		Constraint constraint = new Constraint(linear, Operator.LE, divider.getTheta0());
		problem.add(constraint);
	}
	
	//TODO add constraint according to variable type
	public static void addConstraints(Problem problem, List<Eq<Number>> constraints) {
		for (Eq<Number> eq : constraints) {
			Linear linear = new Linear();
			linear.add(1, eq.getVar().getLabel());
			Constraint constraint = new Constraint(linear, Operator.EQ, eq.getValue());
			problem.add(constraint);
		}
	}
	
	public static void addRandomConstraint(List<Problem> problems, List<ExecVar> vars) {
		for (Problem problem : problems) {
			Linear linear = new Linear();
			linear.add(1, vars.get(random.nextInt(vars.size())).getLabel());
			Constraint constraint = new Constraint(linear, Operator.EQ, 
					random.nextBoolean() ? 1 : -1 * random.nextInt(101));
			problem.add(constraint);
		}
	}

	public static List<Problem> build(List<ExecVar> vars, OrCategoryCalculator orCalculator, 
			List<Divider> current, boolean random) {
		List<Problem> res = new ArrayList<Problem>();
		if (orCalculator == null) {
			res.add(build(vars, current, null, random));
			return res;
		}
		List<List<CategoryCalculator>> calculators = orCalculator.getCalculators();
		if (calculators.isEmpty()) {
			res.add(build(vars, current, null, random));
			return res;
		}
		
		/**
		 * otherwise, calculate the or-relation of precondition.
		 */
		for (List<CategoryCalculator> list : calculators) {
			List<Divider> dividers = current == null ? new ArrayList<Divider>() 
					: new ArrayList<Divider>(current);
			List<List<Divider>> notDividers = new ArrayList<List<Divider>>();
			for (CategoryCalculator calculator : list) {
				if (calculator instanceof MultiDividerBasedCategoryCalculator) {
					dividers.addAll(((MultiDividerBasedCategoryCalculator) calculator).getDividers());
				} else if (calculator instanceof MultiNotDividerBasedCategoryCalculator) {
					unfold((MultiNotDividerBasedCategoryCalculator) calculator, notDividers);
				}
			}
			if (notDividers.isEmpty()) {
				if (!dividers.isEmpty()) {
					res.add(build(vars, dividers, null, random));
				}
			}			
			for (List<Divider> nots : notDividers) {
				res.add(build(vars, dividers, nots, random));
			}
		}
		return res;
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
	
	private static Problem build(List<ExecVar> vars, List<Divider> dividers, 
			List<Divider> notDividers, boolean random) {
		Problem problem = build(vars);
		if (random) {
			PathRandom.randomPath(dividers, notDividers);
			dividers = PathRandom.dividers;
			notDividers = PathRandom.notDividers;
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
	
	private static Problem build(List<ExecVar> vars) {
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
					problem.setVarLowerBound(var.getLabel(), -100);
					problem.setVarUpperBound(var.getLabel(), 100);
					break;
				case FLOAT:
					problem.setVarType(var.getLabel(), Float.class);
					/*problem.setVarLowerBound(var.getLabel(), Float.MIN_VALUE);
					problem.setVarUpperBound(var.getLabel(), Float.MAX_VALUE);*/
					problem.setVarLowerBound(var.getLabel(), -100);
					problem.setVarUpperBound(var.getLabel(), 100);
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
					problem.setVarLowerBound(var.getLabel(), -100);
					problem.setVarUpperBound(var.getLabel(), 100);
					break;
			}
		}
		return problem;
	}

}
