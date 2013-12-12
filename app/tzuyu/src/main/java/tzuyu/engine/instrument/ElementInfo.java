package tzuyu.engine.instrument;

import tzuyu.engine.model.TzuYuException;
import tzuyu.engine.utils.Types;

/**
 * Describe an element of memory containing the field values of a class or an
 * object. In the case of a class, contains the values of the static fields. For
 * an object, contains the values of the object fields.
 * 
 * @author Spencer Xiao
 * 
 */
public abstract class ElementInfo implements Cloneable {
  private static final int ATTR_FIELDS_CHANGED = 0x100000;

  protected ClassInfo ci;
  protected Fields fields;
  protected int attributes;

  protected int objRef;

  protected ElementInfo(ClassInfo c, Fields f) {
    ci = c;
    fields = f;
  }

  protected ElementInfo() {

  }

  public boolean hasRefField(int objRef) {
    return ci.hasRefField(objRef, fields);
  }

  int getAttributes() {
    return attributes;
  }

  public ClassInfo getClassInfo() {
    return ci;
  }

  protected abstract FieldInfo getDeclaredFieldInfo(String cls, String fname);

  protected abstract ElementInfo getElementInfo(ClassInfo ci);

  protected abstract FieldInfo getFieldInfo(String fname);

  protected abstract int getNumberOfFieldsOrElements();

  public boolean hasObjectAttr() {
    return fields.hasObjectAttr();
  }

  public boolean hasObjectAttr(Class<?> attrType) {
    return fields.hasObjectAttr(attrType);
  }

  public Object getObjectAttr() {
    return fields.getObjectAttr();
  }

  public void setObjectAttr(Object a) {
    cloneFields().setObjectAttr(a);
  }

  protected Fields cloneFields() {
    if ((attributes & ATTR_FIELDS_CHANGED) == 0) {
      fields = fields.clone();
      attributes |= ATTR_FIELDS_CHANGED;
    }
    return fields;
  }

  public abstract boolean isObject();

  public Fields getFields() {
    return fields;
  }

  public String getType() {
    return ci.getType();
  }

  public int arrayLength() {
    if (fields instanceof ArrayFields) {
      return ((ArrayFields) fields).arrayLength();
    } else {
      throw new TzuYuException("not an array: " + ci.getName());
    }
  }

  public boolean isStringObject() {
    return ClassInfo.isStringClassInfo(ci);
  }

  public boolean[] asBooleanArray() {
    if (fields instanceof ArrayFields) {
      return ((ArrayFields) fields).asBooleanArray();
    } else {
      throw new TzuYuException("not an array: " + ci.getName());
    }
  }

  public byte[] asByteArray() {
    if (fields instanceof ArrayFields) {
      return ((ArrayFields) fields).asByteArray();
    } else {
      throw new TzuYuException("not an array: " + ci.getName());
    }
  }

  public char[] asCharArray() {
    if (fields instanceof ArrayFields) {
      return ((ArrayFields) fields).asCharArray();
    } else {
      throw new TzuYuException("not an array: " + ci.getName());
    }
  }

  public double[] asDoubleArray() {
    if (fields instanceof ArrayFields) {
      return ((ArrayFields) fields).asDoubleArray();
    } else {
      throw new TzuYuException("not an array: " + ci.getName());
    }
  }

  public float[] asFloatArray() {
    if (fields instanceof ArrayFields) {
      return ((ArrayFields) fields).asFloatArray();
    } else {
      throw new TzuYuException("not an array: " + ci.getName());
    }
  }

  public int[] asIntArray() {
    if (fields instanceof ArrayFields) {
      return ((ArrayFields) fields).asIntArray();
    } else {
      throw new TzuYuException("not an array: " + ci.getName());
    }
  }

  public long[] asLongArray() {
    if (fields instanceof ArrayFields) {
      return ((ArrayFields) fields).asLongArray();
    } else {
      throw new TzuYuException("not an array: " + ci.getName());
    }
  }

  public int[] asReferenceArray() {
    if (fields instanceof ArrayFields) {
      return ((ArrayFields) fields).asReferenceArray();
    } else {
      throw new TzuYuException("not an array: " + ci.getName());
    }
  }

  public short[] asShortArray() {
    if (fields instanceof ArrayFields) {
      return ((ArrayFields) fields).asShortArray();
    } else {
      throw new TzuYuException("not an array: " + ci.getName());
    }
  }

  public String asString() {
    throw new TzuYuException("not a String object: " + this);
  }

  public boolean equalsString(String s) {
    throw new TzuYuException("not a String object: " + this);
  }

  public boolean isBoxedObject() {
    return false;
  }

  public boolean isArray() {
    return ci.isArray();
  }

  public boolean isCharArray() {
    return (fields instanceof CharArrayFields);
  }

  public boolean isNull() {
    return objRef == -1;
  }

  public String getArrayType() {
    if (!ci.isArray()) {
      throw new TzuYuException("object is not an array");
    }
    return Types.getArrayElementType(ci.getType());
  }
}
