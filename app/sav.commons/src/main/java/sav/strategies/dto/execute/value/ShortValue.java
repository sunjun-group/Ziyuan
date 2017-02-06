package sav.strategies.dto.execute.value;

public class ShortValue extends PrimitiveValue {
	private Short value;
	
	public ShortValue(String id, Short value) {
		super(id, String.valueOf(value));
		this.value = value;
	}
	
	@Override
	public Double getDoubleVal() {
		return (value == null) ? null : (double) value;
	}
	
	public Short getShortVal() {
		return (value == null) ? null : value;
	}
	
	public static ShortValue of(String id, short value) {
		return new ShortValue(id, value);
	}
	
	@Override
	public ExecVarType getType() {
		return ExecVarType.SHORT;
	}

}
