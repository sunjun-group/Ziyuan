package sav.strategies.dto.execute.value;

public class LongValue extends PrimitiveValue {
	private long value;
	
	public LongValue(String id, long value) {
		super(id, String.valueOf(value));
		this.value = value;
	}
	
	@Override
	public double getDoubleVal() {
		return value;
	}
	
	public long getLongVal() {
		return value;
	}
	
	public static LongValue of(String id, long value) {
		return new LongValue(id, value);
	}
	
	@Override
	public ExecVarType getType() {
		return ExecVarType.LONG;
	}

}
