package sav.strategies.dto.execute.value;

public class IntegerValue extends PrimitiveValue {
	private int value;
	
	public IntegerValue(String id, int value) {
		super(id, String.valueOf(value));
		this.value = value;
	}
	
	@Override
	public double getDoubleVal() {
		return value;
	}
	
	public int getIntegerVal() {
		return value;
	}
	
	public static IntegerValue of(String id, int value) {
		return new IntegerValue(id, value);
	}
	
	@Override
	public ExecVarType getType() {
		return ExecVarType.INTEGER;
	}

}
