package invariant.templates.threefeatures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sav.common.core.formula.Eq;
import sav.common.core.formula.Var;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;

// Tempalte ax + by + cz = d

public class ThreeNumEqTemplate extends ThreeFeaturesTemplate {
	
	private double a = 0.0;
	
	private double b = 0.0;
	
	private double c = 0.0;
	
	private double d = 0.0;
	
	public ThreeNumEqTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
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
		if (passValues.size() <= 3) return false;
		
		double x1 = passValues.get(0).get(0).getDoubleVal();
		double y1 = passValues.get(0).get(1).getDoubleVal();
		double z1 = passValues.get(0).get(2).getDoubleVal();
		
		double x2 = passValues.get(1).get(0).getDoubleVal();
		double y2 = passValues.get(1).get(1).getDoubleVal();
		double z2 = passValues.get(1).get(2).getDoubleVal();
		
		double x3 = passValues.get(2).get(0).getDoubleVal();
		double y3 = passValues.get(2).get(1).getDoubleVal();
		double z3 = passValues.get(2).get(2).getDoubleVal();
		
		double x12 = x2 - x1, y12 = y2 - y1, z12 = z2 - z1;
		double x13 = x3 - x1, y13 = y3 - y1, z13 = z3 - z1;
		
		a = y12 * z13 - z12 * y13;
		b = - (x12 * z13 - z12 * x13);
		c = x12 * y13 - y12 * x13;
		d = a * x1 + b * y1 + c * z1;
		
		return check(passValues, failValues);
	}

	@Override
	public List<List<Eq<?>>> sampling() {
		List<List<Eq<?>>> samples = new ArrayList<List<Eq<?>>>();
		
		ExecValue ev1 = passValues.get(0).get(0);
		ExecValue ev2 = passValues.get(0).get(1);
		ExecValue ev3 = passValues.get(0).get(3);
		
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
		
		if (t1 == ExecVarType.INTEGER || t1 == ExecVarType.LONG ||
				t1 == ExecVarType.BYTE || t1 == ExecVarType.SHORT) {
			samples.add(sampling(vs, ts, Arrays.asList(d / a, 0.0, 0.0)));
			samples.add(sampling(vs, ts, Arrays.asList(d / a - 1.0, 0.0, 0.0)));
			samples.add(sampling(vs, ts, Arrays.asList(d / a + 1.0, 0.0, 0.0)));
		} else if (t1 == ExecVarType.FLOAT || t1 == ExecVarType.DOUBLE) {
			samples.add(sampling(vs, ts, Arrays.asList(d / a, 0.0, 0.0)));
			samples.add(sampling(vs, ts, Arrays.asList(d / a - offset, 0.0, 0.0)));
			samples.add(sampling(vs, ts, Arrays.asList(d / a + offset, 0.0, 0.0)));
		}
		
		if (t2 == ExecVarType.INTEGER || t2 == ExecVarType.LONG ||
				t2 == ExecVarType.BYTE || t2 == ExecVarType.SHORT) {
			samples.add(sampling(vs, ts, Arrays.asList(0.0, d / b, 0.0)));
			samples.add(sampling(vs, ts, Arrays.asList(0.0, d / b - 1.0, 0.0)));
			samples.add(sampling(vs, ts, Arrays.asList(0.0, d / b + 1.0, 0.0)));
		} else if (t2 == ExecVarType.FLOAT || t2 == ExecVarType.DOUBLE) {
			samples.add(sampling(vs, ts, Arrays.asList(0.0, d / b, 0.0)));
			samples.add(sampling(vs, ts, Arrays.asList(0.0, d / b - offset, 0.0)));
			samples.add(sampling(vs, ts, Arrays.asList(0.0, d / b + offset, 0.0)));
		}
		
		if (t3 == ExecVarType.INTEGER || t3 == ExecVarType.LONG ||
				t3 == ExecVarType.BYTE || t3 == ExecVarType.SHORT) {
			samples.add(sampling(vs, ts, Arrays.asList(0.0, 0.0, d / c)));
			samples.add(sampling(vs, ts, Arrays.asList(0.0, 0.0, d / c - 1.0)));
			samples.add(sampling(vs, ts, Arrays.asList(0.0, 0.0, d / c + 1.0)));
		} else if (t3 == ExecVarType.FLOAT || t3 == ExecVarType.DOUBLE) {
			samples.add(sampling(vs, ts, Arrays.asList(0.0, 0.0, d / c)));
			samples.add(sampling(vs, ts, Arrays.asList(0.0, 0.0, d / c - offset)));
			samples.add(sampling(vs, ts, Arrays.asList(0.0, 0.0, d / c + offset)));
		}
		
		return samples;
	}
	
	@Override
	public String toString() {
		return a + "*" + passValues.get(0).get(0).getVarId() + " + " +
				b + "*" + passValues.get(0).get(1).getVarId() + " + " +
				c + "*" + passValues.get(0).get(2).getVarId() + " = " + d;
	}
	
}
