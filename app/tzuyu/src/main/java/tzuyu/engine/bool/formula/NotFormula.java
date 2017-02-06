package tzuyu.engine.bool.formula;

import java.util.List;

import tzuyu.engine.bool.Var;
import tzuyu.engine.iface.ExpressionVisitor;
import tzuyu.engine.model.Formula;
import tzuyu.engine.model.Prestate;

/**
 * donot init this formula, call FormulaNegation instead.
 *
 */
public final class NotFormula implements Formula {
	private Formula operand;

	@Deprecated
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

	public List<Atom> getAtomics() {
		return this.operand.getAtomics();
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

	@Override
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}
	
	public Formula getChild() {
		return operand;
	}
}
