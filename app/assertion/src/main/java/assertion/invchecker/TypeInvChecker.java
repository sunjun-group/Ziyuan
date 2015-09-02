package assertion.invchecker;

import icsetlv.common.dto.ExecValue;
import icsetlv.common.dto.ExecVar;
import icsetlv.common.dto.ExecVarType;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.formula.Eq;
import sav.common.core.formula.Formula;
import sav.common.core.formula.LIAAtom;
import sav.common.core.formula.LIATerm;
import sav.common.core.formula.Operator;

public class TypeInvChecker {

	// check if all passValues satisfy condition and no failValues satisfy condition
	public boolean checkFormula(List<Double> passValues, List<Double> failValues,
			final Operator op, final double a) {
		for (Double d : passValues) {
			switch(op) {
			case GE:
				if (d < a) return false;
				break;
			case GT:
				if (d <= a) return false;
				break;
			case EQ:
				if (d != a) return false;
				break;
			default:
				break;
			}
		}
		
		for (Double d : failValues) {
			switch(op) {
			case GE:
				if (d >= a) return false;
				break;
			case GT:
				if (d > a) return false;
				break;
			case EQ:
				if (d == a) return false;
				break;
			default:
				break;
			}
		}
		
		return true;
	}
	
	// create formula x [op] [value]
	public <T> Formula createFormula(ExecValue ev, T value) {
		String varId = ev.getVarId();
		ExecVar var = new ExecVar(varId, ExecVarType.BOOLEAN);
		
		Eq<T> formula = new Eq<T>(var, value);
		return formula;
	}
	
	// create formula
	// coef : [a1, a2, ..., an]
	// evl  : [x1, x2, ..., xn]
	// output : a1 * x1 + a2 * x2 + ... + an * xn [op] [value]
	public Formula createFormula(List<Integer> coef, List<ExecValue> evl, Operator op, Double value) {
		List<LIATerm> terms = new ArrayList<LIATerm>();
		
		for (int i = 0; i < coef.size(); i++) {
			String varId = evl.get(i).getVarId();
			ExecVar var = new ExecVar(varId, ExecVarType.PRIMITIVE);
			LIATerm term = new LIATerm(var, coef.get(i));
			terms.add(term);
		}
		
		LIAAtom formula = new LIAAtom(terms, op, value);
		
		return formula;
	}
	
	public List<Formula> check(List<List<ExecValue>> passExecValues, List<List<ExecValue>> failExecValues) {
		return new ArrayList<Formula>();
	}
	
}
