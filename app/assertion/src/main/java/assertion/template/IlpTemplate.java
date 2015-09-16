package assertion.template;

import java.util.ArrayList;
import java.util.List;

import icsetlv.common.dto.ExecValue;
import icsetlv.common.dto.ExecVar;
import sav.common.core.formula.Eq;
import sav.common.core.formula.Operator;
import sav.common.core.formula.Var;

public class IlpTemplate extends Template {

	private List<Double> coefs;
	
	private Operator op;
	
	private Double d;
	
	public IlpTemplate(List<Double> coefs, List<List<ExecValue>> passExecValuesList,
			List<List<ExecValue>> failExecValuesList, Operator op, Double d) {
		super(passExecValuesList, failExecValuesList);
		this.coefs = coefs;
		this.op = op;
		this.d = d;
	}
	
	@Override
	public boolean check() {
		List<Double> passValues = new ArrayList<Double>();
		List<Double> failValues = new ArrayList<Double>();
		
		for (List<ExecValue> execValues : passExecValuesList) {
			double sum = 0.0;
			
			for (int i = 0; i < coefs.size(); i++) {
				sum += coefs.get(i) * execValues.get(i).getDoubleVal();
			}
			
			passValues.add(sum);
		}
		
		for (List<ExecValue> execValues : failExecValuesList) {
			double sum = 0.0;
			
			for (int i = 0; i < coefs.size(); i++) {
				sum += coefs.get(i) * execValues.get(i).getDoubleVal();
			}
			
			failValues.add(sum);
		}
		
		for (Double passValue : passValues) {
			switch(op) {
			case GE:
				if (passValue < d) return false;
				break;
			case LE:
				if (passValue > d) return false;
				break;
			case GT:
				if (passValue <= d) return false;
				break;
			case LT:
				if (passValue >= d) return false;
				break;
			case EQ:
				if (passValue != d) return false;
				break;
			case NE:
				if (passValue == d) return false;
				break;
			default:
				break;
			}
		}
		
		for (Double failValue : failValues) {
			switch(op) {
			case GE:
				if (failValue >= d) return false;
				break;
			case LE:
				if (failValue <= d) return false;
				break;
			case GT:
				if (failValue > d) return false;
				break;
			case LT:
				if (failValue < d) return false;
				break;
			case EQ:
				if (failValue == d) return false;
				break;
			case NE:
				if (failValue != d) return false;
				break;
			default:
				break;
			}
		}
		
		return true;
	}
	
	/*
	@Override
	public Template clone() {
		return new IlpTemplate(coefs, passExecValuesList, failExecValuesList, op, d);
	}
	*/
	
	@Override
	public List<Eq<?>> solve() {
		List<Eq<?>> eql = new ArrayList<Eq<?>>();
		List<ExecValue> execValues = passExecValuesList.get(0);
		
		boolean firstAssign = false;
		
		for (int i = 0; i < coefs.size(); i++) {
			ExecValue ev = execValues.get(i);
			Var v = new ExecVar(ev.getVarId(), ev.getType());
			if (coefs.get(i) == 0.0) {
				Eq<Number> eq = new Eq<Number>(v, 1.0);
				eql.add(eq);
			} else {
				if (!firstAssign) {
					Eq<Number> eq = new Eq<Number>(v, d / coefs.get(i));
					eql.add(eq);
					firstAssign = true;
				} else {
					Eq<Number> eq = new Eq<Number>(v, 0.0);
					eql.add(eq);
				}
			}

		}
		
		return eql;
	}
	
	@Override
	public String toString() {
		String s = "";
		
		List<ExecValue> execValues = passExecValuesList.get(0);
		
		for (int i = 0; i < coefs.size(); i++) {
			if (coefs.get(i) != 0.0) {
				if (s.length() != 0) s += " + ";
				if (coefs.get(i) != 1.0)
					s += coefs.get(i) + "*" + execValues.get(i).getVarId();
				else
					s += execValues.get(i).getVarId();
			}
		}
		
		s += " " + op + " " + d;
		
		return s;
	}
	
}
