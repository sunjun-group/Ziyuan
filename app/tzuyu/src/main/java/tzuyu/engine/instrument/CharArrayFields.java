package tzuyu.engine.instrument;

import tzuyu.engine.iface.TzPrintStream;
import tzuyu.engine.utils.HashData;

public class CharArrayFields extends ArrayFields {

	char[] values;

	public CharArrayFields(int length) {
		values = new char[length];
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CharArrayFields) {
			CharArrayFields other = (CharArrayFields) o;
			char[] v = values;
			char[] vOther = other.values;
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
	protected void printValue(TzPrintStream ps, int index) {
		ps.print(values[index]);
	}

	@Override
	public char[] asCharArray() {
		return values;
	}

	@Override
	public char[] asCharArray(int offset, int length) {
		char[] result = new char[length];
		System.arraycopy(values, offset, result, 0, length);
		return result;
	}

	@Override
	public Object getValues() {
		return values;
	}

	@Override
	public Fields clone() {
		CharArrayFields f = (CharArrayFields) cloneFields();
		f.values = values.clone();
		return f;
	}

	@Override
	public char getCharValue(int pos) {
		return values[pos];
	}

	@Override
	public void setCharValue(int pos, char value) {
		values[pos] = value;
	}

	public void setCharValues(char[] v) {
		System.arraycopy(v, 0, values, 0, v.length);
	}

	public String asString(int offset, int length) {
		return new String(values, offset, length);
	}

	public boolean equals(int offset, int length, String s) {
		char[] v = values;
		if (offset + length > v.length) {
			return false;
		}

		for (int i = offset, j = 0; j < length; i++, j++) {
			if (v[i] != s.charAt(j)) {
				return false;
			}
		}

		return true;
	}

	public void hash(HashData hd) {
		char[] v = values;
		for (int i = 0; i < v.length; i++) {
			hd.add(v[i]);
		}
	}

}
