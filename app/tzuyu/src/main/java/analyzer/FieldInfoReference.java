package analyzer;

import java.lang.reflect.Field;

import tzuyu.engine.model.ClassInfo;
import tzuyu.engine.model.FieldInfo;
import tzuyu.engine.model.TzuYuException;


public class FieldInfoReference implements FieldInfo {

  private ClassInfo parent;
  private String name;
  private int modifier;
  private ClassInfo type;
  private Field field;

  public FieldInfoReference(ClassInfo father, Field field, String name,
      ClassInfo type, int access) {
    this.parent = father;
    this.name = name;
    this.type = type;
    this.field = field;
    this.modifier = access;
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
			throw new TzuYuException(
					"get field value on a type incompatible object");
		} catch (IllegalAccessException e) {
			throw new TzuYuException(
					"get field value on a type incompatible object");
		}
	}
}
