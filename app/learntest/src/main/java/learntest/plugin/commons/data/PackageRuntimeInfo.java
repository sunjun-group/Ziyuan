/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.commons.data;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

/**
 * @author LLT
 *
 */
public class PackageRuntimeInfo extends AbstractModelRuntimeInfo<ClassRuntimeInfo> implements IModelRuntimeInfo {

	public PackageRuntimeInfo(IPackageFragment element) {
		super(element, getSize(element));
	}

	private static int getSize(IPackageFragment element) {
		try {
			return element.getCompilationUnits().length;
		} catch (JavaModelException e) {
			return 0;
		}
	}

	@Override
	public int getJavaElementType() {
		return IJavaElement.PACKAGE_FRAGMENT;
	}

	@Override
	public IModelRuntimeInfo createOrAddToParentInfo() {
		PackageRootRuntimeInfo packageRootRuntimeInfo = new PackageRootRuntimeInfo(getParentElement());
		packageRootRuntimeInfo.add(this);
		return packageRootRuntimeInfo;
	}

	@Override
	protected IPackageFragmentRoot getParentElement() {
		return (IPackageFragmentRoot) ((IPackageFragment) javaElement).getParent();
	}
}
