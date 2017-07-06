/**
 * Copyright TODO
 */
package gentest.core.commons.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.SignatureUtils;

/**
 * @author LLT
 */
public class MethodUtils {
	private MethodUtils() {}
	
	public static boolean isStatic(Method method) {
		return Modifier.isStatic(method.getModifiers());
	}
	
	public static boolean isPublic(Method method) {
		return Modifier.isPublic(method.getModifiers());
	}
	
	public static List<Method> findMethods(Class<?> clazz, List<String> methodFullSigns) {
		List<Method> result = new ArrayList<Method>();
		for (Method method : clazz.getMethods()) {
			if (methodFullSigns.contains(SignatureUtils.createMethodNameSign(method))) {
				result.add(method);
				continue;
			}
		}
		
		return result;
	}
	
	public static Method findMethod(Class<?> clazz, String methodNameOrSign) {
		return ClassUtils.loockupMethod(clazz, methodNameOrSign);
	}
	
}
