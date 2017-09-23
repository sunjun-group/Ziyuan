package invariant.templates.onefeature;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.formula.Eq;
import sav.common.core.formula.Var;
import sav.strategies.dto.execute.value.ByteValue;
import sav.strategies.dto.execute.value.DoubleValue;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;
import sav.strategies.dto.execute.value.FloatValue;
import sav.strategies.dto.execute.value.IntegerValue;
import sav.strategies.dto.execute.value.LongValue;
import sav.strategies.dto.execute.value.ShortValue;

public class OneNumNeMinTemplate extends OneFeatureTemplate {
	
	public OneNumNeMinTemplate(List<List<ExecValue>> passValues, List<List<ExecValue>> failValues) {
		super(passValues, failValues);
	}
	
	@Override
	public boolean checkPassValue(List<ExecValue> evl) {
		if (evl.get(0).getDoubleVal() == null)
			return false;
		
		switch(evl.get(0).getType()) {
		case BYTE:
			return ((ByteValue) evl.get(0)).getByteVal() != Byte.MIN_VALUE;
		case SHORT:
			return ((ShortValue) evl.get(0)).getShortVal() != Short.MIN_VALUE;
		case INTEGER:
			return ((IntegerValue) evl.get(0)).getIntegerVal() != Integer.MIN_VALUE;
		case LONG:
			return ((LongValue) evl.get(0)).getLongVal() != Long.MIN_VALUE;
		case FLOAT:
			return ((FloatValue) evl.get(0)).getFloatVal() != Float.MIN_VALUE;
		case DOUBLE:
			return ((DoubleValue) evl.get(0)).getDoubleVal() != Double.MIN_VALUE;
		default:
			break;
		}
		
		return false;
	}
	
	@Override
	public boolean checkFailValue(List<ExecValue> evl) {
		if (evl.get(0).getDoubleVal() == null)
			return false;
		
		switch(evl.get(0).getType()) {
		case BYTE:
			return ((ByteValue) evl.get(0)).getByteVal() == Byte.MIN_VALUE;
		case SHORT:
			return ((ShortValue) evl.get(0)).getShortVal() == Short.MIN_VALUE;
		case INTEGER:
			return ((IntegerValue) evl.get(0)).getIntegerVal() == Integer.MIN_VALUE;
		case LONG:
			return ((LongValue) evl.get(0)).getLongVal() == Long.MIN_VALUE;
		case FLOAT:
			return ((FloatValue) evl.get(0)).getFloatVal() == Float.MIN_VALUE;
		case DOUBLE:
			return ((DoubleValue) evl.get(0)).getDoubleVal() == Double.MIN_VALUE;
		default:
			break;
		}
		
		return false;
	}
	
	@Override
	public boolean check() {
		return check(passValues, failValues);
	}
	
	@Override
	public List<List<Eq<?>>> sampling() {
		List<List<Eq<?>>> samples = new ArrayList<List<Eq<?>>>();
		
		ExecValue ev = passValues.get(0).get(0);
		
		String id = ev.getVarId();
		ExecVarType t = ev.getType();
		
		Var v = new ExecVar(id, t);
		
		ExecVarType type = ev.getType();
		
		if (type == ExecVarType.BYTE) {
			samples.add(sampling(v, (byte) (Byte.MIN_VALUE + 1)));
		} else if (type == ExecVarType.SHORT) {
			samples.add(sampling(v, (short) (Short.MIN_VALUE + 1)));
		} else if (type == ExecVarType.INTEGER) {
			samples.add(sampling(v, Integer.MIN_VALUE + 1));
		} else if (type == ExecVarType.LONG) {
			samples.add(sampling(v, Long.MIN_VALUE + 1));
		} else if (type == ExecVarType.FLOAT) {
			samples.add(sampling(v, Float.MIN_VALUE + 0.01f));
		} else if (type == ExecVarType.DOUBLE) {
			samples.add(sampling(v, Double.MIN_VALUE + 0.01));
		}
		
		return samples;
	}
	
	@Override
	public String toString() {
		return passValues.get(0).get(0).getVarId() + " != MIN_VALUE";
	}

}
