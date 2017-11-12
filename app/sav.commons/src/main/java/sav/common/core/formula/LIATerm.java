package sav.common.core.formula;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sav.common.core.formula.utils.ExpressionVisitor;

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
	private Var variable;
	private double coefficient;

	public LIATerm(Var var, double coeff) {
		this.variable = var;
		this.coefficient = coeff;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Var> T getVariable() {
		return (T) this.variable;
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
	
	public static <T extends Var> LIATerm of(T var, double coeff) {
		return new LIATerm(var, coeff);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof LIATerm)) {
			return false;
		}
		
		LIATerm term = (LIATerm) obj;
		return term.toString().equals(this.toString());
	}
	
	public static boolean equals(List<LIATerm> l1, List<LIATerm> l2){
		if (l1.size() != l2.size()) {
			return false;
		}
		List<LIATerm> l2Temp = new ArrayList<LIATerm>(l2.size());
		l2Temp.addAll(l2);
		for (Iterator<LIATerm> iterator = l1.iterator(); iterator.hasNext();) {
			LIATerm liaTerm1 = iterator.next();
			boolean found = false;
			int i = 0;
			for (; i< l2Temp.size(); i++) {
				LIATerm liaTerm2 = l2Temp.get(i);
				if (liaTerm1.equals(liaTerm2)) {
					found = true;
					break;
				}
			}
			if (found) {
				l2Temp.remove(i);
			}else {
				return false;
			}
		}
		return true;
	}
}
