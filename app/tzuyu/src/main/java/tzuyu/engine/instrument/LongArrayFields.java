package tzuyu.engine.instrument;

import sav.common.core.iface.IPrintStream;

import tzuyu.engine.utils.HashData;

public class LongArrayFields extends ArrayFields {
  long[] values;

  public LongArrayFields(int length) {
    values = new long[length];
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
  public long[] asLongArray() {
    return values;
  }

  @Override
  public Object getValues() {
    return values;
  }

  @Override
  public Fields clone() {
    LongArrayFields f = (LongArrayFields) cloneFields();
    f.values = values.clone();
    return f;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof LongArrayFields) {
      LongArrayFields other = (LongArrayFields) o;
      long[] v = values;
      long[] vOther = other.values;
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
  public void setLongValue(int pos, long value) {
    values[pos] = value;
  }

  @Override
  public long getLongValue(int pos) {
    return values[pos];
  }

  public void hash(HashData hd) {
    long[] v = values;
    for (int i = 0; i < v.length; i++) {
      hd.add(v[i]);
    }
  }
}
