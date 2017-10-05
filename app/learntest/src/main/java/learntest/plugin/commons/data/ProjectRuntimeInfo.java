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
import org.eclipse.jdt.core.JavaModelException;

/**
 * @author LLT
 *
 */
public class ProjectRuntimeInfo extends AbstractModelRuntimeInfo<PackageRootRuntimeInfo> implements IModelRuntimeInfo {

	public ProjectRuntimeInfo(IJavaProject element) {
		super(element, getSize(element));
	}

	private static int getSize(IJavaProject element) {
		try {
			return element.getPackageFragmentRoots().length;
		} catch (JavaModelException e) {
			return -1;
		}
	}

	@Override
	public int getJavaElementType() {
		return IJavaElement.JAVA_PROJECT;
	}

	@Override
	public IModelRuntimeInfo createOrAddToParentInfo() {
		return null;
	}
	
	@Override
	protected IJavaElement getParentElement() {
		return null;
	}
}
