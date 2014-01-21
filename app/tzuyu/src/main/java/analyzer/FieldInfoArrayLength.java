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
		return Modifier.PUBLIC;
	}

	public Object getValue(Object object) {
		return Array.getLength(object);
	}

}
