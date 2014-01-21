package analyzer;

import java.lang.reflect.Field;

import tzuyu.engine.model.ClassInfo;
import tzuyu.engine.model.FieldInfo;
import tzuyu.engine.model.TzuYuException;

public class FieldInfoPrimitive implements FieldInfo {

	private ClassInfo parent;
	private String name;
	private ClassInfo type;
	private int modifier;
	private Field field;

	public FieldInfoPrimitive(ClassInfo father, Field field, String name,
			ClassInfo type, int access) {
		this.parent = father;
		this.name = name;
		this.type = type;
		this.field = field;
		this.modifier = access;
	}

	public ClassInfo getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public ClassInfo getDeclaringClass() {
		return parent;
	}

	public int getModifiers() {
		return modifier;
	}

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
