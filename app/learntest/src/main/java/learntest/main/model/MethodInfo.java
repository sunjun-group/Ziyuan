/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.main.model;

/**
 * @author LLT
 *
 */
public class MethodInfo {
	private String className;
	private String classSimpleName;
	private String methodName;
	private String methodSignature;
	private int lineNum;
	
	public MethodInfo(String className, String methodName, String methodSign, int lineNumber) {
		setClassName(className);
		this.methodName = methodName;
		this.methodSignature = methodSign;
		this.lineNum = lineNumber;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
		this.classSimpleName = className.substring(className.lastIndexOf(".")+1, className.length());
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getMethodSignature() {
		return methodSignature;
	}

	public void setMethodSignature(String methodSignature) {
		this.methodSignature = methodSignature;
	}

	public int getLineNum() {
		return lineNum;
	}

	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}

	public String getClassSimpleName() {
		return classSimpleName;
	}

	public void setClassSimpleName(String classSimpleName) {
		this.classSimpleName = classSimpleName;
	}

}
