package sav.strategies.dto.execute.value;

public class LongValue extends PrimitiveValue {
	private Long value;
	
	public LongValue(String id, Long value) {
		super(id, String.valueOf(value));
		this.value = value;
	}
	
	@Override
	public Double getDoubleVal() {
		return (value == null) ? null : (double) value;
	}
	
	public Long getLongVal() {
		return (value == null) ? null : value;
	}
	
	public static LongValue of(String id, long value) {
		return new LongValue(id, value);
	}
	
	@Override
	public ExecVarType getType() {
		return ExecVarType.LONG;
	}

}
