package invariant.templates.twofeatures;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.formula.Eq;
import sav.common.core.formula.Var;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;

// Template ax + by = c

public class TwoPrimEqTemplate extends TwoFeaturesTemplate {

	private double a = 0.0;
	
	private double b = 0.0;
	
	private double c = 0.0;
	
	public TwoPrimEqTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}
	
	@Override
	public boolean checkPassValue(List<ExecValue> evl) {
		double v1 = evl.get(0).getDoubleVal();
		double v2 = evl.get(1).getDoubleVal();
		return a * v1 + b * v2 + c == 0;
	}
	
	@Override
	public boolean checkFailValue(List<ExecValue> evl) {
		double v1 = evl.get(0).getDoubleVal();
		double v2 = evl.get(1).getDoubleVal();
		return a * v1 + b * v2 + c != 0;
	}
	
	@Override
	public boolean check() {
		if (passExecValuesList.size() <= 2) return false;
		
		double x1 = passExecValuesList.get(0).get(0).getDoubleVal();
		double y1 = passExecValuesList.get(0).get(1).getDoubleVal();
		
		double x2 = passExecValuesList.get(1).get(0).getDoubleVal();
		double y2 = passExecValuesList.get(2).get(1).getDoubleVal();
		
		double x12 = x2 - x1;
		double y12 = y2 - y1;
		
		a = y12; b = -x12;
		c = a * x1 + b * y1;
		
		return check(passExecValuesList, failExecValuesList);
	}
	
	@Override
	public List<List<Eq<?>>> sampling() {
		List<List<Eq<?>>> samples = new ArrayList<List<Eq<?>>>();
		
		ExecValue ev1 = passExecValuesList.get(0).get(0);
		Var v1 = new ExecVar(ev1.getVarId(), ev1.getType());
		
		ExecValue ev2 = passExecValuesList.get(0).get(1);
		Var v2 = new ExecVar(ev2.getVarId(), ev2.getType());
		
		List<Eq<?>> sample1 = new ArrayList<Eq<?>>();
		sample1.add(new Eq<Number>(v1, (int) (c / a)));
		sample1.add(new Eq<Number>(v2, (int) 0.0));
		
		List<Eq<?>> sample2 = new ArrayList<Eq<?>>();
		sample2.add(new Eq<Number>(v1, (int) (c / a - 1.0)));
		sample2.add(new Eq<Number>(v2, (int) 0.0));
		
		List<Eq<?>> sample3 = new ArrayList<Eq<?>>();
		sample3.add(new Eq<Number>(v1, (int) (c / a + 1.0)));
		sample3.add(new Eq<Number>(v2, (int) 0.0));
		
		List<Eq<?>> sample4 = new ArrayList<Eq<?>>();
		sample4.add(new Eq<Number>(v1, (int) 0.0));
		sample4.add(new Eq<Number>(v2, (int) (c / b)));
		
		List<Eq<?>> sample5 = new ArrayList<Eq<?>>();
		sample5.add(new Eq<Number>(v1, (int) 0.0));
		sample5.add(new Eq<Number>(v2, (int) (c / b - 1.0)));
		
		List<Eq<?>> sample6 = new ArrayList<Eq<?>>();
		sample6.add(new Eq<Number>(v1, (int) 0.0));
		sample6.add(new Eq<Number>(v2, (int) (c / b + 1.0)));
		
		samples.add(sample1);
		samples.add(sample2);
		samples.add(sample3);
		samples.add(sample4);
		samples.add(sample5);
		samples.add(sample6);
		
		return samples;
	}
	
	@Override
	public String toString() {
		return a + "*" + passExecValuesList.get(0).get(0).getVarId() + " + " +
				b + "*" + passExecValuesList.get(0).get(1).getVarId() + " = " + c;
	}
	
}
