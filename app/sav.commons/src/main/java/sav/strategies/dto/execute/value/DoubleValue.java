package sav.strategies.dto.execute.value;

public class DoubleValue extends PrimitiveValue {
	private Double value;
	
	public DoubleValue(String id, Double value) {
		super(id, String.valueOf(value));
		this.value = value;
	}
	
	@Override
	public Double getDoubleVal() {
		return (value == null) ? null : value;
	}
	
	public static DoubleValue of(String id, double value) {
		return new DoubleValue(id, value);
	}
	
	@Override
	public ExecVarType getType() {
		return ExecVarType.DOUBLE;
	}

	@Override
	public ExecValue clone() {
		DoubleValue clone = new DoubleValue(varId, value);
		clone.valueType = valueType;
		return clone;
	}
}
