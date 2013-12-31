/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.core.utils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;

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
}
