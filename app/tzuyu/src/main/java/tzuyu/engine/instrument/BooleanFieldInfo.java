package tzuyu.engine.instrument;

public class BooleanFieldInfo extends SingleSlotFieldInfo {

  boolean init = false;

  protected BooleanFieldInfo(String name, int modifiers, ClassInfo ci, int idx,
      int off) {
    super(name, "Z", modifiers, ci, idx, off);
  }

  public void setConstantValue(Object constValue) {
    if (constValue instanceof Integer) {
      cv = constValue;
      init = ((Integer) constValue).intValue() == 1;
    } else {
      throw new IllegalArgumentException("illegal boolean ConstValue = "
          + constValue);
    }
  }

  public void initialize(ElementInfo ei) {
    ei.getFields().setBooleanValue(storageOffset, init);
  }

  public boolean isBooleanField() {
    return true;
  }

  public Object getValueObject(Fields f) {
    int i = f.getIntValue(storageOffset);
    return new Boolean(i != 0);
  }

  public String valueToString(Fields f) {
    boolean b = f.getBooleanValue(storageOffset);
    return Boolean.toString(b);
  }
}
