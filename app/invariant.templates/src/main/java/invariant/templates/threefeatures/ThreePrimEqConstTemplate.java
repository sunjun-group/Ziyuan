package invariant.templates.threefeatures;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.formula.Eq;
import sav.common.core.formula.Var;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;

public class ThreePrimEqConstTemplate extends ThreeFeaturesTemplate {
	
	private double a = 0.0;
	
	private double b = 0.0;
	
	private double c = 0.0;
	
	private double d = 0.0;
	
	public ThreePrimEqConstTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}
	
	@Override
	public boolean checkPassValue(List<ExecValue> evl) {
		double v1 = evl.get(0).getDoubleVal();
		double v2 = evl.get(1).getDoubleVal();
		double v3 = evl.get(2).getDoubleVal();
		return a * v1 + b * v2 + c * v3 + d == 0;
	}
	
	@Override
	public boolean checkFailValue(List<ExecValue> evl) {
		double v1 = evl.get(0).getDoubleVal();
		double v2 = evl.get(1).getDoubleVal();
		double v3 = evl.get(2).getDoubleVal();
		return a * v1 + b * v2 + c * v3 + d != 0;
	}
	
	@Override
	public boolean check() {
		if (passExecValuesList.size() <= 3) return false;
		
		double x1 = passExecValuesList.get(0).get(0).getDoubleVal();
		double y1 = passExecValuesList.get(0).get(1).getDoubleVal();
		double z1 = passExecValuesList.get(0).get(2).getDoubleVal();
		
		double x2 = passExecValuesList.get(1).get(0).getDoubleVal();
		double y2 = passExecValuesList.get(1).get(1).getDoubleVal();
		double z2 = passExecValuesList.get(1).get(2).getDoubleVal();
		
		double x3 = passExecValuesList.get(2).get(0).getDoubleVal();
		double y3 = passExecValuesList.get(2).get(1).getDoubleVal();
		double z3 = passExecValuesList.get(2).get(2).getDoubleVal();
		
		double x12 = x2 - x1, y12 = y2 - y1, z12 = z2 - z1;
		double x13 = x3 - x1, y13 = y3 - y1, z13 = z3 - z1;
		
		a = y12 * z13 - z12 * y13;
		b = - (x12 * z13 - z12 * x13);
		c = x12 * y13 - y12 * x13;
		d = -(a * x1 + b * y1 + c * z1);
		
		return checkAllPassValues(passExecValuesList) && checkAllFailValues(failExecValuesList);
	}

	@Override
	public List<List<Eq<?>>> sampling() {
		List<List<Eq<?>>> samples = new ArrayList<List<Eq<?>>>();
		
		ExecValue ev1 = passExecValuesList.get(0).get(0);
		Var v1 = new ExecVar(ev1.getVarId(), ev1.getType());
		
		ExecValue ev2 = passExecValuesList.get(0).get(1);
		Var v2 = new ExecVar(ev2.getVarId(), ev2.getType());
		
		ExecValue ev3 = passExecValuesList.get(0).get(2);
		Var v3 = new ExecVar(ev3.getVarId(), ev3.getType());
		
		List<Eq<?>> sample1 = new ArrayList<Eq<?>>();
		sample1.add(new Eq<Number>(v1, 0.0));
		sample1.add(new Eq<Number>(v2, 0.0));
		sample1.add(new Eq<Number>(v3, 0.0));
		
		List<Eq<?>> sample2 = new ArrayList<Eq<?>>();
		sample2.add(new Eq<Number>(v1, 1.0));
		sample2.add(new Eq<Number>(v2, 1.0));
		sample2.add(new Eq<Number>(v3, 1.0));
	
		samples.add(sample1);
		samples.add(sample2);
		
		return samples;
	}
	
	@Override
	public String toString() {
		return a + "*" + passExecValuesList.get(0).get(0).getVarId() + " + " +
				b + "*" + passExecValuesList.get(0).get(1).getVarId() + " + " +
				c + "*" + passExecValuesList.get(0).get(2).getVarId() + " + " +
				d + " = 0";
	}
	
}
