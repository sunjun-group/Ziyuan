package analyzer;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.TzConfiguration;
import tzuyu.engine.model.ArtFieldInfo;
import tzuyu.engine.model.ClassInfo;
import tzuyu.engine.model.ConstructorInfo;
import tzuyu.engine.model.FieldInfo;
import tzuyu.engine.model.MethodInfo;
import tzuyu.engine.model.ObjectInfo;

public class ClassInfoPrimitive extends ClassInfo {
	private Class<?> type;

	public ClassInfoPrimitive(Class<?> name) {
		this.type = name;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ClassInfoPrimitive)) {
			return false;
		}

		ClassInfoPrimitive cip = (ClassInfoPrimitive) o;

		return this.type.equals(cip.type);
	}

	@Override
	public int hashCode() {
		return type.hashCode();
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	@Override
	public FieldInfo[] getDeclaredFields() {
		return new FieldInfo[0];
	}

	@Override
	public FieldInfo getDeclaredField(String name) {
		return null;
	}

	@Override
	public MethodInfo[] getDeclaredMethods() {
		return new MethodInfo[0];
	}

	@Override
	public ClassInfo[] getInterfaces() {
		return new ClassInfo[0];
	}

	@Override
	public ClassInfo getSuperClass() {
		return null;
	}

	@Override
	public ClassInfo[] getInnerClasses() {
		return new ClassInfoArray[0];
	}

	@Override
	public ConstructorInfo[] getConstructors() {
		return new ConstructorInfo[0];
	}

	@Override
	public ClassInfo getComponentType() {
		return null;
	}

	@Override
	public boolean isArray() {
		return false;
	}

	@Override
	public boolean isPrimitive() {
		return true;
	}

	@Override
	public boolean isInterface() {
		return false;
	}

	@Override
	public ObjectInfo cloneMockup(Object object, int level, TzConfiguration config) {
		if (level > config.getClassMaxDepth()) {
			return null;
		} else {
			return new ObjectInfoPrimitive(this, level, object);
		}
	}

	@Override
	public List<ArtFieldInfo> getFieldsOnLevel(ArtFieldInfo parent,
			FieldInfo field, int level) {
		List<ArtFieldInfo> fields = new ArrayList<ArtFieldInfo>();
		if (level == 0) {
			fields.add(new ArtFieldInfo(parent, field, this));
		}

		return fields;
	}

}
