package tzuyu.engine.instrument;

import sav.common.core.iface.IPrintStream;

import tzuyu.engine.utils.HashData;

/**
 * Element values for byte[] objects
 * 
 * @author Spencer Xiao
 * 
 */
public class ByteArrayFields extends ArrayFields {

	byte[] values;

	public ByteArrayFields(int length) {
		values = new byte[length];
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ByteArrayFields) {
			ByteArrayFields other = (ByteArrayFields) o;
			byte[] v = values;
			byte[] vOther = other.values;
			if (v.length != vOther.length) {
				return false;
			}

			for (int i = 0; i < v.length; i++) {
				if (v[i] != vOther[i]) {
					return false;
				}
			}

			return compareAttrs(other);
		} else {
			return false;
		}
	}

	@Override
	public int arrayLength() {
		return values.length;
	}

	@Override
	protected void printValue(IPrintStream ps, int index) {
		ps.print(values[index]);
	}

	@Override
	public Object getValues() {
		return values;
	}

	@Override
	public byte[] asByteArray() {
		return values;
	}

	@Override
	public void setByteValue(int pos, byte value) {
		values[pos] = value;
	}

	@Override
	public byte getByteValue(int pos) {
		return values[pos];
	}

	public void hash(HashData hd) {
		byte[] v = values;
		for (int i = 0; i < v.length; i++) {
			hd.add(v[i]);
		}
	}

	@Override
	public ByteArrayFields clone() {
		ByteArrayFields f = (ByteArrayFields) cloneFields();
		f.values = values.clone();
		return f;
	}

}
