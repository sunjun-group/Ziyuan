package invariant.templates.twofeatures;

import java.util.ArrayList;
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
		Var v1 = new ExecVar(ev1.getVarId(), ev1.getType());
		
		ExecValue ev2 = passValues.get(0).get(1);
		Var v2 = new ExecVar(ev2.getVarId(), ev2.getType());
		
		List<Eq<?>> sample1 = new ArrayList<Eq<?>>();
		if (ev1.getType() == ExecVarType.LONG && ev2.getType() == ExecVarType.LONG) {
			sample1.add(new Eq<Number>(v1, Long.MAX_VALUE));
			sample1.add(new Eq<Number>(v2, 1L));
		} else if (ev1.getType() == ExecVarType.INTEGER && ev2.getType() == ExecVarType.INTEGER) {
			sample1.add(new Eq<Number>(v1, Integer.MAX_VALUE));
			sample1.add(new Eq<Number>(v2, 1));
		}
		
		List<Eq<?>> sample2 = new ArrayList<Eq<?>>();
		if (ev1.getType() == ExecVarType.LONG && ev2.getType() == ExecVarType.LONG) {
			sample2.add(new Eq<Number>(v1, Long.MAX_VALUE / 2));
			sample2.add(new Eq<Number>(v2, 2L));
		} else if (ev1.getType() == ExecVarType.INTEGER && ev2.getType() == ExecVarType.INTEGER) {
			sample2.add(new Eq<Number>(v1, Integer.MAX_VALUE / 2));
			sample2.add(new Eq<Number>(v2, 2));
		}
		
		List<Eq<?>> sample3 = new ArrayList<Eq<?>>();
		if (ev1.getType() == ExecVarType.LONG && ev2.getType() == ExecVarType.LONG) {
			sample3.add(new Eq<Number>(v1, 1L));
			sample3.add(new Eq<Number>(v2, Long.MAX_VALUE));
		} else if (ev1.getType() == ExecVarType.INTEGER && ev2.getType() == ExecVarType.INTEGER) {
			sample3.add(new Eq<Number>(v1, 1));
			sample3.add(new Eq<Number>(v2, Integer.MAX_VALUE));
		}
		
		List<Eq<?>> sample4 = new ArrayList<Eq<?>>();
		if (ev1.getType() == ExecVarType.LONG && ev2.getType() == ExecVarType.LONG) {
			sample4.add(new Eq<Number>(v1, 2L));
			sample4.add(new Eq<Number>(v2, Long.MAX_VALUE / 2));
		} else if (ev1.getType() == ExecVarType.INTEGER && ev2.getType() == ExecVarType.INTEGER) {
			sample4.add(new Eq<Number>(v1, 2));
			sample4.add(new Eq<Number>(v2, Integer.MAX_VALUE / 2));
		}
		
		samples.add(sample1);
		samples.add(sample2);
		samples.add(sample3);
		samples.add(sample4);
		
		return samples;
	}
	
	@Override
	public String toString() {
		String id1 = passValues.get(0).get(0).getVarId();
		String id2 = passValues.get(0).get(1).getVarId();
		
		return id1 + " * " + id2 + " is not overflow";
	}

}
