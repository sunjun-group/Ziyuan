package tzuyu.engine.instrument;

import java.lang.reflect.Modifier;

import tzuyu.engine.utils.Types;

public abstract class FieldInfo extends InfoObject {
  protected final String name;

  /**
   * Fully qualified type name as "int", "x.Y[]"
   */
  protected String type;
  /**
   * "I", [Lx/Y;" etc.
   */
  protected final String signature;
  protected int storageSize;

  /**
   * The class this field belongs to.
   */
  protected final ClassInfo ci;

  /**
   * Declaration ordinal
   */
  protected final int fieldIndex;

  /**
   * Where in the corresponding Fields object do we store the value.
   */
  protected final int storageOffset;

  /**
   * optional initializer for this field, can't be final because it is set from
   * classfile field_info attributes(i.e., after construction).
   */
  protected Object cv;

  /**
	 * 
	 */
  protected String genericSignature;

  protected int modifiers;

  public static FieldInfo create(
      ClassInfo ci, String name, String signature, int modifiers, int idx,
      int off) {

    switch (signature.charAt(0)) {
    case 'Z':
      return new BooleanFieldInfo(name, modifiers, ci, idx, off);
    case 'B':
      return new ByteFieldInfo(name, modifiers, ci, idx, off);
    case 'S':
      return new ShortFieldInfo(name, modifiers, ci, idx, off);
    case 'C':
      return new CharFieldInfo(name, modifiers, ci, idx, off);
    case 'I':
      return new IntergerFieldInfo(name, modifiers, ci, idx, off);
    case 'J':
      return new LongFieldInfo(name, modifiers, ci, idx, off);
    case 'F':
      return new FloatFieldInfo(name, modifiers, ci, idx, off);
    case 'D':
      return new DoubleFieldInfo(name, modifiers, ci, idx, off);
    default:
      return new ReferenceFieldInfo(name, signature, modifiers, ci, idx, off);
    }
  }

  protected FieldInfo(String name, String signature, int modifiers,
      ClassInfo ci, int idx, int off) {
    this.name = name;
    this.signature = signature;
    this.ci = ci;
    this.fieldIndex = idx;
    this.storageOffset = off;
    this.modifiers = modifiers;
  }

  public String getName() {
    return name;
  }

  public int getStorageSize() {
    return 1;
  }

  public String getType() {
    if (type == null) {
      type = Types.getTypeName(signature);
    }
    return type;
  }

  public byte getTypeCode() {
    return Types.getTypeCode(signature);
  }

  public String getGenericSignature() {
    return genericSignature;
  }

  public void setGenericSignature(String sig) {
    genericSignature = sig;
  }

  public ClassInfo getTypeClassInfo() {
    return ClassInfo.getResolvedClassInfo(getType());
  }

  public int getModifiers() {
    return modifiers;
  }

  public int getFieldIndex() {
    return fieldIndex;
  }

  @Override
  public ClassInfo getClassInfo() {
    return ci;
  }

  public Object getConstantValue() {
    return cv;
  }

  public boolean isOneSlotField() {
    return false;
  }

  public boolean isTwoSlotField() {
    return false;
  }

  public boolean isCharField() {
    return false;
  }

  public boolean isByteField() {
    return false;
  }

  public boolean isShortField() {
    return false;
  }

  public boolean isIntField() {
    return false;
  }

  public boolean isLongField() {
    return false;
  }

  public boolean isFloatField() {
    return false;
  }

  public boolean isDoubleField() {
    return false;
  }

  public boolean isNumericField() {
    return false;
  }

  public boolean isFloatingPointField() {
    return false;
  }

  public boolean isReferenceField() {
    return false;
  }

  public boolean isArrayField() {
    return false;
  }

  public boolean isStatic() {
    return (modifiers & Modifier.STATIC) != 0;
  }

  public boolean isFinal() {
    return (modifiers & Modifier.FINAL) != 0;
  }

  public boolean isVolatile() {
    return (modifiers & Modifier.VOLATILE) != 0;
  }

  public boolean isTransient() {
    return (modifiers & Modifier.TRANSIENT) != 0;
  }

  public boolean isPublic() {
    return (modifiers & Modifier.PUBLIC) != 0;
  }

  public abstract Object getValueObject(Fields data);

  public abstract void initialize(ElementInfo ei);

  public abstract String valueToString(Fields f);

  public void setConstantValue(Object constValue) {
    cv = constValue;
  }
}
