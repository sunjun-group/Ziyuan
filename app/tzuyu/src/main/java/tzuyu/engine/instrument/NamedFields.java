package tzuyu.engine.instrument;

import tzuyu.engine.utils.HashData;
import tzuyu.engine.utils.Types;

/**
 * Value container for non-array classes
 * 
 * @author Spencer Xiao
 * 
 */
public class NamedFields extends Fields {

  protected int[] values;

  public NamedFields(int dataSize) {
    values = new int[dataSize];
  }

  public int[] asFieldSlots() {
    return values;
  }

  public boolean isEqual(Fields o, int off, int len, int otherOff) {
    if (o instanceof NamedFields) {
      NamedFields other = (NamedFields) o;
      int iEnd = off + len;
      int jEnd = otherOff + len;
      int[] v = other.values;

      if (iEnd > values.length || jEnd > v.length) {
        return false;
      }
      for (int i = off, j = otherOff; i < iEnd; i++, j++) {
        if (values[i] != v[j]) {
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
  }

  @Override
  public int getIntValue(int index) {
    return values[index];
  }

  @Override
  public int getReferenceValue(int index) {
    return values[index];
  }

  @Override
  public long getLongValue(int index) {
    return Types.intsToLong(values[index + 1], values[index]);
  }

  @Override
  public boolean getBooleanValue(int index) {
    return Types.intToBoolean(values[index]);
  }

  @Override
  public byte getByteValue(int index) {
    return (byte) values[index];
  }

  @Override
  public char getCharValue(int index) {
    return (char) values[index];
  }

  @Override
  public short getShortValue(int index) {
    return (short) values[index];
  }

  @Override
  public double getDoubleValue(int index) {
    return Types.intsToDouble(values[index + 1], values[index]);
  }

  @Override
  public void setIntValue(int index, int newValue) {
    values[index] = newValue;

  }

  @Override
  public void setReferenceValue(int index, int newValue) {
    values[index] = newValue;
  }

  @Override
  public void setLongValue(int index, long newValue) {
    values[index++] = Types.hiLong(newValue);
    values[index] = Types.loLong(newValue);

  }

  @Override
  public void setBooleanValue(int index, boolean newValue) {
    values[index] = newValue ? 1 : 0;
  }

  @Override
  public void setByteValue(int index, byte newValue) {
    values[index] = newValue;

  }

  @Override
  public void setCharValue(int index, char newValue) {
    values[index] = newValue;

  }

  @Override
  public void setShortValue(int index, short newValue) {
    values[index] = newValue;

  }

  @Override
  public void setDoubleValue(int index, double newValue) {
    values[index++] = Types.hiDouble(newValue);
    values[index] = Types.loDouble(newValue);

  }

  @Override
  public Fields clone() {
    NamedFields f = (NamedFields) cloneFields();
    f.values = values.clone();
    return f;
  }

  @Override
  public float getFloatValue(int index) {
    return Types.intToFloat(values[index]);
  }

  @Override
  public void setFloatValue(int index, float newValue) {
    values[index] = Types.floatToInt(newValue);

  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("NamedFields[");
    sb.append("values=");
    sb.append("[");
    for (int i = 0; i < values.length; i++) {
      if (i != 0) {
        sb.append(",");
      }
      sb.append(values[i]);
    }
    sb.append("],]");
    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof NamedFields) {
      NamedFields other = (NamedFields) o;
      int[] v1 = values;
      int[] v2 = other.values;
      int len = v1.length;
      if (len != v2.length) {
        return false;
      }

      for (int i = 0; i < len; i++) {
        if (v1[i] != v2[i]) {
          return false;
        }
      }
      return super.compareAttrs(other);
    } else {
      return false;
    }
  }

  public void hash(HashData hd) {
    int[] v = values;
    for (int i = 0, l = v.length; i < l; i++) {
      hd.add(v[i]);
    }
  }

  public int size() {
    return values.length;
  }

  public void copyFrom(Fields other) {
    System.arraycopy(((NamedFields) other).values, 0, values, 0, values.length);
    super.copyAttrs(other);
  }

}
