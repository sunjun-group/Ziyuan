package assertion.template;

import java.util.ArrayList;
import java.util.List;

import icsetlv.common.dto.ExecValue;
import icsetlv.common.dto.ExecVar;
import sav.common.core.formula.Eq;
import sav.common.core.formula.Var;

public class ModConstTemplate extends Template {
	
	private Integer d;
	
	public ModConstTemplate(List<List<ExecValue>> passExecValuesList,
			List<List<ExecValue>> failExecValuesList, Integer d) {
		super(passExecValuesList, failExecValuesList);
		this.d = d;
	}
	
	@Override
	public boolean check() {
		for (List<ExecValue> execValues : passExecValuesList) {
			int i1 = (int) execValues.get(0).getDoubleVal();
			int i2 = (int) execValues.get(1).getDoubleVal();
			
			if (i1 != i2 % d) {
				return false;
			}
		}
		
		for (List<ExecValue> execValues : failExecValuesList) {
			int i1 = (int) execValues.get(0).getDoubleVal();
			int i2 = (int) execValues.get(1).getDoubleVal();
			
			if (i1 == i2 % d) {
				return false;
			}
		}
		
		return false;
	}
	
	/*
	@Override
	public Template clone() {
		return new ModConstTemplate(passExecValuesList, failExecValuesList, d);
	}
	*/
	
	@Override
	public List<Eq<?>> solve() {
		List<Eq<?>> eql = new ArrayList<Eq<?>>();
		List<ExecValue> execValues = passExecValuesList.get(0);
		
		ExecValue ev0 = execValues.get(0);
		Var v0 = new ExecVar(ev0.getVarId(), ev0.getType());
		
		ExecValue ev1 = execValues.get(1);
		Var v1 = new ExecVar(ev0.getVarId(), ev0.getType());
		
		if (d == 1) {
			// add 0 1
			Eq<Number> eq0 = new Eq<Number>(v0, 0);
			Eq<Number> eq1 = new Eq<Number>(v1, 1);
			eql.add(eq0); eql.add(eq1);
		} else {
			// add 1 (d + 1)
			Eq<Number> eq0 = new Eq<Number>(v0, 1);
			Eq<Number> eq1 = new Eq<Number>(v1, d + 1);
			eql.add(eq0); eql.add(eq1);
		}
		
		return null;
	}
	
}
