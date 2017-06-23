package tzuyu.engine.instrument;

public abstract class SingleSlotFieldInfo extends FieldInfo {

  protected SingleSlotFieldInfo(String name, String signature, int modifiers,
      ClassInfo ci, int idx, int off) {
    super(name, signature, modifiers, ci, idx, off);
  }

  @Override
  public boolean isOneSlotField() {
    return true;
  }

}
