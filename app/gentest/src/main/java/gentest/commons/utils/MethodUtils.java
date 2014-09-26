/**
 * Copyright TODO
 */
package gentest.commons.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author LLT
 */
public class MethodUtils {
	private MethodUtils() {}
	
	public static boolean isStatic(Method method) {
		return Modifier.isStatic(method.getModifiers());
	}
}
