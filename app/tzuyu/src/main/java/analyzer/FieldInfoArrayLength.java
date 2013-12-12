package analyzer;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;

import tzuyu.engine.model.ClassInfo;
import tzuyu.engine.model.FieldInfo;


public class FieldInfoArrayLength implements FieldInfo {

  private ClassInfo parent;
  private ClassInfo type;
  private String name;

  public FieldInfoArrayLength(ClassInfo parent) {
    this.parent = parent;
    this.name = "length";
  }

  @Override
  public ClassInfo getType() {
    return type;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public ClassInfo getDeclaringClass() {
    return parent;
  }

  @Override
  public int getModifiers() {
    return Modifier.PUBLIC;
  }

  @Override
  public Object getValue(Object object) {
    return Array.getLength(object);
  }

}
