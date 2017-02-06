package tzuyu.engine.model;

import java.lang.reflect.Constructor;

public interface ConstructorInfo {

  public ClassInfo getDeclaringClass();

  public ClassInfo[] getParameterTypes();

  public int getModifiers();

  public ClassInfo[] getExceptionTypes();

  public String getName();

  public Constructor<?> getConstructor();

}
