/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sav.common.core.Constants;
import sav.common.core.Pair;


/**
 * @author LLT
 *
 */
public class ClassUtils {
	private static final Logger log = LoggerFactory.getLogger(ClassUtils.class);
	private ClassUtils() {}

	public static String getCanonicalName(String pkg, String clName) {
		return StringUtils.dotJoin(pkg, clName);
	}
	
	public static String getCanonicalName(String pkgName, String ownerClName, String innerClName) {
		return String.format("%s.%s$%s", pkgName, ownerClName, innerClName);
	}
	
	public static String getClassFullName(String pkgName, String clSimpleName, String...owners) {
		if (CollectionUtils.isEmpty(owners)) {
			return getCanonicalName(pkgName, clSimpleName);
		}
		String owner = StringUtils.join("$", (Object[]) owners);
		return getCanonicalName(pkgName, owner, clSimpleName);
	}
	
	public static String toClassCanonicalName(String classPath) {
		return classPath.replace(Constants.FILE_SEPARATOR, Constants.DOT);
	}
	
	public static String getCompilableName(String className, String newChar) {
		return className.replace(Constants.NESTED_CLASS_SEPARATOR, newChar);
	}
	
	/**
	 * very weak method. only handle very simple case of className.
	 * TODO LLT: handle for the case of inner class as well.
	 */
	public static String getJFilePath(String sourcePath, String className) {
		return sourcePath + Constants.FILE_SEPARATOR
				+ className.replace(Constants.DOT, Constants.FILE_SEPARATOR)
				+ Constants.JAVA_EXT_WITH_DOT;
	}
	
	public static String getClassFilePath(String targetPath, String className) {
		return new StringBuilder()
						.append(targetPath)
						.append(Constants.FILE_SEPARATOR)
						.append(replaceDotWithFileSeparator(className))
						.append(Constants.CLASS_EXT_WITH_DOT)
						.toString();
	}
	
	public static List<File> getCompiledClassFiles(String targetPath,
			String className) {
		int lastDotIdx = className.lastIndexOf(Constants.DOT);
		String classSimpleName = className.substring(lastDotIdx + 1)
									.split("$")[0];
		String pkgName = className.substring(0, lastDotIdx);
		String classFolder = new StringBuilder()
								.append(targetPath)
								.append(Constants.FILE_SEPARATOR)
								.append(replaceDotWithFileSeparator(pkgName))
								.toString();
		@SuppressWarnings("unchecked")
		Collection<File> files = FileUtils.listFiles(new File(classFolder), 
				new WildcardFileFilter(
						new String[]{getClassFileName(classSimpleName),
								getClassFileName(classSimpleName + "$*")}), null);
		return new ArrayList<File>(files);
	}
	
	private static String replaceDotWithFileSeparator(String str) {
		return str.replace(Constants.DOT, Constants.FILE_SEPARATOR);
	}
	
	public static String getClassFileName(String classSimpleName) {
		return classSimpleName + Constants.CLASS_EXT_WITH_DOT;
	}
	
	public static String getSimpleName(String className) {
		int idx = className.lastIndexOf(Constants.DOT);
		if (idx > 0) {
			return className.substring(idx + 1);
		}
		return className;
	}
	
	public static String getPackageName(String className) {
		int idx = className.lastIndexOf(Constants.DOT);
		if (idx > 0) {
			return className.substring(0, idx);
		}
		return "";
	}
	
	/**
	 * return pair of class name, and method name
	 */
	public static Pair<String, String> splitClassMethod(String name) {
		int idx = name.lastIndexOf(Constants.DOT);
		if (idx > 0) {
			return Pair.of(name.substring(0, idx), 
					name.substring(idx + 1));
		}
		throw new IllegalArgumentException(
				"Cannot extract method from string, expect [classname].[method], get "
						+ name);
	}
	
	public static String toClassMethodStr(Pair<String, String> classMethod) {
		return toClassMethodStr(classMethod.a, classMethod.b);
	}
	
	public static String toClassMethodStr(String clazz, String method) {
		return StringUtils.dotJoin(clazz, method);
	}
	
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

	public static Method findPublicSetterMethod(Class<?> clazz, String fieldName, Class<?> fieldType) {
		String methodName = new StringBuilder("set").append(org.apache.commons.lang.StringUtils.capitalize(fieldName)).toString();
		try {
			Method setter = clazz.getMethod(methodName, fieldType);
			if (!Modifier.isPublic(setter.getModifiers())) {
				log.debug(String.format("Setter method [%s] is invisible!", methodName));
				return null;
			}
			return setter;
		} catch(Exception ex) {
			return null;
		}
	}
}
