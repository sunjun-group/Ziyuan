/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.commons.data;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;

/**
 * @author LLT
 *
 */
public class PackageRootRuntimeInfo extends AbstractModelRuntimeInfo<PackageRuntimeInfo> implements IModelRuntimeInfo {

	public PackageRootRuntimeInfo(IPackageFragmentRoot element) {
		super(element);
	}
	
	@Override
	public IModelRuntimeInfo createOrAddToParentInfo() {
		return new ProjectRuntimeInfo(getParentElement());
	}

	@Override
	protected IJavaProject getParentElement() {
		return ((IPackageFragmentRoot) javaElement).getJavaProject();
	}

	@Override
	public int getJavaElementType() {
		return IJavaElement.PACKAGE_FRAGMENT_ROOT;
	}
	
}
