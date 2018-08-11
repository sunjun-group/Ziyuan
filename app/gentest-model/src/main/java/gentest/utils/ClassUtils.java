/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * @author LLT
 *
 */
public class ClassUtils {
	private ClassUtils() {}
	
	public static Class<?> getArrayContentType(Class<?> type) {
		Class<?> contentType = type;
		while (contentType.isArray()) {
			contentType = contentType.getComponentType();
		}
		if (contentType == type) {
			return null;
		}
		return contentType;
	}
	
	public static boolean isAupperB(Class<?> a, Class<?> b) {
		return a.isAssignableFrom(b);
	}
	
	public static Method loockupMethod(Class<?> clazz, String methodNameOrSign) {
		List<Method> matches = loockupMethodByNameOrSign(clazz, methodNameOrSign);
		return CollectionUtils.getFirstElement(matches);
	}

	public static List<Method> loockupMethodByNameOrSign(Class<?> clazz, String methodNameOrSign) {
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
			return matchingMethods;
		}
		
		/*
		 * for easy case, just return the first one, if only method name is
		 * provided, and there are more than one method matches. Change the logic if necessary. 
		 */
		if (methodSign.isEmpty()) {
			return matchingMethods;
		}
		
		for (Method method : matchingMethods) {
			if (SignatureUtils.getSignature(method).equals(methodSign)) {
				return CollectionUtils.listOf(method, 1);
			}
		}
		
		/* no method in candidates matches the given signature */
		throw new IllegalArgumentException(String.format("cannot find method %s in class %s", methodNameOrSign
				, clazz.getName()));
	}

}
