package invariant.templates.onefeature;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.formula.Eq;
import sav.common.core.formula.Var;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;

// Template x >= 0

public class OneNumGt0Template extends OneFeatureTemplate {
	
	public OneNumGt0Template(List<List<ExecValue>> passValues, List<List<ExecValue>> failValues) {
		super(passValues, failValues);
		offset = 0.001;
	}

	@Override
	public boolean checkPassValue(List<ExecValue> evl) {
		// list of pass and fail exec value only has one feature
		// all pass values must be the same
		if (evl.get(0).getDoubleVal() == null) return false;
		
		double v = evl.get(0).getDoubleVal();
		return v > 0;
	}
	
	@Override
	public boolean checkFailValue(List<ExecValue> evl) {
		// list of pass and fail exec value only has one feature
		// all fail value must be different with the pass value
		if (evl.get(0).getDoubleVal() == null) return false;
		
		double v = evl.get(0).getDoubleVal();
		return v <= 0;
	}
	
	@Override
	public List<List<Eq<?>>> sampling() {
		List<List<Eq<?>>> samples = new ArrayList<List<Eq<?>>>();
		
		ExecValue ev = passValues.get(0).get(0);
		
		String id = ev.getVarId();
		ExecVarType t = ev.getType();
		
		Var v = new ExecVar(id, t);
		
		if (t == ExecVarType.INTEGER || t == ExecVarType.LONG ||
				t == ExecVarType.BYTE || t == ExecVarType.SHORT) {
			samples.add(sampling(v, t, 0.0));
			samples.add(sampling(v, t, 1.0));
			samples.add(sampling(v, t, -1.0));
		} else if (t == ExecVarType.FLOAT || t == ExecVarType.DOUBLE) {
			samples.add(sampling(v, t, 0.0));
			samples.add(sampling(v, t, offset));
			samples.add(sampling(v, t, -offset));
		}
		
		return samples;
	}
	
	@Override
	public String toString() {
		return passValues.get(0).get(0).getVarId() + " > 0.0";
	}
	
}
