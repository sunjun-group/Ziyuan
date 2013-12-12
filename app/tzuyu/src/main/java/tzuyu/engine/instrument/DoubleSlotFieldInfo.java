package tzuyu.engine.instrument;

public abstract class DoubleSlotFieldInfo extends FieldInfo {

  protected DoubleSlotFieldInfo(String name, String signature, int modifiers,
      ClassInfo ci, int idx, int off) {
    super(name, signature, modifiers, ci, idx, off);
  }

  @Override
  public boolean isTwoSlotField() {
    return true;
  }

  @Override
  public int getStorageSize() {
    return 2;
  }

}
