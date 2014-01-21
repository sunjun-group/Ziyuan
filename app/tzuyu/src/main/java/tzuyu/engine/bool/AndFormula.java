package tzuyu.engine.bool;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.model.Formula;
import tzuyu.engine.model.Prestate;

public final class AndFormula implements Formula {

	private final Formula left;
	private final Formula right;

	public AndFormula(Formula left, Formula right) {
		this.left = left;
		this.right = right;
	}

	public List<Var> getReferencedVariables() {
		List<Var> leftVars = left.getReferencedVariables();
		List<Var> rightVars = right.getReferencedVariables();
		List<Var> result = new ArrayList<Var>(leftVars);
		result.removeAll(rightVars);
		result.addAll(rightVars);
		return result;
	}

	@Override
	public String toString() {
		return left.toString() + " && " + right.toString();
	}

	public boolean evaluate(Object[] objects) {
		return left.evaluate(objects) && right.evaluate(objects);
	}

	public Formula restrict(List<Atom> vars, List<Integer> vals) {
		Formula leftExpr = left.restrict(vars, vals);
		Formula rightExpr = right.restrict(vars, vals);
		if (leftExpr instanceof False || rightExpr instanceof False) {
			return Formula.FALSE;
		}

		if (leftExpr instanceof True) {
			return rightExpr;
		}

		if (rightExpr instanceof True) {
			return leftExpr;
		}

		return new AndFormula(leftExpr, rightExpr);
	}

	public List<Atom> getAtomics() {
		List<Atom> leftAtoms = left.getAtomics();
		List<Atom> rightAtoms = right.getAtomics();
		leftAtoms.removeAll(rightAtoms);
		leftAtoms.addAll(rightAtoms);
		return leftAtoms;
	}

	public Formula simplify() {
		Formula leftExpr = left.simplify();
		Formula rightExpr = right.simplify();
		if (leftExpr instanceof False || rightExpr instanceof False) {
			return Formula.FALSE;
		}

		if (leftExpr instanceof True) {
			return rightExpr;
		}

		if (rightExpr instanceof True) {
			return leftExpr;
		}

		return new AndFormula(leftExpr, rightExpr);
	}

	public boolean evaluate(Prestate state) {
		return left.evaluate(state) && right.evaluate(state);
	}
}
