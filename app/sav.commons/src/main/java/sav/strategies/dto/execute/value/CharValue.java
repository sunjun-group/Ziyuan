package sav.strategies.dto.execute.value;

public class CharValue extends PrimitiveValue {
	private Character value;
	
	public CharValue(String id, Character value) {
		super(id, String.valueOf(value));
		this.value = value;
	}
	
	@Override
	public Double getDoubleVal() {
		return getDoubleVal(value);
	}
	
	public static Double getDoubleVal(Character value) {
		return (value == null) ? null : (double) value;
	}
	
	public Character getCharVal() {
		return (value == null) ? null : value;
	}
	
	public static CharValue of(String id, char value) {
		return new CharValue(id, value);
	}
	
	public void setValue(Character value) {
		this.value = value;
	}
	
	@Override
	public ExecVarType getType() {
		return ExecVarType.CHAR;
	}
	
}
