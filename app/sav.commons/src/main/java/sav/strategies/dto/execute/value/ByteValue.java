package sav.strategies.dto.execute.value;

public class ByteValue extends PrimitiveValue {
	private Byte value;
	
	public ByteValue(String id, Byte value) {
		super(id, String.valueOf(value));
		this.value = value;
	}
	
	@Override
	public Double getDoubleVal() {
		return (value == null) ? null : (double) value;
	}
	
	public Byte getByteVal() {
		return (value == null) ? null : value;
	}
	
	public static ByteValue of(String id, byte value) {
		return new ByteValue(id, value);
	}
	
	@Override
	public ExecVarType getType() {
		return ExecVarType.BYTE;
	}

}
