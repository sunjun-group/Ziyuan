/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.commons.data;

import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;

/**
 * @author LLT
 *
 */
public class TypeRunTimeInfo extends AbstractModelRuntimeInfo<MethodRuntimeInfo> implements IModelRuntimeInfo {

	public TypeRunTimeInfo(IType element) {
		super(element);
	}
	
	public TypeRunTimeInfo(IType element, List<MethodRuntimeInfo> children) {
		super(element, children);
	}

	@Override
	public int getJavaElementType() {
		return IJavaElement.TYPE;
	}

	@Override
	public IModelRuntimeInfo createOrAddToParentInfo() {
		ClassRuntimeInfo classRuntimeInfo = new ClassRuntimeInfo(getParentElement());
		classRuntimeInfo.add(this);
		return classRuntimeInfo;
	}

	@Override
	protected ICompilationUnit getParentElement() {
		return ((IType) javaElement).getCompilationUnit();
	}
	
}
