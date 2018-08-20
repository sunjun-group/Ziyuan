/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package evosuite.core;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.Constants;
import sav.common.core.utils.ClassUtils;

/**
 * @author LLT
 *
 */
public class TargetClass {
	private String className;
	private List<String> methods = new ArrayList<String>();
	private List<Integer> methodStartLines = new ArrayList<Integer>();
	private List<String> classMethods = new ArrayList<String>();
	private List<String> methodIds = new ArrayList<String>();
	
	public void addMethod(String methodId, String methodName, int line, String classMethod) {
		methods.add(methodName);
		methodStartLines.add(line);
		classMethods.add(classMethod);
		methodIds.add(methodId);
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<String> getMethods() {
		return methods;
	}

	public void setMethods(List<String> methods) {
		this.methods = methods;
	}

	public List<Integer> getMethodStartLines() {
		return methodStartLines;
	}

	public void setMethodStartLines(List<Integer> methodStartLines) {
		this.methodStartLines = methodStartLines;
	}

	public String getMethodFullName(int i) {
		return classMethods.get(i);
	}

	public String generatePackage(int i) {
		return ClassUtils.getPackageName(className);
//		String pkg = new StringBuilder().append(classMethods.get(i)).append(methodStartLines.get(i)).toString();
//		return org.apache.commons.lang3.StringUtils.lowerCase(pkg);
	}

}
