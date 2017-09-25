/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.handler.gentest;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;

import cfgcoverage.jacoco.CfgJaCoCoConfigs;
import learntest.core.LearnTestParams.LearntestSystemVariable;
import learntest.plugin.commons.PluginException;
import learntest.plugin.utils.IProjectUtils;
import learntest.plugin.utils.IResourceUtils;
import learntest.plugin.utils.IStatusUtils;
import learntest.plugin.utils.JdartConstants;
import sav.common.core.Constants;
import sav.common.core.SavRtException;
import sav.common.core.SystemVariables;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.SystemPreferences;

/**
 * @author LLT
 *
 */
public class GentestSettings {
	
	public static AppJavaClassPath initAppJavaClassPath(IJavaProject javaProject) {
		try {
			AppJavaClassPath appClasspath = new AppJavaClassPath();
			appClasspath.setJavaHome(IProjectUtils.getJavaHome(javaProject));
			String outputPath = IProjectUtils.getTargetFolder(javaProject);
			appClasspath.setTarget(outputPath);
			appClasspath.setTestTarget(outputPath);
			/* create l2t-test folder in target project */
			String testSrc = IProjectUtils.createSourceFolder(javaProject, "l2t_test");
			appClasspath.setTestSrc(testSrc);
			appClasspath.addClasspaths(IProjectUtils.getPrjectClasspath(javaProject));
			GentestSettings.configureSystemPreferences(appClasspath.getPreferences(), javaProject);
			return appClasspath;
		} catch (CoreException ex) {
			throw new SavRtException(ex);
		}
	}
	
	public static void configureSystemPreferences(SystemPreferences preferences, IJavaProject javaProject) {
		preferences.set(SystemVariables.PROJECT_CLASSLOADER, IProjectUtils.getPrjClassLoader(javaProject));
		preferences.set(SystemVariables.TESTCASE_TIMEOUT, Constants.DEFAULT_JUNIT_TESTCASE_TIMEOUT);
		preferences.set(CfgJaCoCoConfigs.DUPLICATE_FILTER, true);
	}
	
	public static void configureJdart(SystemPreferences preferences) throws CoreException {
		try {
			preferences.set(LearntestSystemVariable.JDART_APP_PROPRETIES,
					IResourceUtils.getResourceAbsolutePath(JdartConstants.BUNDLE_ID, "libs/jdart/jpf.properties"));
			preferences.set(LearntestSystemVariable.JDART_SITE_PROPRETIES,
					IResourceUtils.getResourceAbsolutePath(JdartConstants.BUNDLE_ID, "libs/jpf.properties"));
		} catch (PluginException e) {
			throw new CoreException(IStatusUtils.exception(e, e.getMessage()));
		}
	}
}
