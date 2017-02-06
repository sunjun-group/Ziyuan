package tzuyu.engine.model;

import java.lang.reflect.Method;

public interface MethodInfo {
  public ClassInfo getDeclaringClass();

  public String getName();

  public int getModifiers();

  public ClassInfo getReturnType();

  public ClassInfo[] getParameterTypes();

  public String[] getParemeterNames();

  public ClassInfo[] getExceptionTypes();

  public Method getMethod();
}
