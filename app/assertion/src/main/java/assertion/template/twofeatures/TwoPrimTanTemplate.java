package assertion.template.twofeatures;

import java.util.ArrayList;
import java.util.List;

import icsetlv.common.dto.ExecValue;
import icsetlv.common.dto.ExecVar;
import sav.common.core.formula.Eq;
import sav.common.core.formula.Var;

public class TwoPrimTanTemplate extends TwoFeaturesTemplate {

	public TwoPrimTanTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}
	
	@Override
	public boolean check() {
		// list of pass and fail exec value only has two features
		// first feature must be equals to square of second feature
		for (List<ExecValue> evl : passExecValuesList) {
			double v1 = evl.get(0).getDoubleVal();
			double v2 = evl.get(1).getDoubleVal();
			if (v1 != Math.tan(v2)) return false;
		}
				
		// first feature must not be equals to abs of second feature
		for (List<ExecValue> evl : failExecValuesList) {
			double v1 = evl.get(0).getDoubleVal();
			double v2 = evl.get(1).getDoubleVal();
			if (v1 == Math.tan(v2)) return false;
		}
				
		return true;
	}
	
	@Override
	public List<List<Eq<?>>> sampling() {
		List<List<Eq<?>>> samples = new ArrayList<List<Eq<?>>>();
		
		ExecValue ev1 = passExecValuesList.get(0).get(0);
		Var v1 = new ExecVar(ev1.getVarId(), ev1.getType());
		
		ExecValue ev2 = passExecValuesList.get(0).get(1);
		Var v2 = new ExecVar(ev2.getVarId(), ev2.getType());
		
		List<Eq<?>> sample1 = new ArrayList<Eq<?>>();
		sample1.add(new Eq<Number>(v1, 0.0));
		sample1.add(new Eq<Number>(v2, 0.0));
		
		List<Eq<?>> sample2 = new ArrayList<Eq<?>>();
		sample2.add(new Eq<Number>(v1, 0.0));
		sample2.add(new Eq<Number>(v2, Math.PI));
		
		samples.add(sample1);
		samples.add(sample2);
		
		return samples;
	}
	
	@Override
	public String toString() {
		String id1 = passExecValuesList.get(0).get(0).getVarId();
		String id2 = passExecValuesList.get(0).get(1).getVarId();
		
		return id1 + " = " + "tan(" + id2 + ")";
	}

}
