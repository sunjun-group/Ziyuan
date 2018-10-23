/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.activelearning.plugin.settings;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;

import cfgcoverage.jacoco.CfgJaCoCoParams;
import learntest.activelearning.plugin.utils.ActiveLearnTestConfig;
import sav.common.core.Constants;
import sav.common.core.SavRtException;
import sav.common.core.SystemVariables;
import sav.eclipse.plugin.IProjectUtils;
import sav.eclipse.plugin.IResourceUtils;
import sav.eclipse.plugin.PluginException;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.SystemPreferences;

/**
 * @author LLT
 *
 */
public class GentestSettings {
	
	public static AppJavaClassPath getConfigAppClassPath(ActiveLearnTestConfig config) {
		return getConfigAppClassPath(config.getProjectName());
	}
	
	public static AppJavaClassPath getConfigAppClassPath(String projectName) {
		IProject project = IProjectUtils.getProject(projectName);
		IJavaProject javaProject = IProjectUtils.getJavaProject(project);
		return GentestSettings.initAppJavaClassPath(javaProject);
	}
	
	public static AppJavaClassPath initAppJavaClassPath(IJavaProject javaProject) {
		try {
			AppJavaClassPath appClasspath = new AppJavaClassPath();
			appClasspath.setJavaHome(IProjectUtils.getJavaHome(javaProject));
			String outputPath = IProjectUtils.getTargetFolder(javaProject);
			appClasspath.setTarget(outputPath);
			appClasspath.setTestTarget(outputPath);
			/* create l2t-test folder in target project */
			String testSrc = IProjectUtils.createFolder(javaProject, "l2t_test");
			appClasspath.setTestSrc(testSrc);
			appClasspath.addClasspaths(IProjectUtils.getPrjectClasspath(javaProject));
			String projectPath = IResourceUtils.relativeToAbsolute(javaProject.getPath()).toOSString();
			appClasspath.setWorkingDirectory(projectPath);
			GentestSettings.configureSystemPreferences(appClasspath.getPreferences(), javaProject);
			return appClasspath;
		} catch (CoreException ex) {
			throw new SavRtException(ex);
		}
	}
	
	public static void configureSystemPreferences(SystemPreferences preferences, IJavaProject javaProject) {
		preferences.set(SystemVariables.PROJECT_CLASSLOADER, IProjectUtils.getPrjClassLoader(javaProject));
		preferences.set(SystemVariables.TESTCASE_TIMEOUT, Constants.DEFAULT_JUNIT_TESTCASE_TIMEOUT);
	}
	

}
