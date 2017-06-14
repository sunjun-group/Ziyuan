/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;

import learntest.core.gentest.GentestParams;
import learntest.main.LearnTestConfig;
import learntest.main.LearnTestParams;
import learntest.plugin.utils.IProjectUtils;
import learntest.plugin.utils.IStatusUtils;
import learntest.util.LearnTestUtil;
import sav.common.core.Constants;
import sav.common.core.SavRtException;
import sav.common.core.SystemVariables;
import sav.strategies.dto.AppJavaClassPath;

/**
 * @author LLT
 *
 */
public abstract class AbstractLearntestHandler extends AbstractHandler {
	private AppJavaClassPath appClasspath;

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		Job job = new Job(getJobName()) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					prepareData();
					execute(monitor);
				} catch (CoreException e) {
					e.printStackTrace();
				} finally {
					monitor.done();
				}
				return IStatusUtils.afterRunning(monitor);
			}
		};
		job.schedule();

		return null;
	}
	
	protected void prepareData() throws CoreException {
		appClasspath = initAppJavaClassPath();
	}
	
	public AppJavaClassPath getAppClasspath() {
		if (appClasspath == null) {
			appClasspath = initAppJavaClassPath();
		}
		return appClasspath;
	}
	
	protected GentestParams initGentestParams(LearnTestParams learntestParams) {
		return learntestParams.initGentestParams(getAppClasspath());
	}
	
	private AppJavaClassPath initAppJavaClassPath() {
		try {
			IProject project = IProjectUtils.getProject(LearnTestConfig.projectName);
			IJavaProject javaProject = IProjectUtils.getJavaProject(project);
			AppJavaClassPath appClasspath = new AppJavaClassPath();
			appClasspath.setJavaHome(IProjectUtils.getJavaHome(javaProject));
			appClasspath.addClasspaths(LearnTestUtil.getPrjectClasspath());
			String outputPath = LearnTestUtil.getOutputPath();
			appClasspath.setTarget(outputPath);
			appClasspath.setTestTarget(outputPath);
			appClasspath.setTestSrc(LearnTestUtil.retrieveTestSourceFolder());
			appClasspath.getPreferences().set(SystemVariables.PROJECT_CLASSLOADER, LearnTestUtil.getPrjClassLoader());
			appClasspath.getPreferences().set(SystemVariables.TESTCASE_TIMEOUT,
					Constants.DEFAULT_JUNIT_TESTCASE_TIMEOUT);
			return appClasspath;
		} catch (CoreException ex) {
			throw new SavRtException(ex);
		}
	}
	
	public void refreshProject(){
		IWorkspaceRoot myWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject iProject = myWorkspaceRoot.getProject(LearnTestConfig.projectName);
		
		try {
			iProject.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	protected void handleException(Exception e) {
		e.printStackTrace();
	}

	protected abstract IStatus execute(IProgressMonitor monitor);

	protected abstract String getJobName();

}
