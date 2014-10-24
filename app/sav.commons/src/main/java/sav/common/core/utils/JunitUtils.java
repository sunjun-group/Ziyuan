/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import sav.common.core.Pair;

/**
 * @author LLT
 *
 */
public class JunitUtils {
	private static final String JUNIT_TEST_METHOD_PREFIX = "test";
	private static final String JUNIT_TEST_SUITE_PREFIX = "suite";
	
	public static List<String> extractTestMethods(List<String> junitClassNames)
			throws ClassNotFoundException {
		List<String> result = new ArrayList<String>();
		for (String className : junitClassNames) {
			Class<?> junitClass = Class.forName(className);
			Method[] methods = junitClass.getMethods();
			for (Method method : methods) {
				if (isTestMethod(junitClass, method)) {
					result.add(ClassUtils.toClassMethodStr(className,
							method.getName()));
				}
			}
		}
		return result;
	}

	public static boolean isTestMethod(Class<?> junitClass, Method method) {
		Test test = method.getAnnotation(Test.class);
		return test != null
				|| (TestCase.class.isAssignableFrom(junitClass) && (method.getName()
						.startsWith(JUNIT_TEST_METHOD_PREFIX) || method.getName()
						.startsWith(JUNIT_TEST_SUITE_PREFIX)));
	}
	
	public static List<Pair<String, String>> toPair(List<String> junitClassTestMethods) {
		List<Pair<String, String>> result = new ArrayList<Pair<String,String>>(junitClassTestMethods.size());
		for (String classMethod : junitClassTestMethods) {
			result.add(ClassUtils.splitClassMethod(classMethod));
		}
		return result;
	}
	
	public static List<String> toClassMethodStrs(List<Pair<String, String>> values) {
		List<String> result = new ArrayList<String>();
		for (Pair<String, String> value : values) {
			result.add(ClassUtils.toClassMethodStr(value));
		}
		return result;
	}
}
