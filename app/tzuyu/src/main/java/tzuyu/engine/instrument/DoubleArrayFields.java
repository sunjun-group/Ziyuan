package tzuyu.engine.instrument;

import sav.common.core.iface.IPrintStream;

import tzuyu.engine.utils.HashData;

public class DoubleArrayFields extends ArrayFields {

  double[] values;

  public DoubleArrayFields(int length) {

  }

  @Override
  public int arrayLength() {
    return values.length;
  }

  @Override
  public double[] asDoubleArray() {
    return values;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof CharArrayFields) {
      DoubleArrayFields other = (DoubleArrayFields) o;
      double[] v = values;
      double[] vOther = other.values;
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
  protected void printValue(IPrintStream ps, int index) {
    ps.print(values[index]);

  }

  @Override
  public Object getValues() {
    return values;
  }

  @Override
  public Fields clone() {
    DoubleArrayFields f = (DoubleArrayFields) cloneFields();
    f.values = values.clone();
    return f;
  }

  @Override
  public double getDoubleValue(int pos) {
    return values[pos];
  }

  @Override
  public void setDoubleValue(int pos, double value) {
    values[pos] = value;
  }

  public void hash(HashData hd) {
    double[] v = values;

    for (int i = 0; i < v.length; i++) {
      hd.add(v[i]);
    }
  }

}
