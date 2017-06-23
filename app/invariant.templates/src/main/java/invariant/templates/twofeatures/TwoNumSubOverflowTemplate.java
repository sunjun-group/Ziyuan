package invariant.templates.twofeatures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sav.common.core.formula.Eq;
import sav.common.core.formula.Var;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;
import sav.strategies.dto.execute.value.IntegerValue;
import sav.strategies.dto.execute.value.LongValue;

public class TwoNumSubOverflowTemplate extends TwoFeaturesTemplate {

	public TwoNumSubOverflowTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}
	
	@Override
	public boolean checkPassValue(List<ExecValue> evl) {
		if (evl.get(0).getDoubleVal() == null ||
				evl.get(1).getDoubleVal() == null)
			return false;
		
		// not overflow for long and int
		if (evl.get(0).getType() == ExecVarType.LONG &&
				evl.get(1).getType() == ExecVarType.LONG) {
			long l1 = ((LongValue) evl.get(0)).getLongVal();
			long l2 = ((LongValue) evl.get(1)).getLongVal();
			
			long diff = l1 - l2;
			
			return (l1 ^ diff) >= 0 || (l1 ^ l2) >= 0;
		} else if (evl.get(0).getType() == ExecVarType.INTEGER &&
				evl.get(1).getType() == ExecVarType.INTEGER) {
			int i1 = ((IntegerValue) evl.get(0)).getIntegerVal();
			int i2 = ((IntegerValue) evl.get(1)).getIntegerVal();
			
			int diff = i1 - i2;
			
			return (i1 ^ diff) >= 0 || (i1 ^ i2) >= 0;
		}
		
		return false;
	}
	
	@Override
	public boolean checkFailValue(List<ExecValue> evl) {
		if (evl.get(0).getDoubleVal() == null ||
				evl.get(1).getDoubleVal() == null)
			return false;
		
		if (evl.get(0).getType() == ExecVarType.LONG &&
				evl.get(1).getType() == ExecVarType.LONG) {
			long l1 = ((LongValue) evl.get(0)).getLongVal();
			long l2 = ((LongValue) evl.get(1)).getLongVal();
			
			long diff = l1 - l2;
			
			return (l1 ^ diff) < 0 && (l1 ^ l2) < 0;
		} else if (evl.get(0).getType() == ExecVarType.INTEGER &&
				evl.get(1).getType() == ExecVarType.INTEGER) {
			int i1 = ((IntegerValue) evl.get(0)).getIntegerVal();
			int i2 = ((IntegerValue) evl.get(1)).getIntegerVal();
			
			int diff = i1 - i2;
			
			return (i1 ^ diff) < 0 && (i1 ^ i2) < 0;
		}
		
		return false;
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
		
		if (t1 == ExecVarType.INTEGER) {
			samples.add(sampling(vs, ts, Arrays.asList(Integer.MIN_VALUE * 1.0, 0.0)));
			samples.add(sampling(vs, ts, Arrays.asList(Integer.MIN_VALUE * 1.0, 1.0)));
			samples.add(sampling(vs, ts, Arrays.asList(Integer.MIN_VALUE * 1.0, -1.0)));
		} else if (t1 == ExecVarType.LONG) {
			samples.add(sampling(vs, ts, Arrays.asList(Long.MIN_VALUE * 1.0, 0.0)));
			samples.add(sampling(vs, ts, Arrays.asList(Long.MIN_VALUE * 1.0, 1.0)));
			samples.add(sampling(vs, ts, Arrays.asList(Long.MIN_VALUE * 1.0, -1.0)));
		}
		
		/* else if (t1 == ExecVarType.FLOAT) {
			samples.add(sampling(vs, ts, Arrays.asList(Float.MIN_VALUE * 1.0, 0.0)));
			samples.add(sampling(vs, ts, Arrays.asList(Float.MIN_VALUE * 1.0, 1.0)));
			samples.add(sampling(vs, ts, Arrays.asList(Float.MIN_VALUE * 1.0, -1.0)));
		} else if (t1 == ExecVarType.DOUBLE) {
			samples.add(sampling(vs, ts, Arrays.asList(Double.MIN_VALUE * 1.0, 0.0)));
			samples.add(sampling(vs, ts, Arrays.asList(Double.MIN_VALUE * 1.0, 1.0)));
			samples.add(sampling(vs, ts, Arrays.asList(Double.MIN_VALUE * 1.0, -1.0)));
		} */
		
		if (t2 == ExecVarType.INTEGER) {
			samples.add(sampling(vs, ts, Arrays.asList(0.0, Integer.MIN_VALUE * 1.0)));
			samples.add(sampling(vs, ts, Arrays.asList(1.0, Integer.MIN_VALUE * 1.0)));
			samples.add(sampling(vs, ts, Arrays.asList(-1.0, Integer.MIN_VALUE * 1.0)));
		} else if (t2 == ExecVarType.LONG) {
			samples.add(sampling(vs, ts, Arrays.asList(0.0, Long.MIN_VALUE * 1.0)));
			samples.add(sampling(vs, ts, Arrays.asList(1.0, Long.MIN_VALUE * 1.0)));
			samples.add(sampling(vs, ts, Arrays.asList(-1.0, Long.MIN_VALUE * 1.0)));
		}
		
		/* else if (t2 == ExecVarType.FLOAT) {
			samples.add(sampling(vs, ts, Arrays.asList(0.0, Float.MIN_VALUE * 1.0)));
			samples.add(sampling(vs, ts, Arrays.asList(1.0, Float.MIN_VALUE * 1.0)));
			samples.add(sampling(vs, ts, Arrays.asList(-1.0, Float.MIN_VALUE * 1.0)));
		} else if (t2 == ExecVarType.DOUBLE) {
			samples.add(sampling(vs, ts, Arrays.asList(0.0, Double.MIN_VALUE * 1.0)));
			samples.add(sampling(vs, ts, Arrays.asList(1.0, Double.MIN_VALUE * 1.0)));
			samples.add(sampling(vs, ts, Arrays.asList(-1.0, Double.MIN_VALUE * 1.0)));
		} */
		
		return samples;
	}
	
	@Override
	public String toString() {
		String id1 = passValues.get(0).get(0).getVarId();
		String id2 = passValues.get(0).get(1).getVarId();
		
		return id1 + " - " + id2 + " is not overflow";
	}

}
