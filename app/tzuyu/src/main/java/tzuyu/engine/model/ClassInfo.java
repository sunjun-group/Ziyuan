package tzuyu.engine.model;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import tzuyu.engine.TzConfiguration;

public abstract class ClassInfo {

	public abstract Class<?> getType();

	public abstract FieldInfo[] getDeclaredFields();

	public abstract FieldInfo getDeclaredField(String name);

	public abstract MethodInfo[] getDeclaredMethods();

	public abstract ClassInfo[] getInterfaces();

	public abstract ClassInfo getSuperClass();

	public abstract ClassInfo[] getInnerClasses();

	public abstract ConstructorInfo[] getConstructors();

	public abstract ClassInfo getComponentType();

	public abstract ObjectInfo cloneMockup(Object object, int level, TzConfiguration config);

	public boolean isAbstract() {
		Class<?> type = this.getType();
		return Modifier.isAbstract(type.getModifiers());
	}

	/**
	 * Get all the fields defined in the specified level. The level starts from
	 * 0. The fields defined on level 0 are the reference fields corresponding
	 * to the parameter types themselves. Fields defined on level 1 is the
	 * fields declared directly on the parameter classes.
	 * 
	 * @param level
	 * @return
	 */
	public final List<ArtFieldInfo> getFieldsOnLevel(int level) {
		return getFieldsOnLevel(null, null, level);
	}

	/**
	 * Get all the fields defined above the specified level (inclusive).
	 * 
	 * @param level
	 * @return
	 */
	public final List<ArtFieldInfo> getFieldsAboveLevel(int level) {
		List<ArtFieldInfo> result = new ArrayList<ArtFieldInfo>();

		for (int index = 0; index <= level; index++) {
			result.addAll(getFieldsOnLevel(index));
		}
		return result;
	}

	public abstract List<ArtFieldInfo> getFieldsOnLevel(ArtFieldInfo parent,
			FieldInfo field, int level);

	public final FieldInfo getField(String name) {
		FieldInfo[] fields = getFields();
		for (int i = fields.length - 1; i >= 0; i--) {
			if (fields[i].getName().equals(name)) {
				return fields[i];
			}
		}
		return null;
	}

	public final ObjectInfo clone(Object object, TzConfiguration config) {
		return cloneMockup(object, 0, config);
	}

	public final FieldInfo[] getFields() {
		if (isPrimitive() || isArray()) {
			return new FieldInfo[0];
		}

		HashSet<FieldInfo> result = new HashSet<FieldInfo>();
		// Fields defined in Java interface are implicitly static and final.
		// We may not need to include such fields for instrumentation.

		ClassInfo superClass = getSuperClass();
		FieldInfo[] supFileds = (superClass == null) ? new FieldInfo[0]
				: superClass.getFields();
		for (int i = 0; i < supFileds.length; i++) {
			int m = supFileds[i].getModifiers();
			if (Modifier.isPrivate(m)) {
				continue;
			}
			result.add(supFileds[i]);
		}

		FieldInfo[] localFields = getDeclaredFields();
		for (int i = 0; i < localFields.length; i++) {
			result.add(localFields[i]);
		}

		return result.toArray(new FieldInfo[result.size()]);
	}

	public MethodInfo[] getMethods(boolean isInheritedMethod) {
		if (isArray() || isPrimitive()) {
			return new MethodInfo[0];
		}

		List<MethodInfo> result = new ArrayList<MethodInfo>();
		// We first add the method defined in this class and then process
		// The methods defined in superclass.
		MethodInfo[] localMethods = getDeclaredMethods();
		for (int i = 0; i < localMethods.length; i++) {
			int m = localMethods[i].getModifiers();
			if (Modifier.isPublic(m) && !Modifier.isAbstract(m)) {
				result.add(localMethods[i]);
			}
		}

		if (!isInheritedMethod) {
			return localMethods;
		}

		// Interfaces only define the contract of method, thus we don't need to
		// include the methods defined in the interface.
		ClassInfo superClass = getSuperClass();
		MethodInfo[] supMethods = (superClass == null) ? new MethodInfo[0]
				: superClass.getMethods(isInheritedMethod);

		// Filter out those methods overridden by current class
		List<MethodInfo> normal = filterOverriddenMethods(result, supMethods);

		result.addAll(normal);

		return result.toArray(new MethodInfo[result.size()]);

	}

	/**
	 * Filter the methods in the super classes that are overridden by the
	 * methods in the subclass. We consider the methods to be overridden by
	 * considering the signature and return type and the accessors (only public
	 * or default can be overridden).
	 * 
	 * @param subs
	 * @param supers
	 * @return
	 */
	private List<MethodInfo> filterOverriddenMethods(List<MethodInfo> subs,
			MethodInfo[] supers) {
		List<MethodInfo> result = new ArrayList<MethodInfo>();
		for (MethodInfo superMethod : supers) {
			boolean overridden = false;
			for (MethodInfo subMethod : subs) {
				if (isOverridden(subMethod, superMethod)) {
					overridden = true;
					break;
				}
			}
			if (!overridden) {
				result.add(superMethod);
			}
		}
		return result;
	}

	private boolean isOverridden(MethodInfo subMethod, MethodInfo superMethod) {
		// First check the name
		if (subMethod.getName().equals(superMethod.getName())) {
			// check the parameter List
			if (Arrays.equals(subMethod.getParameterTypes(),
					superMethod.getParameterTypes())) {
				// Check the return type compatible
				if (subMethod.getReturnType().equals(
						superMethod.getReturnType())) {
					return true;
				}
			}
		}
		return false;
	}

	public abstract boolean isArray();

	public abstract boolean isPrimitive();

	public abstract boolean isInterface();

	public final boolean isSuperClassOf(ClassInfo ci) {

		if (this.isInterface()) {
			return false;
		}

		ClassInfo current = ci;
		while (current != null) {
			if (this == current) {
				return true;
			}
			current = current.getSuperClass();
		}
		return false;
	}

}
