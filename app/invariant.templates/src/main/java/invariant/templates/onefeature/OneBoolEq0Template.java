package invariant.templates.onefeature;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.formula.Eq;
import sav.common.core.formula.Var;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;

public class OneBoolEq0Template extends OneFeatureTemplate {
	
	private double a;
	
	public OneBoolEq0Template(List<List<ExecValue>> passValues, List<List<ExecValue>> failValues) {
		super(passValues, failValues);
	}

	@Override
	public boolean checkPassValue(List<ExecValue> evl) {
		// list of pass and fail exec value only has one feature
		// all pass values must be the same
		if (evl.get(0).getDoubleVal() == null) return false;
		
		double v = evl.get(0).getDoubleVal();
		return v == a;
	}
	
	@Override
	public boolean checkFailValue(List<ExecValue> evl) {
		// list of pass and fail exec value only has one feature
		// all fail value must be different with the pass value
		if (evl.get(0).getDoubleVal() == null) return false;
		
		double v = evl.get(0).getDoubleVal();
		return v != a;
	}
	
	@Override
	public boolean check() {
		a = 0; // passValues.get(0).get(0).getDoubleVal();
		return check(passValues, failValues);
	}
	
	@Override
	public List<List<Eq<?>>> sampling() {
		List<List<Eq<?>>> samples = new ArrayList<List<Eq<?>>>();
		
		ExecValue ev = passValues.get(0).get(0);
		Var v = new ExecVar(ev.getVarId(), ev.getType());
		
		List<Eq<?>> sample1 = new ArrayList<Eq<?>>();
		sample1.add(new Eq<Boolean>(v, true));
		
		List<Eq<?>> sample2 = new ArrayList<Eq<?>>();
		sample2.add(new Eq<Boolean>(v, false));
		
		samples.add(sample1);
		samples.add(sample2);
		
		return samples;
	}
	
	@Override
	public String toString() {
		String s = a == 1 ? "true" : "false";
		return passValues.get(0).get(0).getVarId() + " = " + s;
	}

}
