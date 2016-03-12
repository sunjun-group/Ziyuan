package invariant.templates.twofeatures;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.formula.Eq;
import sav.common.core.formula.Var;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;

public class TwoNumSquareTemplate extends TwoFeaturesTemplate {

	public TwoNumSquareTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}
	
	@Override
	public boolean checkPassValue(List<ExecValue> evl) {
		// list of pass and fail exec value only has two features
		// first feature must be equals to square of second feature
		double v1 = evl.get(0).getDoubleVal();
		double v2 = evl.get(1).getDoubleVal();
		return v1 == Math.pow(v2, 2);
	}
	
	@Override
	public boolean checkFailValue(List<ExecValue> evl) {
		// list of pass and fail exec value only has two features
		// first feature must not be equals to square of second feature
		double v1 = evl.get(0).getDoubleVal();
		double v2 = evl.get(1).getDoubleVal();
		return v1 != Math.pow(v2, 2);
	}
	
	@Override
	public List<List<Eq<?>>> sampling() {
		List<List<Eq<?>>> samples = new ArrayList<List<Eq<?>>>();
		
		ExecValue ev1 = passValues.get(0).get(0);
		Var v1 = new ExecVar(ev1.getVarId(), ev1.getType());
		
		ExecValue ev2 = passValues.get(0).get(1);
		Var v2 = new ExecVar(ev2.getVarId(), ev2.getType());
		
		List<Eq<?>> sample1 = new ArrayList<Eq<?>>();
		if (ev1.getType() == ExecVarType.INTEGER) {
			sample1.add(new Eq<Number>(v1, 1));
			sample1.add(new Eq<Number>(v2, 1));
		} else if (ev1.getType() == ExecVarType.LONG) {
			sample1.add(new Eq<Number>(v1, 1L));
			sample1.add(new Eq<Number>(v2, 1L));
		} else {
			sample1.add(new Eq<Number>(v1, 1.0));
			sample1.add(new Eq<Number>(v2, 1.0));
		}
		
		List<Eq<?>> sample2 = new ArrayList<Eq<?>>();
		if (ev1.getType() == ExecVarType.INTEGER) {
			sample2.add(new Eq<Number>(v1, -1));
			sample2.add(new Eq<Number>(v2, 1));
		} else if (ev1.getType() == ExecVarType.LONG) {
			sample2.add(new Eq<Number>(v1, -1L));
			sample2.add(new Eq<Number>(v2, 1L));
		} else {
			sample2.add(new Eq<Number>(v1, -1.0));
			sample2.add(new Eq<Number>(v2, 1.0));
		}
		
		samples.add(sample1);
		samples.add(sample2);
		
		return samples;
	}
	
	@Override
	public String toString() {
		String id1 = passValues.get(0).get(0).getVarId();
		String id2 = passValues.get(0).get(1).getVarId();
		
		return id1 + " = " + "square(" + id2 + ")";
	}

}
