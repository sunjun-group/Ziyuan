package invariant.templates.twofeatures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sav.common.core.formula.Eq;
import sav.common.core.formula.Var;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;

public class TwoNumGteTemplate extends TwoFeaturesTemplate {

	public TwoNumGteTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}
	
	@Override
	public boolean checkPassValue(List<ExecValue> evl) {
		// list of pass and fail exec value only has two features
		// first feature must be greater than or equals to second feature
		if (evl.get(0).getDoubleVal() == null ||
				evl.get(1).getDoubleVal() == null)
			return false;
		
		double v1 = evl.get(0).getDoubleVal();
		double v2 = evl.get(1).getDoubleVal();
		return v1 >= v2;
	}
	
	@Override
	public boolean checkFailValue(List<ExecValue> evl) {
		// list of pass and fail exec value only has two features
		// first feature must be less than second feature
		if (evl.get(0).getDoubleVal() == null ||
				evl.get(1).getDoubleVal() == null)
			return false;
		
		double v1 = evl.get(0).getDoubleVal();
		double v2 = evl.get(1).getDoubleVal();
		return v1 < Math.abs(v2);
	}
	
	@Override
	public List<List<Eq<?>>> sampling() {
		List<List<Eq<?>>> samples = new ArrayList<List<Eq<?>>>();
		
		ExecValue ev1 = passValues.get(0).get(0);
		ExecValue ev2 = passValues.get(0).get(1);
		
		String id1 = ev1.getVarId();
		String id2 = ev2.getVarId();
		
		ExecVarType t1 = ev1.getType();
		ExecVarType t2 = ev2.getType();
		
		Var v1 = new ExecVar(id1, t1);
		Var v2 = new ExecVar(id2, t2);
		
		List<Var> vs = Arrays.asList(v1, v2);
		List<ExecVarType> ts = Arrays.asList(t1, t2);
		
		samples.add(sampling(vs, ts, Arrays.asList(1.0, 1.0)));
		samples.add(sampling(vs, ts, Arrays.asList(1.0, -1.0)));
		samples.add(sampling(vs, ts, Arrays.asList(-1.0, 1.0)));
		samples.add(sampling(vs, ts, Arrays.asList(-1.0, -1.0)));
		
		return samples;
	}
	
	@Override
	public String toString() {
		String id1 = passValues.get(0).get(0).getVarId();
		String id2 = passValues.get(0).get(1).getVarId();
		
		return id1 + " >= " + id2;
	}

}
