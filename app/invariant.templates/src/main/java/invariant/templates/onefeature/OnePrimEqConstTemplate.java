package invariant.templates.onefeature;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.formula.Eq;
import sav.common.core.formula.Var;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;

public class OnePrimEqConstTemplate extends OneFeatureTemplate {

	private double d;
	
	private boolean isInit;
	
	public OnePrimEqConstTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}

	@Override
	public boolean checkPassValue(List<ExecValue> evl) {
		// list of pass and fail exec value only has one feature
		// all pass values must be the same
		double v = evl.get(0).getDoubleVal();
		if (!isInit) {
			d = v;
			isInit = true;
			return true;
		} else {
			return v == d;
		}
	}
	
	@Override
	public boolean checkFailValue(List<ExecValue> evl) {
		// list of pass and fail exec value only has one feature
		// all fail value must be different with the pass value
		double v = evl.get(0).getDoubleVal();
		if (!isInit) {
			return false; // should be init already
		} else {
			return v != d;
		}
	}
	
	/*
	@Override
	public boolean check() {
		// list of pass and fail exec value only has one feature
		ExecValue ev = passExecValuesList.get(0).get(0);
		double v = ev.getDoubleVal();
		
		// all pass values must be the same
		for (List<ExecValue> evl : passExecValuesList) {
			if (evl.get(0).getDoubleVal() != v) {
				return false;
			}
		}
		
		// all fail value must be different with the pass value
		for (List<ExecValue> evl : failExecValuesList) {
			if (evl.get(0).getDoubleVal() == v) {
				return false;
			}
		}
		
		d = v;
		
		return true;
	}
	*/
	
	@Override
	public List<List<Eq<?>>> sampling() {
		List<List<Eq<?>>> samples = new ArrayList<List<Eq<?>>>();
		
		ExecValue ev = passExecValuesList.get(0).get(0);
		Var v = new ExecVar(ev.getVarId(), ev.getType());
		
		List<Eq<?>> sample1 = new ArrayList<Eq<?>>();
		sample1.add(new Eq<Number>(v, d - 1.0));
		
		List<Eq<?>> sample2 = new ArrayList<Eq<?>>();
		sample2.add(new Eq<Number>(v, d + 1.0));
		
		List<Eq<?>> sample3 = new ArrayList<Eq<?>>();
		sample3.add(new Eq<Number>(v, 0.0));
		
		samples.add(sample1);
		samples.add(sample2);
		samples.add(sample3);
		
		return samples;
	}
	
	@Override
	public String toString() {
		return passExecValuesList.get(0).get(0).getVarId() + " = " + d;
	}
	
}
