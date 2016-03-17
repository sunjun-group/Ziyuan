package invariant.templates.onefeature;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.formula.Eq;
import sav.common.core.formula.Var;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;

// Template x >= 0

public class OneNumGt0Template extends OneFeatureTemplate {
	
	public OneNumGt0Template(List<List<ExecValue>> passValues, List<List<ExecValue>> failValues) {
		super(passValues, failValues);
	}

	@Override
	public boolean checkPassValue(List<ExecValue> evl) {
		// list of pass and fail exec value only has one feature
		// all pass values must be the same
		double v = evl.get(0).getDoubleVal();
		return v > 0;
	}
	
	@Override
	public boolean checkFailValue(List<ExecValue> evl) {
		// list of pass and fail exec value only has one feature
		// all fail value must be different with the pass value
		double v = evl.get(0).getDoubleVal();
		return v <= 0;
	}
	
	@Override
	public List<List<Eq<?>>> sampling() {
		List<List<Eq<?>>> samples = new ArrayList<List<Eq<?>>>();
		
		ExecValue ev = passValues.get(0).get(0);
		Var v = new ExecVar(ev.getVarId(), ev.getType());
		
		List<Eq<?>> sample1 = new ArrayList<Eq<?>>();
		sample1.add(new Eq<Number>(v, 0));
		
		List<Eq<?>> sample2 = new ArrayList<Eq<?>>();
		sample2.add(new Eq<Number>(v, 1));
		
		List<Eq<?>> sample3 = new ArrayList<Eq<?>>();
		sample3.add(new Eq<Number>(v, -1));
		
		samples.add(sample1);
		samples.add(sample2);
		samples.add(sample3);
		
		return samples;
	}
	
	@Override
	public String toString() {
		return passValues.get(0).get(0).getVarId() + " > 0.0";
	}
	
}
