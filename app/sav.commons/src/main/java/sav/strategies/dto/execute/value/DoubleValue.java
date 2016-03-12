package sav.strategies.dto.execute.value;

public class DoubleValue extends PrimitiveValue {
	private double value;
	
	public DoubleValue(String id, double value) {
		super(id, String.valueOf(value));
		this.value = value;
	}
	
	@Override
	public double getDoubleVal() {
		return value;
	}
	
	public static DoubleValue of(String id, double value) {
		return new DoubleValue(id, value);
	}
	
	@Override
	public ExecVarType getType() {
		return ExecVarType.DOUBLE;
	}

}
