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

import tzuyu.engine.TzMethod;

public class Filter {
	private static Map<Class<?>, Set<TzMethod>> filters = new HashMap<Class<?>, Set<TzMethod>>();

	public static void setMethodFileter(Class<?> clazz, List<TzMethod> methods) {
		Set<TzMethod> set = new HashSet<TzMethod>();
		for (TzMethod element : methods) {
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
				for (TzMethod tzMethod : filters.get(method.getDeclaringClass())) {
					if (method.getName().equals(tzMethod.getName())
							&& SignatureUtils.getSignature(method).equals(
									tzMethod.getSignature())) {
						return true;
					}
					 
				}
			} else {
				return true;
			}
		} 
		return false;
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
