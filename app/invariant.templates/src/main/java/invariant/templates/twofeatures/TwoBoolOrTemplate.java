package invariant.templates.twofeatures;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.formula.Eq;
import sav.common.core.formula.Var;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;

public class TwoBoolOrTemplate extends TwoFeaturesTemplate {
	
	private double a;

	public TwoBoolOrTemplate(List<List<ExecValue>> passValues, List<List<ExecValue>> failValues) {
		super(passValues, failValues);
	}
	
	@Override
	public boolean checkPassValue(List<ExecValue> evl) {
		// list of pass and fail exec value only has one feature
		// all pass values must be the same
		double v1 = evl.get(0).getDoubleVal();
		double v2 = evl.get(0).getDoubleVal();
		double v = (v1 == 1 || v2 == 1) ? 1 : 0;
		return v == a;
	}
	
	@Override
	public boolean checkFailValue(List<ExecValue> evl) {
		// list of pass and fail exec value only has one feature
		// all fail value must be different with the pass value
		double v1 = evl.get(0).getDoubleVal();
		double v2 = evl.get(0).getDoubleVal();
		double v = (v1 == 1 || v2 == 1) ? 1 : 0;
		return v != a;
	}
	
	@Override
	public boolean check() {
		double b1 = passValues.get(0).get(0).getDoubleVal();
		double b2 = passValues.get(0).get(1).getDoubleVal();
		a = (b1 == 1 || b2 == 1) ? 1 : 0;
		return check(passValues, failValues);
	}
	
	@Override
	public List<List<Eq<?>>> sampling() {
		List<List<Eq<?>>> samples = new ArrayList<List<Eq<?>>>();
		
		ExecValue ev1 = passValues.get(0).get(0);
		Var v1 = new ExecVar(ev1.getVarId(), ev1.getType());
		
		ExecValue ev2 = passValues.get(0).get(1);
		Var v2 = new ExecVar(ev2.getVarId(), ev2.getType());
		
		List<Eq<?>> sample1 = new ArrayList<Eq<?>>();
		sample1.add(new Eq<Boolean>(v1, true));
		sample1.add(new Eq<Boolean>(v2, true));
		
		List<Eq<?>> sample2 = new ArrayList<Eq<?>>();
		sample2.add(new Eq<Boolean>(v1, true));
		sample2.add(new Eq<Boolean>(v2, false));
		
		List<Eq<?>> sample3 = new ArrayList<Eq<?>>();
		sample3.add(new Eq<Boolean>(v1, false));
		sample3.add(new Eq<Boolean>(v2, true));
		
		List<Eq<?>> sample4 = new ArrayList<Eq<?>>();
		sample4.add(new Eq<Boolean>(v1, false));
		sample4.add(new Eq<Boolean>(v2, false));
		
		samples.add(sample1);
		samples.add(sample2);
		samples.add(sample3);
		samples.add(sample4);
		
		return samples;
	}
	
	@Override
	public String toString() {
		String s = a == 1 ? "true" : "false";
		return passValues.get(0).get(0).getVarId() + " || " +
			passValues.get(0).get(1).getVarId() + " = " + s;
	}
	
}
