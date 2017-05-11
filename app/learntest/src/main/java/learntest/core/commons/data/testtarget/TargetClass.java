/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data.testtarget;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.utils.ClassUtils;

/**
 * @author LLT
 *
 */
public class TargetClass {
	private String className;
	private String classSimpleName;
	private List<TargetMethod> targetMethods = new ArrayList<TargetMethod>();

	public TargetClass(String className) {
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

	public List<TargetMethod> getTargetMethods() {
		return targetMethods;
	}

	public void setTargetMethods(List<TargetMethod> targetMethods) {
		this.targetMethods = targetMethods;
	}

	public void addMethod(TargetMethod targetMethod) {
		targetMethods.add(targetMethod);
	}

}
