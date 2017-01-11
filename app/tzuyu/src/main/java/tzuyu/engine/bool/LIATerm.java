package tzuyu.engine.bool;

import tzuyu.engine.bool.formula.LIAAtom;
import tzuyu.engine.iface.ExpressionVisitor;

/**
 * The term in the linear integer arithmetic {@link LIAAtom} formula contains an
 * integer coefficient and a field variable which refers to a field defined in
 * the target class or the classes of its fields. We use ILATerm to distinguish
 * it from the {@link DNFTerm} used in Disjunctive Normal Form.
 * 
 * @author Spencer Xiao
 * 
 */
public class LIATerm {
  private FieldVar variable;
  private double coefficient;

  public LIATerm(FieldVar var, double coeff) {
    this.variable = var;
    this.coefficient = coeff;
  }

  public FieldVar getVariable() {
    return this.variable;
  }

  public double getCoefficient() {
    return this.coefficient;
  }

  public String toCodeString() {

    if (coefficient == 1) {
      return variable.toString();
    }

    return "" + coefficient + "*" + variable.toString();
  }

  @Override
  public String toString() {
    return toCodeString();
  }

	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}
}
