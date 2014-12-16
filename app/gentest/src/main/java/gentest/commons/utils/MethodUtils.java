/**
 * Copyright TODO
 */
package gentest.commons.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import sav.common.core.utils.SignatureUtils;

/**
 * @author LLT
 */
public class MethodUtils {
	private MethodUtils() {}
	
	public static boolean isStatic(Method method) {
		return Modifier.isStatic(method.getModifiers());
	}
	
	public static List<Method> findMethods(Class<?> clazz, List<String> methodSigns) {
		List<Method> result = new ArrayList<Method>();
		for (Method method : clazz.getMethods()) {
			if (methodSigns.contains(SignatureUtils.getSignature(method))) {
				result.add(method);
				continue;
			}
		}
		
		return result;
	}
}
