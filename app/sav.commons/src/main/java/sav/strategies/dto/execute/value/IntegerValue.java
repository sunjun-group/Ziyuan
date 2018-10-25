package sav.strategies.dto.execute.value;

public class IntegerValue extends PrimitiveValue {
	private Integer value;
	
	public IntegerValue(String id, Integer value) {
		super(id, String.valueOf(value));
		this.value = value;
	}
	
	@Override
	public Double getDoubleVal() {
		return (value == null) ? null : (double) value;
	}
	
	public Integer getIntegerVal() {
		return (value == null) ? null : value;
	}
	
	public static IntegerValue of(String id, int value) {
		return new IntegerValue(id, value);
	}
	
	public void setValue(Integer value) {
		this.value = value;
	}
	
	@Override
	public ExecVarType getType() {
		return ExecVarType.INTEGER;
	}

}
