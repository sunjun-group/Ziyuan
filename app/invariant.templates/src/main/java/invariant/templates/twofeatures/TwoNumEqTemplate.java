package invariant.templates.twofeatures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sav.common.core.formula.Eq;
import sav.common.core.formula.Var;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;

// Template ax + by = c

public class TwoNumEqTemplate extends TwoFeaturesTemplate {

	private double a = 0.0;
	
	private double b = 0.0;
	
	private double c = 0.0;
	
	public TwoNumEqTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
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
		if (passValues.size() <= 2) return false;
		
		double x1 = passValues.get(0).get(0).getDoubleVal();
		double y1 = passValues.get(0).get(1).getDoubleVal();
		
		double x2 = passValues.get(1).get(0).getDoubleVal();
		double y2 = passValues.get(1).get(1).getDoubleVal();
		
		double x12 = x2 - x1;
		double y12 = y2 - y1;
		
		a = y12; b = -x12;
		c = a * x1 + b * y1;
		
		return check(passValues, failValues);
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
		
		if (t1 == ExecVarType.INTEGER || t1 == ExecVarType.LONG ||
				t1 == ExecVarType.BYTE || t1 == ExecVarType.SHORT) {
			samples.add(sampling(vs, ts, Arrays.asList(c / a, 0.0)));
			samples.add(sampling(vs, ts, Arrays.asList(c / a - 1.0, 0.0)));
			samples.add(sampling(vs, ts, Arrays.asList(c / a + 1.0, 0.0)));
		} else if (t1 == ExecVarType.FLOAT || t1 == ExecVarType.DOUBLE) {
			samples.add(sampling(vs, ts, Arrays.asList(c / a, 0.0)));
			samples.add(sampling(vs, ts, Arrays.asList(c / a - offset, 0.0)));
			samples.add(sampling(vs, ts, Arrays.asList(c / a + offset, 0.0)));
		}
		
		if (t2 == ExecVarType.INTEGER || t2 == ExecVarType.LONG ||
				t2 == ExecVarType.BYTE || t2 == ExecVarType.SHORT) {
			samples.add(sampling(vs, ts, Arrays.asList(0.0, c / b)));
			samples.add(sampling(vs, ts, Arrays.asList(0.0, c / b - 1.0)));
			samples.add(sampling(vs, ts, Arrays.asList(0.0, c / b + 1.0)));
		} else if (t2 == ExecVarType.FLOAT || t2 == ExecVarType.DOUBLE) {
			samples.add(sampling(vs, ts, Arrays.asList(0.0, c / b)));
			samples.add(sampling(vs, ts, Arrays.asList(0.0, c / b - offset)));
			samples.add(sampling(vs, ts, Arrays.asList(0.0, c / b + offset)));
		}
		
		return samples;
	}
	
	@Override
	public String toString() {
		return a + "*" + passValues.get(0).get(0).getVarId() + " + " +
				b + "*" + passValues.get(0).get(1).getVarId() + " = " + c;
	}
	
}
