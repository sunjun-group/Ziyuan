package tzuyu.engine.instrument;

import tzuyu.engine.model.TzuYuException;

public class ReferenceFieldInfo extends SingleSlotFieldInfo {

  int init = -1;
  String sInit;

  protected ReferenceFieldInfo(String name, String signature, int modifiers,
      ClassInfo ci, int idx, int off) {
    super(name, signature, modifiers, ci, idx, off);
  }

  @Override
  public Object getValueObject(Fields f) {
    int i = f.getReferenceValue(storageOffset);
    if (i == -1) {
      return null;
    } else {
      // TODO: How to return concrete value for object
    }
    return null;
  }

  @Override
  public void initialize(ElementInfo ei) {
    int ref = init;
    if (sInit != null) {

    }
    ei.getFields().setReferenceValue(storageOffset, ref);

  }

  @Override
  public String valueToString(Fields f) {
    int i = f.getIntValue(storageOffset);
    if (i == -1) {
      return "null";
    } else {
      return toString();
    }
  }

  @Override
  public boolean isArrayField() {
    return ci.isArray;
  }

  @Override
  public boolean isReferenceField() {
    return true;
  }

  @Override
  public void setConstantValue(Object constValue) {
    if (constValue instanceof String) {
      cv = constValue;
      sInit = (String) constValue;
    } else {
      throw new TzuYuException("unsupported reference initialization:"
          + constValue);
    }
  }

}
