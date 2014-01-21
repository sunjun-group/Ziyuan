package refiner.bool;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.bool.Atom;
import tzuyu.engine.bool.Var;
import tzuyu.engine.model.ObjectInfo;
import tzuyu.engine.model.Prestate;
import tzuyu.engine.model.TzuYuException;

/**
 * The relational expression in Linear Integer Arithmetic establish the relation
 * between a set of {@link LIATerm}s and an integer constant.
 * 
 * @author Spencer Xiao
 * 
 */
public class LIAAtom extends Atom {

	private double constant;
	/**
	 * Multiple Variables First Order expression
	 */
	private List<LIATerm> MVFOExpr;
	private Operator operator;

	public LIAAtom(List<LIATerm> terms, Operator op, double right) {
		MVFOExpr = terms;
		operator = op;
		constant = right;
	}

	@Override
	public String toString() {
		StringBuilder termsStr = new StringBuilder();
		int size = MVFOExpr.size();
		for (int index = 0; index < size; index++) {
			LIATerm term = MVFOExpr.get(index);
			termsStr.append(term.toCodeString());
			if (index != size - 1) {
				termsStr.append("+");
			}
		}

		return termsStr.toString() + " " + operator.toString() + " " + constant;
	}

	public boolean evaluate(Object[] objects) {
		double leftValue = 0;
		// Currently we only support integer values
		for (LIATerm term : MVFOExpr) {
			FieldVar var = term.getVariable();
			try {
				Object object = var.getValue(objects);
				double fieldValue = Double.valueOf(object.toString())
						.doubleValue();
				leftValue += fieldValue * term.getCoefficient();
			} catch (NumberFormatException e) {
				throw new TzuYuException(
						"Try to convert non-numerical value to "
								+ "numerical value");
			}
		}

		return operator.evaluate(leftValue, constant);
	}

	public List<Var> getReferencedVariables() {
		List<Var> result = new ArrayList<Var>(MVFOExpr.size());
		for (LIATerm term : MVFOExpr) {
			result.add(term.getVariable());
		}

		return result;
	}

	@Override
	public int hashCode() {
		return MVFOExpr.hashCode() * 31 + operator.hashCode() * 19
				+ (int) (constant * 1000);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof LIAAtom)) {
			return false;
		}

		LIAAtom obj = (LIAAtom) o;

		return MVFOExpr.equals(obj.MVFOExpr) && operator == obj.operator
				&& obj.constant == constant;
	}

	public boolean evaluate(Prestate state) {
		double leftValue = 0;
		// Currently we only support integer values
		for (LIATerm term : MVFOExpr) {
			FieldVar var = term.getVariable();
			ObjectInfo objectInfo = var.getObjectInfo(state);
			double fieldValue = objectInfo.getNumericValue();
			leftValue += fieldValue * term.getCoefficient();
		}

		return operator.evaluate(leftValue, constant);
	}

}
