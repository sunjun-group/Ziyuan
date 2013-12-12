package tzuyu.engine.model;

public interface FieldInfo {

  public ClassInfo getType();

  public String getName();

  public ClassInfo getDeclaringClass();

  public int getModifiers();

  public Object getValue(Object object);
}
