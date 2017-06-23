/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;

import sav.common.core.SavRtException;

/**
 * @author LLT
 *
 */
public class IProjectUtils {
	public IProjectUtils(){}
	
	public static IJavaProject getJavaProject(IProject project) {
		return JavaCore.create(project);
	}

	/**
	 * Returns the IProject by the specified name in the workspace. To convert
	 * the project to a Java project use JavaCore.create(project)
	 */
	public static IProject getProject(String projectName) {
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
	
	public static String getJavaHome(IJavaProject project) throws CoreException {
		IVMInstall vmInstall = JavaRuntime.getVMInstall(project);
		return vmInstall.getInstallLocation().getAbsolutePath();
	}
	
	public static List<String> getPrjectClasspath(IJavaProject project) {
		try {
			String[] classPathEntries = JavaRuntime.computeDefaultRuntimeClassPath(project);
			return Arrays.asList(classPathEntries);
		} catch (CoreException e) {
			throw new SavRtException(e);
		}
	}
	
	public static List<IPackageFragmentRoot> findTargetSourcePkgRoots(IJavaProject project) {
		List<IPackageFragmentRoot> roots = new ArrayList<>();
		try {
			for (IPackageFragmentRoot packageFragmentRoot : project.getPackageFragmentRoots()) {
				if (packageFragmentRoot.getKind() == IPackageFragmentRoot.K_SOURCE
						&& !startWith(packageFragmentRoot.getResource().getProjectRelativePath().toString(), "src/test",
								"test")) {
					roots.add(packageFragmentRoot);
				}
			}

		} catch (JavaModelException e1) {
			e1.printStackTrace();
		}

		return roots;
	}

	private static boolean startWith(String name, String... prefixes) {
		for (String prefix : prefixes) {
			if (name.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}
	
	
}
