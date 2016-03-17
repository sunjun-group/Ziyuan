package sav.strategies.dto.execute.value;

public class CharValue extends PrimitiveValue {
	private char value;
	
	public CharValue(String id, char value) {
		super(id, String.valueOf(value));
		this.value = value;
	}
	
	@Override
	public double getDoubleVal() {
		return value;
	}
	
	public char getCharVal() {
		return value;
	}
	
	public static CharValue of(String id, char value) {
		return new CharValue(id, value);
	}
	
	@Override
	public ExecVarType getType() {
		return ExecVarType.CHAR;
	}
	
}
