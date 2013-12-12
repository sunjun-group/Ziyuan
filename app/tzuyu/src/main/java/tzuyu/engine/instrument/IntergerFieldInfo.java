package tzuyu.engine.instrument;

import tzuyu.engine.model.TzuYuException;

public class IntergerFieldInfo extends SingleSlotFieldInfo {

  int init;

  protected IntergerFieldInfo(String name, int modifiers, ClassInfo ci,
      int idx, int off) {
    super(name, "I", modifiers, ci, idx, off);
  }

  @Override
  public Object getValueObject(Fields f) {
    int i = f.getIntValue(storageOffset);
    return new Integer(i);
  }

  @Override
  public void initialize(ElementInfo ei) {
    ei.getFields().setIntValue(storageOffset, init);
  }

  @Override
  public String valueToString(Fields f) {
    int i = f.getIntValue(storageOffset);
    return Integer.toString(i);
  }

  @Override
  public boolean isIntField() {
    return true;
  }

  @Override
  public boolean isNumericField() {
    return true;
  }

  public void setConstantValue(Object constValue) {
    if (constValue instanceof Integer) {
      cv = constValue;
      init = (Integer) constValue;
    } else {
      throw new TzuYuException("illegal int ConstValue=" + constValue);
    }
  }
}
