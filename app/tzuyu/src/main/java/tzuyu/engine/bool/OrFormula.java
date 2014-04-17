package tzuyu.engine.bool;

import java.util.List;

import tzuyu.engine.model.Formula;
import tzuyu.engine.model.Prestate;


public final class OrFormula extends ConjunctionFormula implements Formula {
	
	public OrFormula(Formula left, Formula right) {
		super(left, right);
	}

	public boolean evaluate(Object[] objects) {
		return left.evaluate(objects) || right.evaluate(objects);
	}

	public Formula restrict(List<Atom> vars, List<Integer> vals) {
		Formula leftExpr = left.restrict(vars, vals);
		Formula rightExpr = right.restrict(vars, vals);
		if (leftExpr instanceof True || rightExpr instanceof True) {
			return Formula.TRUE;
		}

		if (leftExpr instanceof False) {
			return rightExpr;
		}

		if (rightExpr instanceof False) {
			return leftExpr;
		}

		return new OrFormula(leftExpr, rightExpr);
	}

	public Formula simplify() {
		Formula leftExpr = left.simplify();
		Formula rightExpr = right.simplify();
		if (leftExpr instanceof True || rightExpr instanceof True) {
			return Formula.TRUE;
		}

		if (leftExpr instanceof False) {
			return rightExpr;
		}

		if (rightExpr instanceof False) {
			return leftExpr;
		}

		return new OrFormula(leftExpr, rightExpr);
	}

	public boolean evaluate(Prestate state) {
		return left.evaluate(state) || right.evaluate(state);
	}
	
	@Override
	public String getOperation() {
		return " || ";
	}
}
