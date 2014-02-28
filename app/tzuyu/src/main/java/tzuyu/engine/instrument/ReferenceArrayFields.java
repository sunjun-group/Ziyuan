package tzuyu.engine.instrument;

import tzuyu.engine.iface.TzPrintStream;
import tzuyu.engine.utils.HashData;

public class ReferenceArrayFields extends ArrayFields {
	int[] values;

	public ReferenceArrayFields(int length) {
		values = new int[length];
		for (int i = 0; i < length; i++) {
			values[i] = -1;
		}
	}

	@Override
	public int arrayLength() {
		return values.length;
	}

	@Override
	public boolean isReferenceArray() {
		return true;
	}

	@Override
	protected void printValue(TzPrintStream ps, int index) {
		ps.print(values[index]);
	}

	@Override
	public int[] asReferenceArray() {
		return values;
	}

	@Override
	public Object getValues() {
		return values;
	}

	@Override
	public Fields clone() {
		ReferenceArrayFields f = (ReferenceArrayFields) cloneFields();
		f.values = values.clone();
		return f;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ReferenceArrayFields) {
			ReferenceArrayFields other = (ReferenceArrayFields) o;
			int[] v = values;
			int[] vOther = other.values;
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
	public void setReferenceValue(int pos, int value) {
		values[pos] = value;
	}

	@Override
	public int getReferenceValue(int pos) {
		return values[pos];
	}

	public void hash(HashData hd) {
		int[] v = values;
		for (int i = 0; i < v.length; i++) {
			hd.add(v[i]);
		}
	}

}
