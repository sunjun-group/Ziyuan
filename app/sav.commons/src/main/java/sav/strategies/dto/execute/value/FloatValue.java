package sav.strategies.dto.execute.value;

public class FloatValue extends PrimitiveValue {
	private float value;
	
	public FloatValue(String id, float value) {
		super(id, String.valueOf(value));
		this.value = value;
	}
	
	@Override
	public double getDoubleVal() {
		return value;
	}
	
	public float getFloatVal() {
		return value;
	}
	
	public static FloatValue of(String id, float value) {
		return new FloatValue(id, value);
	}
	
	@Override
	public ExecVarType getType() {
		return ExecVarType.FLOAT;
	}

}
