package tzuyu.engine.instrument;

import tzuyu.engine.model.exception.TzRuntimeException;

public class ByteFieldInfo extends SingleSlotFieldInfo {

  byte init;

  protected ByteFieldInfo(String name, int modifiers, ClassInfo ci, int idx,
      int off) {
    super(name, "B", modifiers, ci, idx, off);
  }

  @Override
  public Object getValueObject(Fields f) {
    byte v = f.getByteValue(storageOffset);
    return new Byte((byte) v);
  }

  @Override
  public void initialize(ElementInfo ei) {
    ei.getFields().setByteValue(storageOffset, init);
  }

  @Override
  public String valueToString(Fields f) {
    byte v = f.getByteValue(storageOffset);
    return Byte.toString(v);
  }

  @Override
  public boolean isNumericField() {
    return true;
  }

  @Override
  public boolean isByteField() {
    return true;
  }

  @Override
  public void setConstantValue(Object constValue) {
    if (constValue instanceof Integer) {
      cv = constValue;
      init = ((Integer) constValue).byteValue();
    } else {
      throw new TzRuntimeException("illegal byte ConstValue=" + constValue);
    }
  }

}
