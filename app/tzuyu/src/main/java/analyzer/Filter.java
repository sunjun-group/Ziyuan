package analyzer;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Filter {
	
	private static Map<Class<?>, Set<String>> filters = new HashMap<Class<?>, Set<String>>();

	public static void setMethodFileter(Class<?> clazz, List<String> methods) {
		Set<String> set = new HashSet<String>();
		for (String element : methods) {
			set.add(element);
		}
		filters.put(clazz, set);
	}

	private static final Set<Class<?>> filterdTypes = new HashSet<Class<?>>();
	static {
		// For entries below, this acts as a filter.
		filterdTypes.add(Object.class);
		filterdTypes.add(Thread.class);
		filterdTypes.add(Closeable.class);
		filterdTypes.add(Cloneable.class);
		filterdTypes.add(Flushable.class);
		filterdTypes.add(IOException.class);
		filterdTypes.add(Serializable.class);
		filterdTypes.add(Exception.class);
		filterdTypes.add(Class.class);
		filterdTypes.add(Throwable.class);
	}

	public static boolean isInFilterList(Class<?> type) {
		return filterdTypes.contains(type);
	}

	public static boolean filterMethod(Method method) {
		// We only handle public non-abstract method
		if (Modifier.isPublic(method.getModifiers())
				&& !Modifier.isAbstract(method.getModifiers())) {
			if (filters.containsKey(method.getDeclaringClass())) {
				return filters.get(method.getDeclaringClass()).contains(
						method.getName());
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public static boolean filterConstructor(Constructor<?> ctor) {
		/*
		 * if (Modifier.isPublic(ctor.getModifiers())) { return true; }
		 */
		return true;
	}

	public static boolean filterField(Field field) {
		if (Modifier.isStatic(field.getModifiers())
				|| Modifier.isFinal(field.getModifiers())
				|| Modifier.isTransient(field.getModifiers())
				|| Modifier.isVolatile(field.getModifiers())) {
			return false;
		}
		return true;
	}

	public static boolean filterClass(Class<?> type) {
		return Modifier.isPublic(type.getModifiers());
	}

}
