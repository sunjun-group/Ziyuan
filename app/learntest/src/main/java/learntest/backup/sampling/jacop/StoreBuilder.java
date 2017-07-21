package learntest.backup.sampling.jacop;

import java.util.ArrayList;
import java.util.List;

import org.jacop.core.Store;
import org.jacop.floats.constraints.LinearFloat;
import org.jacop.floats.constraints.PeqC;
import org.jacop.floats.constraints.PmulQeqR;
import org.jacop.floats.core.FloatDomain;
import org.jacop.floats.core.FloatVar;

import learntest.core.machinelearning.calculator.MultiNotDividerBasedCategoryCalculator;
import learntest.core.machinelearning.calculator.OrCategoryCalculator;
import learntest.core.machinelearning.sampling.PathRandom;
import libsvm.core.CategoryCalculator;
import libsvm.core.Divider;
import libsvm.extension.MultiDividerBasedCategoryCalculator;
import sav.common.core.formula.Eq;
import sav.common.core.formula.LIAAtom;
import sav.common.core.formula.LIATerm;
import sav.common.core.formula.Var;
import sav.strategies.dto.execute.value.ExecVar;

public class StoreBuilder {	

	public static int max = 2000;
	
	public static List<Store> build(Divider object, List<ExecVar> vars, OrCategoryCalculator calculator, 
			List<Divider> dividers, boolean random) {
		List<Store> stores = build(vars, calculator, dividers, random);
		if (!stores.isEmpty() && object != null) {
			for (Store store : stores) {
				addTarget(store, object);
			}
		}
		return stores;
	}

	private static void addTarget(Store store, Divider object) {
		double[] thetas = object.getThetas();
		//int[] weights = new int[thetas.length];
		double[] weights = new double[store.size()];
		for (int i = 0; i < thetas.length; i++) {
			//weights[i] = (int) thetas[i];
			//weights[i] = (int) Math.rint(thetas[i]);
			weights[i] = thetas[i];
		}
		FloatVar[] intVars = new FloatVar[store.size()];
		for (int i = 0; i < intVars.length; i++) {
			intVars[i] = (FloatVar)store.vars[i];
		}
		/*new Linear(coefficients, variables);
		new LinearFloat(store, list, weights, rel, sum)*/
		store.impose(new LinearFloat(store, intVars, weights, "==", (int) object.getTheta0()));
	}
	
	public static void addOpposite(Store store, Divider divider) {
		double[] thetas = divider.getThetas();
		//int[] weights = new int[thetas.length];
		double[] weights = new double[store.size()];
		for (int i = 0; i < thetas.length; i++) {
			//weights[i] = (int) thetas[i];
			//weights[i] = (int) Math.rint(thetas[i]);
			weights[i] = thetas[i];
		}
		FloatVar[] intVars = new FloatVar[store.size()];
		for (int i = 0; i < intVars.length; i++) {
			intVars[i] = (FloatVar)store.vars[i];
		}
		store.impose(new LinearFloat(store, intVars, weights, "<", (int) divider.getTheta0()));
	}
	
	public static void addConstraints(Store store, List<Eq<Number>> constraints) {
		for (Eq<Number> eq : constraints) {
			String name = eq.getVar().getLabel();
			FloatVar var = (FloatVar) store.findVariable(name);
			Number c = eq.getValue();
			store.impose(new PeqC(var, c.doubleValue()));
		}
	}

	// vars: original vars like x and y, not include x * x, y * y and x * y;
	public static List<Store> build(List<ExecVar> vars, OrCategoryCalculator orCalculator, 
			List<Divider> current, boolean random) {
		List<Store> res = new ArrayList<Store>();
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
	
	private static Store build(List<ExecVar> vars, List<Divider> dividers, 
			List<Divider> notDividers, boolean random) {
		Store store = build(vars);
		if (random) {
			PathRandom randomPath = PathRandom.randomPath(dividers, notDividers);
			dividers = randomPath.getDividers();
			notDividers = randomPath.getNotDividers();
		}
		if (dividers != null) {
			for (Divider divider : dividers) {
				double[] thetas = divider.getThetas();
				//int[] weights = new int[thetas.length];
				double[] weights = new double[store.size()];
				for (int i = 0; i < thetas.length; i++) {
					//weights[i] = (int) thetas[i];
					//weights[i] = (int) Math.rint(thetas[i]);
					weights[i] = thetas[i];
				}
				FloatVar[] intVars = new FloatVar[store.size()];
				for (int i = 0; i < intVars.length; i++) {
					intVars[i] = (FloatVar)store.vars[i];
				}
				store.impose(new LinearFloat(store, intVars, weights, ">=", divider.getTheta0()));
			}
		}		
		if (notDividers != null) {
			for (Divider divider : notDividers) {
				double[] thetas = divider.getThetas();
				//int[] weights = new int[thetas.length];
				double[] weights = new double[store.size()];
				for (int i = 0; i < thetas.length; i++) {
					//weights[i] = (int) thetas[i];
					//weights[i] = (int) Math.rint(thetas[i]);
					weights[i] = thetas[i];
				}
				FloatVar[] intVars = new FloatVar[store.size()];
				for (int i = 0; i < intVars.length; i++) {
					intVars[i] = (FloatVar)store.vars[i];
				}
				store.impose(new LinearFloat(store, intVars, weights, "<", (int) divider.getTheta0()));
			}
		}		
		return store;
	}
	
	private static Store build(List<ExecVar> vars) {
		FloatDomain.setPrecision(1.0e-14);
		FloatDomain.intervalPrint(false);
		Store store = new Store();
		int size = vars.size();
		FloatVar[] intVars = new FloatVar[size + size * (size + 1) / 2];
		int idx = 0;
		for (ExecVar var : vars) {
			intVars[idx] = buildVar(store, var);
			idx++;
		}
		for (int i = 0; i < size; i++) {
			String label = vars.get(i).getLabel();
			for (int j = i; j < size; j++) {
				double max = intVars[i].dom().max() * intVars[j].dom().max();
				intVars[idx] = new FloatVar(store, label + " * " + vars.get(j).getLabel(), 
						-max, max);
				store.impose(new PmulQeqR(intVars[i], intVars[j], intVars[idx]));
				idx++;
			}
		}
		/*IntVar x = (IntVar) store.findVariable("x");
		IntVar y = (IntVar) store.findVariable("y");
		IntVar z = (IntVar) store.findVariable("z");
		store.impose(new XgteqY(x, y));
		store.impose(new XgteqY(y, z));*/
		return store;
	}
	
	private static FloatVar buildVar(Store store, ExecVar var) {
		switch (var.getType()) {
			case BOOLEAN:
				return new FloatVar(store, var.getLabel(), 0, 1);
			case BYTE:
				return new FloatVar(store, var.getLabel(), Byte.MIN_VALUE, Byte.MAX_VALUE);
			case CHAR:
				return new FloatVar(store, var.getLabel(), -100, 100);
			case SHORT:
				return new FloatVar(store, var.getLabel(), Short.MIN_VALUE, Short.MAX_VALUE);
			/*case DOUBLE:
				return new IntVar(store, var.getLabel(), -2000, 2000);
			case FLOAT:
				return new IntVar(store, var.getLabel(), -1000, 1000);
			case LONG:
				return new IntVar(store, var.getLabel(), -1000, 1000);*/
			default:
				return new FloatVar(store, var.getLabel(), -max, max);
		}
	}
	
	public static Store build(List<ExecVar> vars, List<LIAAtom> atoms, List<LIAAtom> nots, int i) {
		Store store = build(vars);
		for (LIAAtom atom : atoms) {
			List<LIATerm> exps = atom.getMVFOExpr();
			FloatVar[] intVars = new FloatVar[exps.size()];
			double[] weights = new double[exps.size()];
			int idx = 0;
			for (LIATerm term : exps) {
				double coefficient = term.getCoefficient();
				//weights[idx] = (int) coefficient;
				//weights[idx] = (int) Math.rint(coefficient);
				weights[idx] = coefficient;
				Var variable = term.getVariable();
				intVars[idx] = (FloatVar) store.findVariable(variable.getLabel());
				idx ++;
			}
			double constant = atom.getConstant();
			store.impose(new LinearFloat(store, intVars, weights, ">=", constant));
		}
		for (LIAAtom atom : nots) {
			List<LIATerm> exps = atom.getMVFOExpr();
			FloatVar[] intVars = new FloatVar[exps.size()];
			double[] weights = new double[exps.size()];
			int idx = 0;
			for (LIATerm term : exps) {
				double coefficient = term.getCoefficient();
				//weights[idx] = (int) coefficient;
				//weights[idx] = (int) Math.rint(coefficient);
				weights[idx] = coefficient;
				Var variable = term.getVariable();
				intVars[idx] = (FloatVar) store.findVariable(variable.getLabel());
				idx ++;
			}
			double constant = atom.getConstant();
			store.impose(new LinearFloat(store, intVars, weights, "<", constant));
		}
		return store;
	}
}
