package tzuyu.engine.instrument;

import tzuyu.engine.iface.IPrintStream;

import tzuyu.engine.utils.HashData;

public class BooleanArrayFields extends ArrayFields {

  boolean[] values;

  public BooleanArrayFields(int length) {
    values = new boolean[length];
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof BooleanArrayFields) {
      BooleanArrayFields other = (BooleanArrayFields) o;
      boolean[] v = values;
      boolean[] vOther = other.values;
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
    ps.print(values[index] ? 't' : 'f');
  }

  @Override
  public boolean[] asBooleanArray() {
    return values;
  }

  @Override
  public Object getValues() {
    return values;
  }

  @Override
  public BooleanArrayFields clone() {
    BooleanArrayFields f = (BooleanArrayFields) cloneFields();
    f.values = values.clone();
    return f;
  }

  @Override
  public boolean getBooleanValue(int pos) {
    return values[pos];
  }

  @Override
  public void setBooleanValue(int pos, boolean value) {
    values[pos] = value;
  }

  public void hash(HashData hd) {
    boolean[] v = values;
    for (int i = 0; i < v.length; i++) {
      hd.add(v[i]);
    }
  }

}
