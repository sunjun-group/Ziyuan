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
import tzuyu.engine.model.exception.TzRuntimeException;
import tzuyu.engine.utils.Assert;
import tzuyu.engine.utils.ReflectionUtils;

public class ClassInfoReference extends ClassInfo {

	private Class<?> type;
	private ClassInfo superClass;
	private ClassInfo[] interfaces;
	private ClassInfo[] innerClasses;

	private FieldInfo[] declaredFields;
	private MethodInfo[] declaredMethods;
	private ConstructorInfo[] constructors;
	private boolean isInitialized;

	public ClassInfoReference(Class<?> nm) {
		this.type = nm;
		isInitialized = false;
	}

	public void initialize(ClassInfo sc, ClassInfo[] ins, ClassInfo[] inners,
			FieldInfo[] fields, MethodInfo[] methods, ConstructorInfo[] ctors) {
		Assert.assertTrue(!isInitialized, "try to initialize an initialized object");
		if (!isInitialized) {
			isInitialized = true;
			this.superClass = sc;
			this.interfaces = ins;
			this.innerClasses = inners;
			this.declaredFields = fields;
			this.declaredMethods = methods;
			this.constructors = ctors;
		} 
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ClassInfoReference)) {
			return false;
		}

		ClassInfoReference cir = (ClassInfoReference) o;

		return this.type.equals(cir.type);
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
		return declaredFields;
	}

	@Override
	public FieldInfo getDeclaredField(String name) {
		for (FieldInfo field : declaredFields) {
			if (field.getName().equals(name)) {
				return field;
			}
		}

		return null;
	}

	@Override
	public MethodInfo[] getDeclaredMethods() {
		return declaredMethods;
	}

	@Override
	public ClassInfo[] getInterfaces() {
		return interfaces;
	}

	@Override
	public ClassInfo getSuperClass() {
		return superClass;
	}

	@Override
	public ClassInfo[] getInnerClasses() {
		return innerClasses;
	}

	@Override
	public ConstructorInfo[] getConstructors() {
		return constructors;
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
		return false;
	}

	@Override
	public boolean isInterface() {
		return type.isInterface();
	}

	@Override
	public ObjectInfo cloneMockup(Object object, int level, TzConfiguration config) {
		if (level > config.getClassMaxDepth()) {
			return null;
		}

		if (object == null) {
			List<ObjectInfo> innerObjects = new ArrayList<ObjectInfo>();
			// Use all the fields, not just the fields declared in this class
			FieldInfo[] allFields = this.getFields();
			for (FieldInfo field : allFields) {
				// for null objects, the fields objects are also null
				ObjectInfo inner = field.getType().cloneMockup(null, level + 1, config);
				if (inner != null) {
					innerObjects.add(inner);
				}
			}

			return new ObjectInfoReference(this, level, innerObjects, true);

		} else if (!ReflectionUtils.canBeUsedAs(object.getClass(), type)) {
			throw new TzRuntimeException("try to clone imcompatable object");
		} else {
			List<ObjectInfo> innerObjects = new ArrayList<ObjectInfo>();
			// Use all the fields, not just the fields declared in this class
			FieldInfo[] allFields = this.getFields();
			for (FieldInfo field : allFields) {
				Object fieldValue = field.getValue(object);
				ObjectInfo inner = field.getType().cloneMockup(fieldValue,
						level + 1, config);
				if (inner != null) {
					innerObjects.add(inner);
				}
			}

			return new ObjectInfoReference(this, level, innerObjects, false);
		}
	}

	@Override
	public List<ArtFieldInfo> getFieldsOnLevel(ArtFieldInfo parent,
			FieldInfo field, int level) {

		ArtFieldInfo thisField = new ArtFieldInfo(parent, field, this);

		if (level == 0) {
			List<ArtFieldInfo> fields = new ArrayList<ArtFieldInfo>();
			fields.add(thisField);
			return fields;
		} else {
			List<ArtFieldInfo> fields = new ArrayList<ArtFieldInfo>();
			FieldInfo[] allFields = getFields();
			for (FieldInfo fieldInfo : allFields) {
				List<ArtFieldInfo> innerFields = fieldInfo.getType()
						.getFieldsOnLevel(thisField, fieldInfo, level - 1);
				fields.addAll(innerFields);
			}
			return fields;
		}
	}

}
