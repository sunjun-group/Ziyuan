package analyzer;

import java.lang.reflect.Field;

import tzuyu.engine.model.ClassInfo;
import tzuyu.engine.model.FieldInfo;
import tzuyu.engine.model.TzuYuException;


public class FieldInfoArray implements FieldInfo {

  private ClassInfo type;
  private String name;
  private ClassInfo parent;
  private int modifier;
  private Field field;

  public FieldInfoArray(ClassInfo father, Field field, String nm,
      ClassInfo type, int access) {
    this.parent = father;
    this.type = type;
    this.modifier = access;
    this.field = field;
    this.name = nm;
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
    return modifier;
  }

  @Override
  public Object getValue(Object object) {
    try {
      return field.get(object);
    } catch (IllegalArgumentException e) {
    	 throw new TzuYuException("get field value on a type incompatible object");
    } catch (IllegalAccessException e) {
    	 throw new TzuYuException("get field value on a type incompatible object");
	}
  }

}
