/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.commons.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import tzuyu.engine.utils.CollectionUtils;
import tzuyu.plugin.tester.preferences.SearchScope;
import tzuyu.plugin.tester.reporter.PluginLogger;

/**
 * @author LLT
 * 
 */
public class IResourceUtils {
	private IResourceUtils() {
	}

	/**
	 * Checks whether the given resource is a Java source file.
	 * 
	 * @param resource
	 *            The resource to check.
	 * @return <code>true</code> if the given resource is a Java source file,
	 *         <code>false</code> otherwise.
	 */
	public static boolean isJavaFile(IResource resource) {
		if (resource == null || (resource.getType() != IResource.FILE)) {
			return false;
		}
		String ex = resource.getFileExtension();
		return "java".equalsIgnoreCase(ex); //$NON-NLS-1$
	}

	/**
	 * Returns the IProject by the specified name in the workspace. To convert
	 * the project to a Java project use JavaCore.create(project)
	 * [Randoop code]
	 * @param projectName
	 *            the name of the project
	 * @return the project by the specific name, or <code>null</code> it was not
	 *         found
	 */
	public static IProject getProjectFromName(String projectName) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IStatus status = workspace.validateName(projectName, IResource.PROJECT);

		if (status.isOK()) {
			IProject project = workspace.getRoot().getProject(projectName);

			if (!project.exists())
				return null;

			return project;
		}
		return null;
	}

	public static List<IPackageFragment> filterSourcePkgs(
			IPackageFragment[] packageFragments, SearchScope scope) {
		if (CollectionUtils.isEmpty(packageFragments)) {
			return Arrays.asList(packageFragments);
		}
		List<IPackageFragment> result = new ArrayList<IPackageFragment>();
		boolean sourceJars = (scope == SearchScope.SOURCE_JARS);
		for (IPackageFragment pkg : packageFragments) {
			try {
				if ((sourceJars || (pkg.getKind() == IPackageFragmentRoot.K_SOURCE))
						&& !CollectionUtils.isEmpty(pkg.getCompilationUnits())) {
					result.add(pkg);
				}
			} catch (JavaModelException e) {
				PluginLogger.getLogger().logEx(e);
			}
		}
		return result;
	}
	
	public static IType getIType(IJavaProject project, Class<?> clazz) {
		try {
			return project.findType(clazz.getCanonicalName());
		} catch (JavaModelException e) {
			PluginLogger.getLogger().logEx(e);
			return null;
		}
	}
}
