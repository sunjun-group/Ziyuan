/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.handler;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;

import learntest.main.LearnTestConfig;
import learntest.plugin.utils.IProjectUtils;
import learntest.util.LearnTestUtil;
import sav.common.core.SystemVariables;
import sav.strategies.dto.AppJavaClassPath;

/**
 * @author LLT
 *
 */
public class HandlerUtils {
	private HandlerUtils(){}
	
	public static void refreshProject(){
		IWorkspaceRoot myWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject iProject = myWorkspaceRoot.getProject(LearnTestConfig.projectName);
		
		try {
			iProject.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	public static AppJavaClassPath initAppJavaClassPath() throws CoreException {
		IProject project = IProjectUtils.getProject(LearnTestConfig.projectName);
		IJavaProject javaProject = IProjectUtils.getJavaProject(project);
		AppJavaClassPath appClasspath = new AppJavaClassPath();
		appClasspath.setJavaHome(IProjectUtils.getJavaHome(javaProject));
		appClasspath.addClasspaths(LearnTestUtil.getPrjectClasspath());
		String outputPath = LearnTestUtil.getOutputPath();
		appClasspath.setTarget(outputPath);
		appClasspath.setTestTarget(outputPath);
		appClasspath.getPreferences().set(SystemVariables.PROJECT_CLASSLOADER, LearnTestUtil.getPrjClassLoader());
		return appClasspath;
	}
}
