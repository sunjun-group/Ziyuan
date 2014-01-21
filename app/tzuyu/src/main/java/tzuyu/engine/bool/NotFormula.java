package tzuyu.engine.bool;

import java.util.List;

import tzuyu.engine.model.Formula;
import tzuyu.engine.model.Prestate;

public final class NotFormula implements Formula {
	private final Formula operand;

	public NotFormula(Formula operand) {
		this.operand = operand;
	}

	public List<Var> getReferencedVariables() {
		return operand.getReferencedVariables();
	}

	@Override
	public String toString() {
		return "!" + operand.toString();
	}

	public boolean evaluate(Object[] objects) {
		return !operand.evaluate(objects);
	}

	public Formula restrict(List<Atom> vars, List<Integer> vals) {
		Formula expr = this.operand.restrict(vars, vals);
		if (expr instanceof True) {
			return Formula.FALSE;
		}

		if (expr instanceof False) {
			return Formula.TRUE;
		}

		return new NotFormula(expr);
	}

	public List<Atom> getAtomics() {
		return this.operand.getAtomics();
	}

	public Formula simplify() {
		Formula expr = this.operand.simplify();
		if (expr instanceof True) {
			return Formula.FALSE;
		}

		if (expr instanceof False) {
			return Formula.TRUE;
		}

		return new NotFormula(expr);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof NotFormula)) {
			return false;
		}

		NotFormula obj = (NotFormula) o;

		return obj.operand.equals(operand);
	}

	@Override
	public int hashCode() {
		return operand.hashCode();
	}

	public boolean evaluate(Prestate state) {
		return !operand.evaluate(state);
	}
}
