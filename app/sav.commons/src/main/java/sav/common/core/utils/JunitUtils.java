/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import sav.common.core.Pair;

/**
 * @author LLT
 *
 */
public class JunitUtils {
	private static final String JUNIT_TEST_METHOD_PREFIX = "test";
	private static final String JUNIT_TEST_SUITE_PREFIX = "suite";
	
	public static List<String> extractTestMethods(List<String> junitClassNames, ClassLoader classLoader) throws ClassNotFoundException {
		List<String> result = new ArrayList<String>();
		for (String className : junitClassNames) {
			extractTestMethodsForClass(result, className, classLoader);
		}
		return result;
	}
	
	public static List<String> extractTestMethods(List<String> junitClassNames)
			throws ClassNotFoundException {
		return extractTestMethods(junitClassNames, null);
	}

	private static void extractTestMethodsForClass(List<String> result, String className, ClassLoader classLoader)
			throws ClassNotFoundException {
		List<String> tcs = new ArrayList<String>();
		Class<?> junitClass = ClassLoaderUtils.forName(className, classLoader);
		Method[] methods = junitClass.getDeclaredMethods();
		for (Method method : methods) {
			if (isTestMethod(junitClass, method)) {
				tcs.add(ClassUtils.toClassMethodStr(className,
						method.getName()));
			}
		}
		/* TODO: to remove. just for test the specific testcases in SIR */
		if (tcs.isEmpty()) {
			try {
				Method suiteMth = junitClass.getMethod(JUNIT_TEST_SUITE_PREFIX);
				TestSuite suite = (TestSuite) suiteMth.invoke(junitClass);
				findTestcasesInSuite(suite, tcs, classLoader);
			} catch (Exception e) {
				throw new IllegalArgumentException("cannot find testcases in class " + className);
			}
		}
		sortMethods(tcs);
		result.addAll(tcs);
	}

	private static void sortMethods(List<String> tcs) {
		Collections.sort(tcs, new AlphanumComparator());
	}

	private static void findTestcasesInSuite(TestSuite suite,
			List<String> classMethods, ClassLoader classLoader) throws ClassNotFoundException {
		Enumeration<junit.framework.Test> tests = suite.tests();
		while (tests.hasMoreElements()) {
			junit.framework.Test test = tests.nextElement();
			if (test instanceof TestSuite) {
				findTestcasesInSuite((TestSuite) test, classMethods, classLoader);
			} else if (test instanceof TestCase) {
				TestCase tc = (TestCase) test;
				extractTestMethodsForClass(classMethods, tc.getClass().getName(), classLoader);
			}
		}
	}

	public static boolean isTestMethod(Class<?> junitClass, Method method) {
		for (Annotation annotation : method.getAnnotations()) {
			Class<? extends Annotation> annotationType = annotation.annotationType();
			if (Test.class.getName().equals(annotationType.getName())) {
				return true;
			}
		}
		if (TestCase.class.isAssignableFrom(junitClass)) {
			int modifiers = method.getModifiers();
			if (Modifier.isPublic(modifiers)
					&& !Modifier.isStatic(modifiers)) {
				return true;
			}
		}
		return false;
	}
	
	public static List<Pair<String, String>> toPair(List<String> junitClassTestMethods) {
		List<Pair<String, String>> result = new ArrayList<Pair<String,String>>(junitClassTestMethods.size());
		for (String classMethod : junitClassTestMethods) {
			result.add(toPair(classMethod));
		}
		return result;
	}

	public static Pair<String, String> toPair(String junitClassTestMethod) {
		return ClassUtils.splitClassMethod(junitClassTestMethod);
	}
	
	public static Map<String, List<String>> toClassMethodsMap(Collection<String> junitClassTestMethods) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		for (String classMethod : junitClassTestMethods) {
			Pair<String, String> classMethodPair = toPair(classMethod);
			CollectionUtils.getListInitIfEmpty(map, classMethodPair.a).add(classMethodPair.b);
		}
		return map;
	}
	
	public static Map<String, List<String>> toOrderedClassMethodsMap(Collection<String> junitClassTestMethods) {
		List<String> classMethods = new ArrayList<>(junitClassTestMethods);
		StringUtils.sortAlphanumericStrings(classMethods);
		Map<String, List<String>> map = new LinkedHashMap<>();
		List<List<String>> methodLists = new ArrayList<>();
		for (String classMethod : junitClassTestMethods) {
			Pair<String, String> classMethodPair = toPair(classMethod);
			List<String> methodList = map.get(classMethodPair.a);
			if (methodList == null) {
				methodList = new ArrayList<>();
				map.put(classMethodPair.a, methodList);
				methodLists.add(methodList);
			}
			methodList.add(classMethodPair.b);
		}
		for (List<String> methodList : methodLists) {
			StringUtils.sortAlphanumericStrings(methodList);
		}
		return map;
	}
	
	public static List<String> toClassMethodStrs(List<Pair<String, String>> values) {
		List<String> result = new ArrayList<String>();
		for (Pair<String, String> value : values) {
			result.add(ClassUtils.toClassMethodStr(value));
		}
		return result;
	}
}
