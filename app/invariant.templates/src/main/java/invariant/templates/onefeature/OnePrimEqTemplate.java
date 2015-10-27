package invariant.templates.onefeature;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.formula.Eq;
import sav.common.core.formula.Var;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;

// Template x = a

public class OnePrimEqTemplate extends OneFeatureTemplate {
	
	private double a;
	
	public OnePrimEqTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}

	@Override
	public boolean checkPassValue(List<ExecValue> evl) {
		// list of pass and fail exec value only has one feature
		// all pass values must be the same
		double v = evl.get(0).getDoubleVal();
		return v == a;
	}
	
	@Override
	public boolean checkFailValue(List<ExecValue> evl) {
		// list of pass and fail exec value only has one feature
		// all fail value must be different with the pass value
		double v = evl.get(0).getDoubleVal();
		return v != a;
	}
	
	@Override
	public boolean check() {
		a = passExecValuesList.get(0).get(0).getDoubleVal();
		return check(passExecValuesList, failExecValuesList);
	}

	@Override
	public List<List<Eq<?>>> sampling() {
		List<List<Eq<?>>> samples = new ArrayList<List<Eq<?>>>();
		
		ExecValue ev = passExecValuesList.get(0).get(0);
		Var v = new ExecVar(ev.getVarId(), ev.getType());
		
		List<Eq<?>> sample1 = new ArrayList<Eq<?>>();
		sample1.add(new Eq<Number>(v, (int) (a - 1.0)));
		
		List<Eq<?>> sample2 = new ArrayList<Eq<?>>();
		sample2.add(new Eq<Number>(v, (int) (a + 1.0)));
		
		samples.add(sample1);
		samples.add(sample2);
		
		return samples;
	}
	
	@Override
	public String toString() {
		return passExecValuesList.get(0).get(0).getVarId() + " = " + a;
	}
	
}
