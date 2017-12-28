/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.settings;

import java.io.File;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import learntest.core.commons.LearntestConstants;
import learntest.plugin.utils.IProjectUtils;
import learntest.plugin.utils.IStatusUtils;

/**
 * @author LLT
 *
 */
public class ProjectSetting {

	public static String createOutputFolderInWorkspace() {
		String workspace = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
		File learntest = new File(workspace, LearntestConstants.REPORT_FOLDER);
		if (!learntest.exists()) {
			learntest.mkdir();
		}
		return learntest.getAbsolutePath();
	}
	
	public static String getLearntestOutputFolder(String projectName) throws CoreException {
		if (projectName == null) {
			return createOutputFolderInWorkspace();
		}
		IProject project = IProjectUtils.getProject(projectName);
		if (project == null || !project.exists() || !project.isOpen()) {
			throw new CoreException(IStatusUtils.error(String.format("Project %s is not open!", projectName)));
		}
		IFolder outputFolder = project.getFolder(LearntestConstants.REPORT_FOLDER);
		if (!outputFolder.exists()) {
			outputFolder.create(IResource.NONE, true, null);
		}
		return outputFolder.getLocation().toOSString();
	}
}
