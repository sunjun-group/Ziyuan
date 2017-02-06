package tzuyu.engine.instrument;

import tzuyu.engine.model.exception.TzRuntimeException;

public class DoubleFieldInfo extends DoubleSlotFieldInfo {

  double init;

  protected DoubleFieldInfo(String name, int modifiers, ClassInfo ci, int idx,
      int off) {
    super(name, "D", modifiers, ci, idx, off);
  }

  @Override
  public Object getValueObject(Fields f) {
    double v = f.getDoubleValue(storageOffset);
    return new Double(v);
  }

  @Override
  public void initialize(ElementInfo ei) {
    ei.getFields().setDoubleValue(storageOffset, init);
  }

  @Override
  public String valueToString(Fields f) {
    double v = f.getDoubleValue(storageOffset);
    return Double.toString(v);
  }

  @Override
  public boolean isFloatingPointField() {
    return true;
  }

  @Override
  public boolean isDoubleField() {
    return true;
  }

  @Override
  public boolean isNumericField() {
    return true;
  }

  @Override
  public void setConstantValue(Object constValue) {
    if (constValue instanceof Double) {
      cv = constValue;
      init = (Double) constValue;
    } else {
      throw new TzRuntimeException("illegal double ConstValue=" + constValue);
    }
  }

}
