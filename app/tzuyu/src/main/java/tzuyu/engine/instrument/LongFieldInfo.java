package tzuyu.engine.instrument;

import tzuyu.engine.model.TzuYuException;

public class LongFieldInfo extends DoubleSlotFieldInfo {

  long init;

  protected LongFieldInfo(String name, int modifiers, ClassInfo ci, int idx,
      int off) {
    super(name, "J", modifiers, ci, idx, off);
  }

  @Override
  public void setConstantValue(Object constValue) {
    if (constValue instanceof Long) {
      cv = constValue;
      init = (Long) constValue;
    } else {
      throw new TzuYuException("illegal long ConstValue=" + constValue);
    }
  }

  @Override
  public Object getValueObject(Fields f) {
    long v = f.getLongValue(storageOffset);
    return new Long(v);
  }

  @Override
  public void initialize(ElementInfo ei) {
    ei.getFields().setLongValue(storageOffset, init);

  }

  @Override
  public String valueToString(Fields f) {
    long v = f.getLongValue(storageOffset);
    return Long.toString(v);
  }

  @Override
  public boolean isNumericField() {
    return true;
  }

  @Override
  public boolean isLongField() {
    return true;
  }

}
