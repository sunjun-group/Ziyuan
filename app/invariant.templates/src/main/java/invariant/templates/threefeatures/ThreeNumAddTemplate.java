package invariant.templates.threefeatures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sav.common.core.formula.Eq;
import sav.common.core.formula.Var;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;

public class ThreeNumAddTemplate extends ThreeFeaturesTemplate {

	public ThreeNumAddTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}
	
	@Override
	public boolean checkPassValue(List<ExecValue> evl) {
		// list of pass and fail exec value only has three features
		// first feature must be equals to addition between second and third feature
		if (evl.get(0).getDoubleVal() == null ||
				evl.get(1).getDoubleVal() == null ||
				evl.get(2).getDoubleVal() == null)
			return false;
		
		double v1 = evl.get(0).getDoubleVal();
		double v2 = evl.get(1).getDoubleVal();
		double v3 = evl.get(2).getDoubleVal();
		return v1 == v2 + v3;
	}
	
	@Override
	public boolean checkFailValue(List<ExecValue> evl) {
		// list of pass and fail exec value only has three features
		// first feature must not be equals to addition between second and third feature
		if (evl.get(0).getDoubleVal() == null ||
				evl.get(1).getDoubleVal() == null ||
				evl.get(2).getDoubleVal() == null)
			return false;
		
		double v1 = evl.get(0).getDoubleVal();
		double v2 = evl.get(1).getDoubleVal();
		double v3 = evl.get(2).getDoubleVal();
		return v1 != v2 + v3;
	}
	
	@Override
	public List<List<Eq<?>>> sampling() {
		List<List<Eq<?>>> samples = new ArrayList<List<Eq<?>>>();
		
		ExecValue ev1 = passValues.get(0).get(0);
		ExecValue ev2 = passValues.get(0).get(1);
		ExecValue ev3 = passValues.get(0).get(2);
		
		String id1 = ev1.getVarId();
		String id2 = ev2.getVarId();
		String id3 = ev3.getVarId();
		
		ExecVarType t1 = ev1.getType();
		ExecVarType t2 = ev2.getType();
		ExecVarType t3 = ev3.getType();
		
		Var v1 = new ExecVar(id1, t1);
		Var v2 = new ExecVar(id2, t2);
		Var v3 = new ExecVar(id3, t3);
		
		List<Var> vs = Arrays.asList(v1, v2, v3);
		List<ExecVarType> ts = Arrays.asList(t1, t2, t3);
		
		samples.add(sampling(vs, ts, Arrays.asList(1.0, 1.0, 2.0)));
		samples.add(sampling(vs, ts, Arrays.asList(1.0, -1.0, 1.0)));
		samples.add(sampling(vs, ts, Arrays.asList(-1.0, 1.0, 1.0)));
		samples.add(sampling(vs, ts, Arrays.asList(-1.0, -1.0, -1.0)));
		
		return samples;
	}
	
	@Override
	public String toString() {
		String id1 = passValues.get(0).get(0).getVarId();
		String id2 = passValues.get(0).get(1).getVarId();
		String id3 = passValues.get(0).get(2).getVarId();
		
		return id1 + " = " + id2 + " + " + id3;
	}

}
