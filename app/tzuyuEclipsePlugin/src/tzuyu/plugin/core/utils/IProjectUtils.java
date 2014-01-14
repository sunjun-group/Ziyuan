/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.core.utils;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

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
		IWorkspaceRoot workspaceRoot = project.getProject().getWorkspace().getRoot();
		// convert from text to iPackageFragementRoot;
		IPath pkgFrgRootPath = project.getPath().append(relativePath);
		IPackageFragmentRoot result = null;
		for (IProject proj : workspaceRoot.getProjects()) {
			IJavaProject javaProj = JavaCore.create(proj);
			try {
				result = javaProj.findPackageFragmentRoot(pkgFrgRootPath);
			} catch (JavaModelException e) {
				result = null;
			}
			if (result != null) {
				break;
			}
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
