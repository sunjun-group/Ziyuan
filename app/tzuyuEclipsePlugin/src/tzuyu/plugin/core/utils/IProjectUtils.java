/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.core.utils;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

import tzuyu.engine.model.exception.TzRuntimeException;

/**
 * @author LLT
 *
 */
public class IProjectUtils {
	private IProjectUtils() {}
	
	public static String toRelativePath(IJavaElement child, IJavaElement root) {
		return child.getPath().makeRelativeTo(root.getPath()).toString();
	}
	
	public static IPackageFragmentRoot toPackageFragmentRoot(IJavaProject project, String relativePath) {
		// convert from text to iPackageFragementRoot;
		IPackageFragmentRoot result = null;
		IPath pkgFrgRootPath = project.getPath().append(relativePath);
		try {
			result = project.findPackageFragmentRoot(pkgFrgRootPath);
		} catch (JavaModelException e1) {
			throw new TzRuntimeException("cannot find package roots");
		}
		return result;
	}
	
	public static IPackageFragment toPackageFragment(
			IPackageFragmentRoot root, String packageName) {
		if (StringUtils.isBlank(packageName)) {
			return null;
		}
		if (root != null) {
			return root.getPackageFragment(packageName);
		}
		return null;
	}
	
    public static IPath relativeToAbsolute(IPath relativePath) {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IResource resource = root.findMember(relativePath);
        if (resource != null) {
            return resource.getLocation();
        }
        return relativePath;
    }
}
