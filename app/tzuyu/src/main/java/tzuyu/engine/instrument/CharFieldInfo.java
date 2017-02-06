package tzuyu.engine.instrument;

import tzuyu.engine.model.exception.TzRuntimeException;

public class CharFieldInfo extends SingleSlotFieldInfo {

  char init;

  protected CharFieldInfo(String name, int modifiers, ClassInfo ci, int idx,
      int off) {
    super(name, "C", modifiers, ci, idx, off);
  }

  @Override
  public Object getValueObject(Fields f) {
    int v = f.getIntValue(storageOffset);
    return new Character((char) v);
  }

  @Override
  public void initialize(ElementInfo ei) {
    ei.getFields().setCharValue(storageOffset, init);
  }

  @Override
  public String valueToString(Fields f) {
    char[] buf = new char[1];
    buf[0] = f.getCharValue(storageOffset);
    return new String(buf);
  }

  @Override
  public boolean isCharField() {
    return true;
  }

  @Override
  public void setConstantValue(Object constValue) {
    if (constValue instanceof Integer) {
      cv = constValue;
      init = (char) ((Integer) constValue).shortValue();
    } else {
      throw new TzRuntimeException("illegal char ConstValue=" + constValue);
    }
  }
}
