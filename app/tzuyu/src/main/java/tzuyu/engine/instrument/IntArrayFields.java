package tzuyu.engine.instrument;

import tzuyu.engine.iface.TzPrintStream;

import tzuyu.engine.utils.HashData;

public class IntArrayFields extends ArrayFields {
  int[] values;

  public IntArrayFields(int length) {
    values = new int[length];
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
  public int[] asIntArray() {
    return values;
  }

  @Override
  public Object getValues() {
    return values;
  }

  @Override
  public Fields clone() {
    IntArrayFields f = (IntArrayFields) cloneFields();
    f.values = values.clone();
    return f;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof IntArrayFields) {
      IntArrayFields other = (IntArrayFields) o;
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
  public void setIntValue(int pos, int value) {
    values[pos] = value;
  }

  @Override
  public int getIntValue(int pos) {
    return values[pos];
  }

  public void hash(HashData hd) {
    int[] v = values;
    for (int i = 0; i < v.length; i++) {
      hd.add(v[i]);
    }
  }
}
