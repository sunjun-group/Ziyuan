package analyzer;

import java.lang.reflect.Constructor;

import tzuyu.engine.model.ClassInfo;
import tzuyu.engine.model.ConstructorInfo;


public class ConstructorInfoImpl implements ConstructorInfo {

  private ClassInfo parent;
  private String name;
  private int modifiers;
  private ClassInfo[] parameterTypes;
  private ClassInfo[] exceptionTypes;
  private Constructor<?> constructor;

  public ConstructorInfoImpl(ClassInfo father, Constructor<?> ctor, String nm,
      ClassInfo[] inputTypes, ClassInfo[] exceptions, int access) {
    this.parent = father;
    this.constructor = ctor;
    this.name = nm;
    this.parameterTypes = inputTypes;
    this.exceptionTypes = exceptions;
    this.modifiers = access;
  }

  @Override
  public ClassInfo getDeclaringClass() {
    return parent;
  }

  @Override
  public ClassInfo[] getParameterTypes() {
    return parameterTypes;
  }

  @Override
  public int getModifiers() {
    return modifiers;
  }

  @Override
  public ClassInfo[] getExceptionTypes() {
    return exceptionTypes;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Constructor<?> getConstructor() {
    return constructor;
  }

}
