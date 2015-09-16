package assertion.template.onefeature;

import java.util.ArrayList;
import java.util.List;

import icsetlv.common.dto.ExecValue;
import icsetlv.common.dto.ExecVar;
import sav.common.core.formula.Eq;
import sav.common.core.formula.Var;

public class OnePrimRangeTemplate extends OneFeatureTemplate {

	private double min = 0.0;
	
	private double max = 0.0;
	
	private boolean change = false;
	
	public OnePrimRangeTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}

	@Override
	public boolean check() {
		// list of pass and fail exec value only has one feature
		// find min and max value of all pass values
		ExecValue ev = passExecValuesList.get(0).get(0);
		double minPass = ev.getDoubleVal();
		double maxPass = ev.getDoubleVal();
				
		for (List<ExecValue> evl : passExecValuesList) {
			if (evl.get(0).getDoubleVal() < minPass) {
				minPass = evl.get(0).getDoubleVal();
			}
			if (evl.get(0).getDoubleVal() > maxPass) {
				maxPass = evl.get(0).getDoubleVal();
			}
		}
		
		// all fail values must be less than minPass or greater than maxPass
		for (List<ExecValue> evl : failExecValuesList) {
			if (evl.get(0).getDoubleVal() >= minPass &&
					evl.get(0).getDoubleVal() <= maxPass) {
				return false;
			}
		}
		
		if (min != minPass || max != maxPass) {
			min = minPass; max = maxPass; change = true;
		} else {
			change = false;
		}
		
		return true;
	}
	
	@Override
	public boolean isChanged() {
		return change;
	}
	
	@Override
	public List<List<Eq<?>>> sampling() {
		List<List<Eq<?>>> samples = new ArrayList<List<Eq<?>>>();
		
		ExecValue ev = passExecValuesList.get(0).get(0);
		Var v = new ExecVar(ev.getVarId(), ev.getType());
		
		List<Eq<?>> sample1 = new ArrayList<Eq<?>>();
		sample1.add(new Eq<Number>(v, min - 1.0));
		
		List<Eq<?>> sample2 = new ArrayList<Eq<?>>();
		sample2.add(new Eq<Number>(v, min + 1.0));
		
		List<Eq<?>> sample3 = new ArrayList<Eq<?>>();
		sample3.add(new Eq<Number>(v, max - 1.0));
		
		List<Eq<?>> sample4 = new ArrayList<Eq<?>>();
		sample4.add(new Eq<Number>(v, max + 1.0));
		
		List<Eq<?>> sample5 = new ArrayList<Eq<?>>();
		sample5.add(new Eq<Number>(v, 0.0));
		
		samples.add(sample1);
		samples.add(sample2);
		samples.add(sample3);
		samples.add(sample4);
		samples.add(sample5);
		
		return samples;
	}
	
	
	
	@Override
	public String toString() {
		return min + " <= " + passExecValuesList.get(0).get(0).getVarId() + " <= " + max;
	}
	
}
