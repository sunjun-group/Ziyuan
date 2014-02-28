package tzuyu.engine.instrument;

import tzuyu.engine.iface.TzPrintStream;

import tzuyu.engine.utils.HashData;

public class FloatArrayFields extends ArrayFields {

  float[] values;

  public FloatArrayFields(int length) {
    values = new float[length];
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
  public float[] asFloatArray() {
    return values;
  }

  @Override
  public Object getValues() {
    return values;
  }

  @Override
  public Fields clone() {
    FloatArrayFields f = (FloatArrayFields) cloneFields();
    f.values = values.clone();
    return f;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof FloatArrayFields) {
      FloatArrayFields other = (FloatArrayFields) o;
      float[] v = values;
      float[] vOther = other.values;
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
  public void setFloatValue(int pos, float value) {
    values[pos] = value;
  }

  @Override
  public float getFloatValue(int pos) {
    return values[pos];
  }

  public void hash(HashData hd) {
    float[] v = values;
    for (int i = 0; i < v.length; i++) {
      hd.add(v[i]);
    }
  }
}
