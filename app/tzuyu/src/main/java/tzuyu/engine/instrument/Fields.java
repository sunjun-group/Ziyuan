package tzuyu.engine.instrument;

import tzuyu.engine.utils.Misc;
import tzuyu.engine.utils.ObjectList;

public abstract class Fields implements Cloneable {
  protected Object[] fieldAttrs;
  protected Object objectAttr;

  protected Fields() {
  }

  public boolean hasFieldAttr() {
    return fieldAttrs != null;
  }

  public boolean hasFieldAttr(Class<?> attrType) {
    Object[] fa = fieldAttrs;
    if (fa != null) {
      for (int i = 0; i < fa.length; i++) {
        Object a = fa[i];
        if (a != null && ObjectList.containsType(a, attrType)) {
          return true;
        }
      }
    }
    return false;
  }

  public Object getFieldAttr(int fieldOrElementIndex) {
    if (fieldAttrs != null) {
      return fieldAttrs[fieldOrElementIndex];
    }
    return null;
  }

  public abstract int getIntValue(int index);

  public abstract int getReferenceValue(int index);

  public abstract long getLongValue(int index);

  public abstract boolean getBooleanValue(int index);

  public abstract byte getByteValue(int index);

  public abstract char getCharValue(int index);

  public abstract short getShortValue(int index);

  public abstract double getDoubleValue(int index);

  public abstract float getFloatValue(int index);

  public abstract void setIntValue(int index, int newValue);

  public abstract void setReferenceValue(int index, int newValue);

  public abstract void setLongValue(int index, long newValue);

  public abstract void setBooleanValue(int index, boolean newValue);

  public abstract void setByteValue(int index, byte newValue);

  public abstract void setCharValue(int index, char newValue);

  public abstract void setShortValue(int index, short newValue);

  public abstract void setDoubleValue(int index, double newValue);

  public abstract void setFloatValue(int index, float newValue);

  public abstract Fields clone();

  protected Fields cloneFields() {
    try {
      Fields f = (Fields) super.clone();
      if (fieldAttrs != null) {
        f.fieldAttrs = fieldAttrs.clone();
      }
      if (objectAttr != null) {
        f.objectAttr = objectAttr;
      }
      return f;
    } catch (CloneNotSupportedException cnsx) {
      return null;
    }
  }

  protected boolean compareAttrs(Fields f) {
    if (fieldAttrs != null || f.fieldAttrs != null) {
      if (!Misc.compare(fieldAttrs, f.fieldAttrs)) {
        return false;
      }
    }

    if (!ObjectList.equals(objectAttr, f.objectAttr)) {
      return false;
    }

    return true;
  }

  public void copyAttrs(Fields other) {
    if (other.fieldAttrs != null) {
      if (fieldAttrs == null) {
        fieldAttrs = other.fieldAttrs.clone();
      } else {
        System.arraycopy(other.fieldAttrs, 0, fieldAttrs, 0, fieldAttrs.length);
      }
    }

    objectAttr = other.objectAttr;
  }

  public boolean hasObjectAttr(Class<?> attrType) {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean hasObjectAttr() {
    return objectAttr != null;
  }

  public Object getObjectAttr() {
    return objectAttr;
  }

  public void setObjectAttr(Object a) {
    objectAttr = a;

  }

}
