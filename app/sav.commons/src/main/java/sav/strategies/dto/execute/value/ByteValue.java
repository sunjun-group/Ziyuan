package sav.strategies.dto.execute.value;

public class ByteValue extends PrimitiveValue {
	private byte value;
	
	public ByteValue(String id, byte value) {
		super(id, String.valueOf(value));
		this.value = value;
	}
	
	@Override
	public double getDoubleVal() {
		return value;
	}
	
	public byte getByteVal() {
		return value;
	}
	
	public static ByteValue of(String id, byte value) {
		return new ByteValue(id, value);
	}
	
	@Override
	public ExecVarType getType() {
		return ExecVarType.BYTE;
	}

}
