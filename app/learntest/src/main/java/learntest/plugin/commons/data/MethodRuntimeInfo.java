/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.commons.data;

import java.util.Map;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

import learntest.core.EmptyRuntimeInfo;
import learntest.core.RunTimeInfo;
import learntest.core.commons.data.classinfo.TargetMethod;

/**
 * @author LLT
 *
 */
public class MethodRuntimeInfo extends AbstractModelRuntimeInfo<IModelRuntimeInfo> implements IModelRuntimeInfo {
	private RunTimeInfo runtimeInfo;
	
	public MethodRuntimeInfo(IMethod element, RunTimeInfo runtimeInfo, TargetMethod targetMethod) {
		super(element, 0);
		if (runtimeInfo == null) {
			this.runtimeInfo = new EmptyRuntimeInfo(targetMethod);
		} else {
			this.runtimeInfo = runtimeInfo;
		}
	}
	
	@Override
	public int getJavaElementType() {
		return IJavaElement.METHOD;
	}

	@Override
	public IModelRuntimeInfo createOrAddToParentInfo() {
		TypeRunTimeInfo typeRunTimeInfo = new TypeRunTimeInfo(getParentElement());
		typeRunTimeInfo.add(this);
		return typeRunTimeInfo; 
	}

	@Override
	protected IType getParentElement() {
		return ((IMethod) javaElement).getDeclaringType();
	}
	
	@Override
	protected void appendMethodRuntimeInfo(Map<IMethod, RunTimeInfo> map) {
		map.put((IMethod) javaElement, runtimeInfo);
	}
	
	public RunTimeInfo getRawRuntimeInfo() {
		return runtimeInfo;
	}
}
