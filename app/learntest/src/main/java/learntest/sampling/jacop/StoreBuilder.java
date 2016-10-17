package learntest.sampling.jacop;

import java.util.ArrayList;
import java.util.List;

import org.jacop.constraints.LinearInt;
import org.jacop.constraints.XeqC;
import org.jacop.constraints.XgteqY;
import org.jacop.constraints.XmulYeqZ;
import org.jacop.core.IntVar;
import org.jacop.core.Store;

import learntest.calculator.MultiNotDividerBasedCategoryCalculator;
import learntest.calculator.OrCategoryCalculator;
import learntest.sampling.PathRandom;
import libsvm.core.CategoryCalculator;
import libsvm.core.Divider;
import libsvm.extension.MultiDividerBasedCategoryCalculator;
import sav.common.core.formula.Eq;
import sav.common.core.formula.LIAAtom;
import sav.common.core.formula.LIATerm;
import sav.common.core.formula.Var;
import sav.strategies.dto.execute.value.ExecVar;

public class StoreBuilder {
	
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
		int[] weights = new int[thetas.length];
		for (int i = 0; i < thetas.length; i++) {
			//weights[i] = (int) thetas[i];
			weights[i] = (int) Math.rint(thetas[i]);
		}
		IntVar[] intVars = new IntVar[store.size()];
		for (int i = 0; i < intVars.length; i++) {
			intVars[i] = (IntVar)store.vars[i];
		}
		store.impose(new LinearInt(store, intVars, weights, "==", (int) object.getTheta0()));
	}
	
	public static void addOpposite(Store store, Divider divider) {
		double[] thetas = divider.getThetas();
		int[] weights = new int[thetas.length];
		for (int i = 0; i < thetas.length; i++) {
			//weights[i] = (int) thetas[i];
			weights[i] = (int) Math.rint(thetas[i]);
		}
		IntVar[] intVars = new IntVar[store.size()];
		for (int i = 0; i < intVars.length; i++) {
			intVars[i] = (IntVar)store.vars[i];
		}
		store.impose(new LinearInt(store, intVars, weights, "<", (int) divider.getTheta0()));
	}
	
	public static void addConstraints(Store store, List<Eq<Number>> constraints) {
		for (Eq<Number> eq : constraints) {
			String name = eq.getVar().getLabel();
			IntVar var = (IntVar) store.findVariable(name);
			Number c = eq.getValue();
			store.impose(new XeqC(var, c.intValue()));
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
		for (List<CategoryCalculator> list : calculators) {
			List<Divider> dividers = current == null ? new ArrayList<Divider>() 
					: new ArrayList<Divider>(current);
			List<List<Divider>> notDividers = new ArrayList<List<Divider>>();
			for (CategoryCalculator calculator : list) {
				if (calculator instanceof MultiDividerBasedCategoryCalculator) {
					dividers.addAll(
							((MultiDividerBasedCategoryCalculator) calculator).getDividers());
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
			PathRandom.randomPath(dividers, notDividers);
			dividers = PathRandom.dividers;
			notDividers = PathRandom.notDividers;
		}
		if (dividers != null) {
			for (Divider divider : dividers) {
				double[] thetas = divider.getThetas();
				int[] weights = new int[thetas.length];
				for (int i = 0; i < thetas.length; i++) {
					//weights[i] = (int) thetas[i];
					weights[i] = (int) Math.rint(thetas[i]);
				}
				IntVar[] intVars = new IntVar[store.size()];
				for (int i = 0; i < intVars.length; i++) {
					intVars[i] = (IntVar)store.vars[i];
				}
				store.impose(new LinearInt(store, intVars, weights, ">=", 
						(int) divider.getTheta0()));
			}
		}		
		if (notDividers != null) {
			for (Divider divider : notDividers) {
				double[] thetas = divider.getThetas();
				int[] weights = new int[thetas.length];
				for (int i = 0; i < thetas.length; i++) {
					//weights[i] = (int) thetas[i];
					weights[i] = (int) Math.rint(thetas[i]);
				}
				IntVar[] intVars = new IntVar[store.size()];
				for (int i = 0; i < intVars.length; i++) {
					intVars[i] = (IntVar)store.vars[i];
				}
				store.impose(new LinearInt(store, intVars, weights, "<", (int) divider.getTheta0()));
			}
		}		
		return store;
	}
	
	private static Store build(List<ExecVar> vars) {
		Store store = new Store();
		int size = vars.size();
		IntVar[] intVars = new IntVar[size + size * (size + 1) / 2];
		int idx = 0;
		for (ExecVar var : vars) {
			intVars[idx ++] = buildVar(store, var);
		}
		for (int i = 0; i < size; i++) {
			String label = vars.get(i).getLabel();
			for (int j = i; j < size; j++) {
				int max = intVars[i].dom().max() * intVars[j].dom().max();
				intVars[idx] = new IntVar(store, label + " * " + vars.get(j).getLabel(), 
						-max, max);
				store.impose(new XmulYeqZ(intVars[i], intVars[j], intVars[idx ++]));
			}
		}
		/*IntVar x = (IntVar) store.findVariable("x");
		IntVar y = (IntVar) store.findVariable("y");
		IntVar z = (IntVar) store.findVariable("z");
		store.impose(new XgteqY(x, y));
		store.impose(new XgteqY(y, z));*/
		return store;
	}
	
	private static IntVar buildVar(Store store, ExecVar var) {
		switch (var.getType()) {
			case BOOLEAN:
				return new IntVar(store, var.getLabel(), 0, 1);
			case BYTE:
				return new IntVar(store, var.getLabel(), -100, 100);
			case CHAR:
				return new IntVar(store, var.getLabel(), -100, 100);
			case DOUBLE:
				return new IntVar(store, var.getLabel(), -2000, 2000);
			case FLOAT:
				return new IntVar(store, var.getLabel(), -1000, 1000);
			case LONG:
				return new IntVar(store, var.getLabel(), -1000, 1000);
			case SHORT:
				return new IntVar(store, var.getLabel(), -100, 100);
			default:
				return new IntVar(store, var.getLabel(), -200, 200);
		}
	}
	
	public static Store build(List<ExecVar> vars, List<LIAAtom> atoms, List<LIAAtom> nots, int i) {
		Store store = build(vars);
		for (LIAAtom atom : atoms) {
			List<LIATerm> exps = atom.getMVFOExpr();
			IntVar[] intVars = new IntVar[exps.size()];
			int[] weights = new int[exps.size()];
			int idx = 0;
			for (LIATerm term : exps) {
				double coefficient = term.getCoefficient();
				//weights[idx] = (int) coefficient;
				weights[idx] = (int) Math.rint(coefficient);
				Var variable = term.getVariable();
				intVars[idx] = (IntVar) store.findVariable(variable.getLabel());
				idx ++;
			}
			double constant = atom.getConstant();
			store.impose(new LinearInt(store, intVars, weights, ">=", (int) constant));
		}
		for (LIAAtom atom : nots) {
			List<LIATerm> exps = atom.getMVFOExpr();
			IntVar[] intVars = new IntVar[exps.size()];
			int[] weights = new int[exps.size()];
			int idx = 0;
			for (LIATerm term : exps) {
				double coefficient = term.getCoefficient();
				//weights[idx] = (int) coefficient;
				weights[idx] = (int) Math.rint(coefficient);
				Var variable = term.getVariable();
				intVars[idx] = (IntVar) store.findVariable(variable.getLabel());
				idx ++;
			}
			double constant = atom.getConstant();
			store.impose(new LinearInt(store, intVars, weights, "<", (int) constant));
		}
		return store;
	}
}
