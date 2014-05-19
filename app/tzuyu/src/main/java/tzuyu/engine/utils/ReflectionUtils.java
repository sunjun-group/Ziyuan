package tzuyu.engine.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ReflectionUtils {

	public static enum Match {
		EXACT_TYPE, COMPATIBLE_TYPE
	}

	private static Map<Pair<Class<?>, Class<?>>, Boolean> canBeUsedCache = new LinkedHashMap<Pair<Class<?>, Class<?>>, Boolean>();

	public static long num_times_canBeUsedAs_called = 0;

	/**
	 * Checks if an object of class c1 can be used as an object of class c2.
	 * This is more than subtyping: for example, int can be used as Integer, but
	 * the latter is not a subtype of the former.
	 */
	public static boolean canBeUsedAs(Class<?> c1, Class<?> c2) {
		if (c1 == null || c2 == null)
			throw new IllegalArgumentException("Parameters cannot be null.");
		if (c1.equals(c2))
			return true;
		if (c1.equals(void.class) && c2.equals(void.class))
			return true;
		if (c1.equals(void.class) || c2.equals(void.class))
			return false;
		Pair<Class<?>, Class<?>> classPair = new Pair<Class<?>, Class<?>>(c1,
				c2);
		Boolean cachedRetVal = canBeUsedCache.get(classPair);
		boolean retval;
		if (cachedRetVal == null) {
			retval = canBeUsedAs0(c1, c2);
			canBeUsedCache.put(classPair, retval);
		} else {
			retval = cachedRetVal;
		}
		return retval;
	}

	// TODO test classes array code (third if clause)
	private static boolean canBeUsedAs0(Class<?> c1, Class<?> c2) {
		if (c1.isArray()) {
			if (c2.equals(Object.class))
				return true;
			if (!c2.isArray())
				return false;
			Class<?> c1SequenceType = c1.getComponentType();
			Class<?> c2componentType = c2.getComponentType();

			if (c1SequenceType.isPrimitive()) {
				if (c2componentType.isPrimitive()) {
					return (c1SequenceType.equals(c2componentType));
				} else {
					return false;
				}
			} else {
				if (c2componentType.isPrimitive()) {
					return false;
				} else {
					c1 = c1SequenceType;
					c2 = c2componentType;
				}
			}
		}

		if (c1.isPrimitive())
			c1 = PrimitiveTypes.boxedType(c1);
		if (c2.isPrimitive())
			c2 = PrimitiveTypes.boxedType(c2);

		boolean ret = false;

		if (c1.equals(c2)) { // XXX redundant (see canBeUsedAs(..)).
			ret = true;
		} else if (c2.isInterface()) {
			Set<Class<?>> c1Interfaces = getInterfacesTransitive(c1);
			if (c1Interfaces.contains(c2))
				ret = true;
			else
				ret = false;
		} else if (c1.isInterface()) {
			// c1 represents an interface and c2 a class.
			// The only safe possibility is when c2 is Object.
			if (c2.equals(Object.class))
				ret = true;
			else
				ret = false;
		} else {
			ret = isSubclass(c1, c2);
		}
		return ret;
	}

	private static Set<Class<?>> getInterfacesTransitive(Class<?> c1) {

		Set<Class<?>> ret = new LinkedHashSet<Class<?>>();

		Class<?>[] c1Interfaces = c1.getInterfaces();
		for (int i = 0; i < c1Interfaces.length; i++) {
			ret.add(c1Interfaces[i]);
			ret.addAll(getInterfacesTransitive(c1Interfaces[i]));
		}

		Class<?> superClass = c1.getSuperclass();
		if (superClass != null)
			ret.addAll(getInterfacesTransitive(superClass));

		return ret;
	}

	public static Set<Class<?>> getDirectSuperTypes(Class<?> c) {
		Set<Class<?>> result = new LinkedHashSet<Class<?>>();
		Class<?> superclass = c.getSuperclass();
		if (superclass != null)
			result.add(superclass);
		result.addAll(Arrays.<Class<?>> asList(c.getInterfaces()));
		return result;
	}

	private static boolean isSubclass(Class<?> c1, Class<?> c2) {
		assert (c1 != null);
		assert (c2 != null);
		assert (!c1.equals(Void.TYPE));
		assert (!c2.equals(Void.TYPE));
		assert (!c1.isInterface());
		assert (!c2.isInterface());
		return c2.isAssignableFrom(c1);
	}

	public static String checkArgumentTypes(Object[] inputs, Class<?>[] types,
			Object errMsgContext) {
		if (inputs.length != types.length)
			return "Bad number of parameters for " + errMsgContext + " was:"
					+ inputs.length;

		for (int i = 0; i < types.length; i++) {
			Object input = inputs[i];
			Class<?> pType = types[i];
			if (!canBePassedAsArgument(input, pType))
				return "Invalid type of argument at pos "
						+ i
						+ " for:"
						+ errMsgContext
						+ " expected:"
						+ pType
						+ " was:"
						+ (input == null ? "n/a(input was null)" : input
								.getClass());
		}
		return null;
	}

	public static boolean canBePassedAsArgument(Object input, Class<?> c) {
		if (c == null || c.equals(Void.TYPE))
			throw new IllegalStateException("Illegal type of parameter " + c);
		if (input == null) {
			return true;
		} else if (!ReflectionUtils.canBeUsedAs(input.getClass(), c)) {
			return false;
		} else
			return true;
	}

	private static Map<Class<?>, Boolean> cached_isVisible = new LinkedHashMap<Class<?>, Boolean>();

	public static boolean isVisible(Class<?> type) {
		Boolean cached = cached_isVisible.get(type);
		if (cached == null) {
			if (type.isAnonymousClass()) {
				cached = false;
			} else {
				int modifiers = type.getModifiers();
				boolean typeVisible = isVisible(modifiers);
				if (type.isMemberClass()) {
					cached = typeVisible && isVisible(type.getDeclaringClass());
				} else {
					cached = typeVisible;
				}
			}
			cached_isVisible.put(type, cached);
		}
		return cached;
	}

	private static boolean isVisible(int modifiers) {
		return Modifier.isPublic(modifiers);
	}

	public static String getSignature(Method method, String[] parameterNames) {
		StringBuilder sb = new StringBuilder();
		sb.append(method.getDeclaringClass().getSimpleName() + ".");
		sb.append(method.getName() + "(");
		Class<?>[] params = method.getParameterTypes();
		for (int j = 0; j < params.length; j++) {
			sb.append(params[j].getSimpleName())
				.append(" ").append(parameterNames[j]);
			if (j < (params.length - 1))
				sb.append(",");
		}
		sb.append(")");
		return sb.toString();
	}

	public static String getSignature(Constructor<?> ctor) {
		StringBuilder sb = new StringBuilder();
		sb.append(ctor.getName() + ".<init>(");
		Class<?>[] params = ctor.getParameterTypes();
		for (int j = 0; j < params.length; j++) {
			sb.append(params[j].getSimpleName());
			if (j < (params.length - 1))
				sb.append(",");
		}
		sb.append(")");
		return sb.toString();
	}

	public static String getCompilableName(Class<?> type) {
		String retval = type.getName();

		// If it's an array, it starts with "[".
		if (retval.charAt(0) == '[') {
			// Class.getName() returns a a string that is almost in JVML
			// format, except that it slashes are periods. So before calling
			// classnameFromJvm, we replace the period with slashes to
			// make the string true JVML.
			retval = Types.getTypeName(retval.replace('.', '/'));
		}

		// If inner classes are involved, Class.getName() will return
		// a string with "$" characters. To make it compilable, must replace
		// with
		// dots.
		retval = retval.replace('$', '.');

		return retval;
	}

}
