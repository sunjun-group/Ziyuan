package tzuyu.engine.instrument;

import tzuyu.engine.model.exception.TzRuntimeException;

public class ShortFieldInfo extends SingleSlotFieldInfo {

  short init;

  protected ShortFieldInfo(String name, int modifiers, ClassInfo ci, int idx,
      int off) {
    super(name, "S", modifiers, ci, idx, off);
  }

  @Override
  public Object getValueObject(Fields f) {
    short v = f.getShortValue(storageOffset);
    return new Short(v);
  }

  @Override
  public void initialize(ElementInfo ei) {
    ei.getFields().setShortValue(storageOffset, init);
  }

  @Override
  public String valueToString(Fields f) {
    short v = f.getShortValue(storageOffset);
    return Short.toString(v);
  }

  @Override
  public boolean isShortField() {
    return true;
  }

  @Override
  public boolean isNumericField() {
    return true;
  }

  @Override
  public void setConstantValue(Object constValue) {
    if (constValue instanceof Integer) {
      cv = constValue;
      init = ((Integer) constValue).shortValue();
    } else {
      throw new TzRuntimeException("illegal short ConstValue=" + constValue);
    }
  }
}
