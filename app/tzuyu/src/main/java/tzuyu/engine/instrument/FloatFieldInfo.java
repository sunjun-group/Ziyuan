package tzuyu.engine.instrument;

import tzuyu.engine.model.exception.TzRuntimeException;

public class FloatFieldInfo extends SingleSlotFieldInfo {

  float init;

  protected FloatFieldInfo(String name, int modifiers, ClassInfo ci, int idx,
      int off) {
    super(name, "F", modifiers, ci, idx, off);
  }

  @Override
  public Object getValueObject(Fields f) {
    float v = f.getFloatValue(storageOffset);
    return new Float(v);
  }

  @Override
  public void initialize(ElementInfo ei) {
    ei.getFields().setFloatValue(storageOffset, init);
  }

  @Override
  public String valueToString(Fields f) {
    float i = f.getFloatValue(storageOffset);
    return Float.toString(i);
  }

  @Override
  public boolean isFloatField() {
    return true;
  }

  @Override
  public boolean isNumericField() {
    return true;
  }

  @Override
  public void setConstantValue(Object constValue) {
    if (constValue instanceof Float) {
      cv = constValue;
      init = (Float) constValue;
    } else {
      throw new TzRuntimeException("illegal float ConstValue=" + constValue);
    }
  }

}
