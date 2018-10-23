/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.activelearning.core.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.SignatureUtils;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class MethodInfo {
	private ClassInfo targetClazz;
	private String methodName;
	private String methodSignature;
	private int lineNum;
	private int methodLength = -1; // can be null
	private List<String> params;
	private List<String> paramTypes;
	private boolean varType; // if all primitive TRUE, otherwise FALSE;
	public final static boolean ALL_PT_VAR = true;
	public final static boolean SOME_PT_VAR = false;
	
	public MethodInfo(ClassInfo targetClass) {
		this.targetClazz = targetClass;
		targetClass.addMethod(this);
	}

	public ClassInfo getTargetClazz() {
		return targetClazz;
	}

	public List<String> getParams() {
		return params;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	public String getMethodSignature() {
		return SignatureUtils.createMethodNameSign(methodName, methodSignature);
	}

	public String getSignature() {
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

	public String getMethodFullName() {
		return ClassUtils.toClassMethodStr(getClassName(), methodName);
	}
	
	public String getMethodWithSignature() {
		return SignatureUtils.createMethodNameSign(methodName, methodSignature);
	}

	public String getClassName() {
		return targetClazz.getClassName();
	}

	public void setParams(List<String> params) {
		this.params = params;
	}

	public Map<String, List<String>> createClassMethodMap() {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		map.put(getClassName(), CollectionUtils.listOf(methodName, 1));
		return map;
	}

	public void setParamTypes(List<String> paramTypes) {
		this.paramTypes = paramTypes;
	}
	
	public List<String> getParamTypes() {
		return paramTypes;
	}

	public int getMethodLength() {
		return methodLength;
	}

	public void setMethodLength(int methodLength) {
		this.methodLength = methodLength;
	}
	
	@Override
	public String toString() {
		return getMethodId();
	}

	public String getMethodId() {
		return StringUtils.dotJoin(getClassName(), methodName, lineNum);
	}

	public boolean isVarType() {
		return varType;
	}

	public void setVarType(boolean varType) {
		this.varType = varType;
	}
}
