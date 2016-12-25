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

public class TwoNumMulOverflowTemplate extends TwoFeaturesTemplate {

	public TwoNumMulOverflowTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
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
			
			return (l1 != -1 || l2 != Long.MIN_VALUE) &&
					(l2 != -1 || l1 != Long.MIN_VALUE) &&
					(l2 == 0 || (l1 * l2) / l2 == l1);
		} else if (evl.get(0).getType() == ExecVarType.INTEGER &&
				evl.get(1).getType() == ExecVarType.INTEGER) {
			long i1 = (long) ((IntegerValue) evl.get(0)).getIntegerVal();
			long i2 = (long) ((IntegerValue) evl.get(1)).getIntegerVal();
			
			long total = i1 * i2;
			
			return total >= Integer.MIN_VALUE && total <= Integer.MAX_VALUE;
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
			
			return (l1 == -1 && l2 == Long.MIN_VALUE) ||
					(l2 == -1 && l1 == Long.MIN_VALUE) ||
					(l2 != 0 && (l1 * l2) / l2 != l1);
		} else if (evl.get(0).getType() == ExecVarType.INTEGER &&
				evl.get(1).getType() == ExecVarType.INTEGER) {
			long i1 = (long) ((IntegerValue) evl.get(0)).getIntegerVal();
			long i2 = (long) ((IntegerValue) evl.get(1)).getIntegerVal();
			
			long total = i1 * i2;
			
			return total < Integer.MIN_VALUE || total > Integer.MAX_VALUE;
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
			samples.add(sampling(vs, ts, Arrays.asList(Integer.MAX_VALUE * 1.0, 1.0)));
			samples.add(sampling(vs, ts, Arrays.asList(Integer.MAX_VALUE / 2.0, 2.0)));
		} else if (t1 == ExecVarType.LONG) {
			samples.add(sampling(vs, ts, Arrays.asList(Long.MAX_VALUE * 1.0, 1.0)));
			samples.add(sampling(vs, ts, Arrays.asList(Long.MAX_VALUE / 2.0, 2.0)));
		}
		
		/*else if (t1 == ExecVarType.FLOAT) {
			samples.add(sampling(vs, ts, Arrays.asList(Float.MAX_VALUE * 1.0, 1.0)));
			samples.add(sampling(vs, ts, Arrays.asList(Float.MAX_VALUE / 2.0, 2.0)));
		} else if (t1 == ExecVarType.DOUBLE) {
			samples.add(sampling(vs, ts, Arrays.asList(Double.MAX_VALUE * 1.0, 1.0)));
			samples.add(sampling(vs, ts, Arrays.asList(Double.MAX_VALUE / 2.0, 2.0)));
		}*/
		
		if (t2 == ExecVarType.INTEGER) {
			samples.add(sampling(vs, ts, Arrays.asList(1.0, Integer.MAX_VALUE * 1.0)));
			samples.add(sampling(vs, ts, Arrays.asList(2.0, Integer.MAX_VALUE / 2.0)));
		} else if (t2 == ExecVarType.LONG) {
			samples.add(sampling(vs, ts, Arrays.asList(1.0, Long.MAX_VALUE * 1.0)));
			samples.add(sampling(vs, ts, Arrays.asList(2.0, Long.MAX_VALUE / 2.0)));
		}
		
		/*else if (t2 == ExecVarType.FLOAT) {
			samples.add(sampling(vs, ts, Arrays.asList(1.0, Float.MAX_VALUE * 1.0)));
			samples.add(sampling(vs, ts, Arrays.asList(2.0, Float.MAX_VALUE / 2.0)));
		} else if (t2 == ExecVarType.DOUBLE) {
			samples.add(sampling(vs, ts, Arrays.asList(1.0, Double.MAX_VALUE * 1.0)));
			samples.add(sampling(vs, ts, Arrays.asList(2.0, Double.MAX_VALUE / 2.0)));
		}*/
		
		return samples;
	}
	
	@Override
	public String toString() {
		String id1 = passValues.get(0).get(0).getVarId();
		String id2 = passValues.get(0).get(1).getVarId();
		
		return id1 + " * " + id2 + " is not overflow";
	}

}
