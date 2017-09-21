/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data.classinfo;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.utils.ClassUtils;

/**
 * @author LLT
 *
 */
public class ClassInfo {
	private String className;
	private String classSimpleName;
	private List<MethodInfo> methods = new ArrayList<MethodInfo>();

	public ClassInfo(String className) {
		this.className = className;
		classSimpleName = ClassUtils.getSimpleName(className);
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassSimpleName() {
		return classSimpleName;
	}

	public void setClassSimpleName(String classSimpleName) {
		this.classSimpleName = classSimpleName;
	}

	public List<MethodInfo> getMethods() {
		return methods;
	}

	public void setMethods(List<MethodInfo> targetMethods) {
		this.methods = targetMethods;
	}

	public void addMethod(MethodInfo targetMethod) {
		methods.add(targetMethod);
	}

}
