package sav.strategies.dto.execute.value;

public class ShortValue extends PrimitiveValue {
	private short value;
	
	public ShortValue(String id, short value) {
		super(id, String.valueOf(value));
		this.value = value;
	}
	
	@Override
	public double getDoubleVal() {
		return value;
	}
	
	public short getShortVal() {
		return value;
	}
	
	public static ShortValue of(String id, short value) {
		return new ShortValue(id, value);
	}
	
	@Override
	public ExecVarType getType() {
		return ExecVarType.SHORT;
	}

}
