package sav.strategies.dto.execute.value;

public class FloatValue extends PrimitiveValue {
	private Float value;
	
	public FloatValue(String id, Float value) {
		super(id, String.valueOf(value));
		this.value = value;
	}
	
	@Override
	public Double getDoubleVal() {
		return (value == null) ? null : (double) value;
	}
	
	public Float getFloatVal() {
		return (value == null) ? null : value;
	}
	
	public static FloatValue of(String id, float value) {
		return new FloatValue(id, value);
	}
	
	@Override
	public ExecVarType getType() {
		return ExecVarType.FLOAT;
	}

}
