/**
 * Copyright TODO
 */
package gentest.core.commons.utils;

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
		String methodName = SignatureUtils.extractMethodName(methodNameOrSign);
		String methodSign = SignatureUtils.extractSignature(methodNameOrSign);
		
		List<Method> matchingMethods = new ArrayList<Method>();
		/* try to look up by name first */
		for (Method method : clazz.getMethods()) {
			if (method.getName().equals(methodName)) {
				matchingMethods.add(method);
			}
		}
		
		if (matchingMethods.isEmpty()) {
			/* cannot find method for class */
			throw new IllegalArgumentException(String.format("cannot find method %s in class %s", methodNameOrSign
					, clazz.getName()));
		}
		
		/* if only one method is found with given name, just return. 
		 * otherwise, check for the method with right signature */
		if (matchingMethods.size() == 1) {
			return matchingMethods.get(0);
		}
		
		/*
		 * for easy case, just return the first one, if only method name is
		 * provided, and there are more than one method matches. Change the logic if necessary. 
		 */
		if (methodSign.isEmpty()) {
			return matchingMethods.get(0);
		}
		
		for (Method method : matchingMethods) {
			if (SignatureUtils.getSignature(method).equals(methodSign)) {
				return method;
			}
		}
		
		/* no method in candidates matches the given signature */
		throw new IllegalArgumentException(String.format("cannot find method %s in class %s", methodNameOrSign
				, clazz.getName()));
	}
	
}
