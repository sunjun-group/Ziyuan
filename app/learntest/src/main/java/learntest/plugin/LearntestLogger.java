/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import learntest.plugin.utils.IProjectUtils;
import learntest.plugin.utils.IStatusUtils;

/**
 * @author LLT
 *
 */
public class LearntestLogger {

	public static void initLog4j(String projectName) throws CoreException {
		try {
			ResourceBundle log4j = ResourceBundle.getBundle("learntest_log4j");
			Properties props = new Properties();
			for (String key : log4j.keySet()) {
				props.setProperty(key, log4j.getString(key));
			}
			props.setProperty("log4j.appender.file.File", getLearntestLogFile(projectName));
			PropertyConfigurator.configure(props);
		} catch (Exception e) {
			throw new CoreException(IStatusUtils.exception(e, e.getMessage()));
		}
	}

	/**
	 * TODO LLT: using reset configuration to set the corresponding log file for each project, instead of
	 * using global log file for workspace. 
	 */
	private static String getLearntestLogFile(String projectName) throws CoreException {
		if (projectName == null) {
			return getLearntestLogFileInWorkspace();
		}
		IProject project = IProjectUtils.getProject(projectName);
		if (project == null || !project.exists() || !project.isOpen()) {
			throw new CoreException(IStatusUtils.error(String.format("Project %s is not open!", projectName)));
		}
		IFolder logFolder = project.getFolder("log");
		if (!logFolder.exists()) {
			logFolder.create(IResource.NONE, true, null);
		}
		IFile logFile = logFolder.getFile("learntest-eclipse.log");
		if (!logFile.exists()) {
			logFile.create(new ByteArrayInputStream("".getBytes()), IResource.NONE, null);
		}
		return logFile.getLocation().toOSString();
	}
	
	private static String getLearntestLogFileInWorkspace() throws CoreException {
		try {
			String workspace = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
			File logFile = new File(workspace, "learntest-eclipse.log");
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
			return logFile.getAbsolutePath();
		} catch (IOException e) {
			throw new CoreException(IStatusUtils.exception(e, e.getMessage()));
		}
	}
}
