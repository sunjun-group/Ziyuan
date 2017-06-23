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

// Template x != a

public class OneNumNeTemplate extends OneFeatureTemplate {

	private ExecVarType type;
	
	private String value;
	
	public OneNumNeTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}

	@Override
	public boolean checkPassValue(List<ExecValue> evl) {
		// list of pass and fail exec value only has one feature
		// all pass values must be must be different with the fail value
		switch(type) {
		case BYTE:
			return !((ByteValue) evl.get(0)).getByteVal().
					toString().equals(value);
		case SHORT:
			return !((ShortValue) evl.get(0)).getShortVal().
					toString().equals(value);
		case INTEGER:
			return !((IntegerValue) evl.get(0)).getIntegerVal().
					toString().equals(value);
		case LONG:
			return !((LongValue) evl.get(0)).getLongVal().
					toString().equals(value);
		case FLOAT:
			return !((FloatValue) evl.get(0)).getFloatVal().
					toString().equals(value);
		case DOUBLE:
			return !((DoubleValue) evl.get(0)).getDoubleVal().
					toString().equals(value);
		default:
			break;
		}
		
		return false;
	}
	
	@Override
	public boolean checkFailValue(List<ExecValue> evl) {
		// list of pass and fail exec value only has one feature
		// all fail value must be the same
		switch(type) {
		case BYTE:
			return ((ByteValue) evl.get(0)).getByteVal().
					toString().equals(value);
		case SHORT:
			return ((ShortValue) evl.get(0)).getShortVal().
					toString().equals(value);
		case INTEGER:
			return ((IntegerValue) evl.get(0)).getIntegerVal().
					toString().equals(value);
		case LONG:
			return ((LongValue) evl.get(0)).getLongVal().
					toString().equals(value);
		case FLOAT:
			return ((FloatValue) evl.get(0)).getFloatVal().
					toString().equals(value);
		case DOUBLE:
			return ((DoubleValue) evl.get(0)).getDoubleVal().
					toString().equals(value);
		default:
			break;
		}
		
		return false;
	}
	
	@Override
	public boolean check() {
		type = failValues.get(0).get(0).getType();
		
		switch(type) {
		case BYTE:
			value = ((ByteValue) failValues.get(0).get(0)).getByteVal().toString();
			break;
		case SHORT:
			value = ((ShortValue) failValues.get(0).get(0)).getShortVal().toString();
			break;
		case INTEGER:
			value = ((IntegerValue) failValues.get(0).get(0)).getIntegerVal().toString();
			break;
		case LONG:
			value = ((LongValue) failValues.get(0).get(0)).getLongVal().toString();
			break;
		case FLOAT:
			value = ((FloatValue) failValues.get(0).get(0)).getFloatVal().toString();
			break;
		case DOUBLE:
			value = ((DoubleValue) failValues.get(0).get(0)).getDoubleVal().toString();
			break;
		default:
			value = "";
			break;
		}
		
 		return check(passValues, failValues);
	}
	
	@Override
	public List<List<Eq<?>>> sampling() {
		List<List<Eq<?>>> samples = new ArrayList<List<Eq<?>>>();
		
		ExecValue ev = passValues.get(0).get(0);
		
		String id = ev.getVarId();
		ExecVarType t = ev.getType();
		
		Var v = new ExecVar(id, t);
		
		if (type == ExecVarType.BYTE) {
			byte b = Byte.parseByte(value);
			
			samples.add(sampling(v, (byte) (b - 1)));
			samples.add(sampling(v, (byte) (b + 1)));
		} else if (type == ExecVarType.SHORT) {
			short s = Short.parseShort(value);
			
			samples.add(sampling(v, (short) (s - 1)));
			samples.add(sampling(v, (short) (s + 1)));
		} else if (type == ExecVarType.INTEGER) {
			int i = Integer.parseInt(value);
			
			samples.add(sampling(v, i - 1));
			samples.add(sampling(v, i + 1));
		} else if (type == ExecVarType.LONG) {
			long l = Long.parseLong(value);
			
			samples.add(sampling(v, l - 1));
			samples.add(sampling(v, l + 1));
		} else if (type == ExecVarType.FLOAT) {
			float f = Float.parseFloat(value);
			
			samples.add(sampling(v, f - 0.01f));
			samples.add(sampling(v, f + 0.01f));
		} else if (type == ExecVarType.DOUBLE) {
			double d = Double.parseDouble(value);
			
			samples.add(sampling(v, d - 0.01));
			samples.add(sampling(v, d + 0.01));
		}
		
		return samples;
	}
	
	@Override
	public String toString() {
		return passValues.get(0).get(0).getVarId() + " != " + value;
	}
	
}
