package tzuyu.engine.instrument;

import tzuyu.engine.iface.IPrintStream;

import tzuyu.engine.utils.HashData;

public class ShortArrayFields extends ArrayFields {
  short[] values;

  public ShortArrayFields(int length) {
    values = new short[length];
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
  public short[] asShortArray() {
    return values;
  }

  @Override
  public Object getValues() {
    return values;
  }

  @Override
  public Fields clone() {
    ShortArrayFields f = (ShortArrayFields) cloneFields();
    f.values = values.clone();
    return f;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof ShortArrayFields) {
      ShortArrayFields other = (ShortArrayFields) o;
      short[] v = values;
      short[] vOther = other.values;
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
  public void setShortValue(int pos, short value) {
    values[pos] = value;
  }

  @Override
  public short getShortValue(int pos) {
    return values[pos];
  }

  public void hash(HashData hd) {
    short[] v = values;
    for (int i = 0; i < v.length; i++) {
      hd.add(v[i]);
    }
  }
}
