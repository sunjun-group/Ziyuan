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

import learntest.core.RunTimeInfo;

/**
 * @author LLT
 *
 */
public interface IModelRuntimeInfo {

	
	public int getJavaElementType();

	public void transferToMap(Map<IJavaElement, IModelRuntimeInfo> jEleRuntimeInfoMap);

	IModelRuntimeInfo createOrAddToParentInfo();
	
	public Map<IMethod, RunTimeInfo> getRuntimeInfo();

	IJavaElement getJavaElement();
}
