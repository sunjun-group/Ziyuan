/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.commons.data;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;

/**
 * @author LLT
 *
 */
public class ClassRuntimeInfo extends AbstractModelRuntimeInfo<TypeRunTimeInfo> implements IModelRuntimeInfo {

	public ClassRuntimeInfo(ICompilationUnit cu) {
		super(cu, getSize(cu));
	}

	private static int getSize(ICompilationUnit cu) {
		try {
			return cu.getTypes().length;
		} catch (JavaModelException e) {
			return 0;
		}
	}

	@Override
	public int getJavaElementType() {
		return IJavaElement.COMPILATION_UNIT;
	}

	@Override
	public IModelRuntimeInfo createOrAddToParentInfo() {
		PackageRuntimeInfo packageRuntimeInfo = new PackageRuntimeInfo(getParentElement());
		packageRuntimeInfo.add(this);
		return packageRuntimeInfo;
	}

	@Override
	protected IPackageFragment getParentElement() {
		return (IPackageFragment) ((ICompilationUnit) javaElement).getParent();
	}

}
