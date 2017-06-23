/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.handler;

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import learntest.core.commons.data.classinfo.JunitTestsInfo;
import learntest.core.commons.data.classinfo.TargetClass;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.core.gentest.GentestParams;
import learntest.core.gentest.TestGenerator;
import learntest.core.gentest.TestGenerator.GentestResult;
import learntest.main.LearnTestConfig;
import learntest.main.LearnTestParams;
import learntest.main.LearnTestParams.LearntestSystemVariable;
import learntest.plugin.utils.IProjectUtils;
import learntest.plugin.utils.IResourceUtils;
import learntest.plugin.utils.IStatusUtils;
import learntest.plugin.utils.JdartConstants;
import learntest.util.LearnTestUtil;
import sav.common.core.Constants;
import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
import sav.common.core.SavRtException;
import sav.common.core.SystemVariables;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.vm.JavaCompiler;
import sav.strategies.vm.VMConfiguration;

/**
 * @author LLT
 *
 */
public abstract class AbstractLearntestHandler extends AbstractHandler {
	private AppJavaClassPath appClasspath;
	
	/* PLUGIN HANDLER  */
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
	
	protected abstract IStatus execute(IProgressMonitor monitor);

	protected abstract String getJobName();
	
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
	
	protected void prepareData() throws CoreException {
		appClasspath = initAppJavaClassPath();
	}
	
	protected LearnTestParams prepareLearnTestData() throws SavException {
		LearnTestParams params = initLearntestParams();
		GentestParams gentestParams = initGentestParams(params);
		gentestParams.setGenerateMainClass(true);
		/* generate testcase and jdart entry */
		GentestResult testResult = generateTestcases(gentestParams);
		params.setInitialTests(new JunitTestsInfo(testResult, getAppClasspath().getClassLoader()));
		return params;
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
	
	protected TargetMethod initTargetMethod(String className, CompilationUnit cu, MethodDeclaration method) {
		String simpleMethodName = method.getName().getIdentifier();
//		 cu.getLineNumber(method.getName().getStartPosition())
		int lineNumber = cu.getLineNumber(((ASTNode)method.getBody().statements().get(0)).getStartPosition());
		TargetClass targetClass = new TargetClass(className);
		TargetMethod targetMethod = new TargetMethod(targetClass);
		targetMethod.setMethodName(simpleMethodName);
		targetMethod.setLineNum(lineNumber);
		targetMethod.setMethodSignature(LearnTestUtil.getMethodSignature(method));
		List<String> paramNames = new ArrayList<String>(CollectionUtils.getSize(method.parameters()));
		List<String> paramTypes = new ArrayList<String>(paramNames.size());
		for(Object obj: method.parameters()){
			if(obj instanceof SingleVariableDeclaration){
				SingleVariableDeclaration svd = (SingleVariableDeclaration)obj;
				paramNames.add(svd.getName().getIdentifier());
				paramTypes.add(svd.getType().toString());
			}
		}
		targetMethod.setParams(paramNames);
		targetMethod.setParamTypes(paramTypes);
		
		/* TODO to remove*/
		LearnTestConfig.targetMethodName = simpleMethodName;
		LearnTestConfig.targetMethodLineNum = String.valueOf(lineNumber);
		return targetMethod;
	}
	
	protected LearnTestParams initLearntestParams() {
		try {
			LearnTestParams params = LearnTestParams.initFromLearnTestConfig();
			setSystemConfig(params);
			return params;
		} catch (SavException e) {
			throw new SavRtException(e);
		}
	}
	
	public void setSystemConfig(LearnTestParams params) {
		params.getSystemConfig().set(LearntestSystemVariable.JDART_APP_PROPRETIES, 
				IResourceUtils.getResourceAbsolutePath(JdartConstants.BUNDLE_ID, "libs/jdart/jpf.properties"));
		params.getSystemConfig().set(LearntestSystemVariable.JDART_SITE_PROPRETIES, 
				IResourceUtils.getResourceAbsolutePath(JdartConstants.BUNDLE_ID, "libs/jpf.properties"));
	}
	
	protected void log(String logStr) {
		System.out.println(logStr);
	}
	
	/* END PLUGIN HANDLER  */
	protected GentestResult generateTestcases(GentestParams params) throws SavException {
		try {
			AppJavaClassPath appClasspath = getAppClasspath();
			TestGenerator testGenerator = new TestGenerator(appClasspath);
			JavaCompiler compiler = new JavaCompiler(new VMConfiguration(appClasspath));
			GentestResult result = testGenerator.genTest(params);
			compiler.compile(appClasspath.getTestTarget(), result.getAllFiles());
			return result;
		} catch (Exception e) {
			throw new SavException(ModuleEnum.UNSPECIFIED, e, e.getMessage());
		}
	}
	

}
/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.handler;

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import learntest.core.commons.data.classinfo.JunitTestsInfo;
import learntest.core.commons.data.classinfo.TargetClass;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.core.gentest.GentestParams;
import learntest.core.gentest.GentestResult;
import learntest.core.gentest.TestGenerator;
import learntest.main.LearnTestConfig;
import learntest.main.LearnTestParams;
import learntest.main.LearnTestParams.LearntestSystemVariable;
import learntest.plugin.utils.IProjectUtils;
import learntest.plugin.utils.IResourceUtils;
import learntest.plugin.utils.IStatusUtils;
import learntest.plugin.utils.JdartConstants;
import learntest.util.LearnTestUtil;
import sav.common.core.Constants;
import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
import sav.common.core.SavRtException;
import sav.common.core.SystemVariables;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.vm.JavaCompiler;
import sav.strategies.vm.VMConfiguration;

/**
 * @author LLT
 *
 */
public abstract class AbstractLearntestHandler extends AbstractHandler {
	private AppJavaClassPath appClasspath;
	
	/* PLUGIN HANDLER  */
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
	
	protected abstract IStatus execute(IProgressMonitor monitor);

	protected abstract String getJobName();
	
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
	
	protected void prepareData() throws CoreException {
		appClasspath = initAppJavaClassPath();
	}
	
	protected LearnTestParams prepareLearnTestData() throws SavException {
		LearnTestParams params = initLearntestParams();
		GentestParams gentestParams = initGentestParams(params);
		gentestParams.setGenerateMainClass(true);
		/* generate testcase and jdart entry */
		GentestResult testResult = generateTestcases(gentestParams);
		params.setInitialTests(new JunitTestsInfo(testResult, getAppClasspath().getClassLoader()));
		return params;
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
	
	protected TargetMethod initTargetMethod(String className, CompilationUnit cu, MethodDeclaration method) {
		String simpleMethodName = method.getName().getIdentifier();
//		 cu.getLineNumber(method.getName().getStartPosition())
		int lineNumber = cu.getLineNumber(((ASTNode)method.getBody().statements().get(0)).getStartPosition());
		TargetClass targetClass = new TargetClass(className);
		TargetMethod targetMethod = new TargetMethod(targetClass);
		targetMethod.setMethodName(simpleMethodName);
		targetMethod.setLineNum(lineNumber);
		targetMethod.setMethodSignature(LearnTestUtil.getMethodSignature(method));
		List<String> paramNames = new ArrayList<String>(CollectionUtils.getSize(method.parameters()));
		List<String> paramTypes = new ArrayList<String>(paramNames.size());
		for(Object obj: method.parameters()){
			if(obj instanceof SingleVariableDeclaration){
				SingleVariableDeclaration svd = (SingleVariableDeclaration)obj;
				paramNames.add(svd.getName().getIdentifier());
				paramTypes.add(svd.getType().toString());
			}
		}
		targetMethod.setParams(paramNames);
		targetMethod.setParamTypes(paramTypes);
		
		/* TODO to remove*/
		LearnTestConfig.targetMethodName = simpleMethodName;
		LearnTestConfig.targetMethodLineNum = String.valueOf(lineNumber);
		return targetMethod;
	}
	
	protected LearnTestParams initLearntestParams() {
		try {
			LearnTestParams params = LearnTestParams.initFromLearnTestConfig();
			setSystemConfig(params);
			return params;
		} catch (SavException e) {
			throw new SavRtException(e);
		}
	}
	
	public void setSystemConfig(LearnTestParams params) {
		params.getSystemConfig().set(LearntestSystemVariable.JDART_APP_PROPRETIES, 
				IResourceUtils.getResourceAbsolutePath(JdartConstants.BUNDLE_ID, "libs/jdart/jpf.properties"));
		params.getSystemConfig().set(LearntestSystemVariable.JDART_SITE_PROPRETIES, 
				IResourceUtils.getResourceAbsolutePath(JdartConstants.BUNDLE_ID, "libs/jpf.properties"));
	}
	
	/* END PLUGIN HANDLER  */
	protected GentestResult generateTestcases(GentestParams params) throws SavException {
		try {
			AppJavaClassPath appClasspath = getAppClasspath();
			TestGenerator testGenerator = new TestGenerator(appClasspath);
			JavaCompiler compiler = new JavaCompiler(new VMConfiguration(appClasspath));
			GentestResult result = testGenerator.genTest(params);
			compiler.compile(appClasspath.getTestTarget(), result.getAllFiles());
			return result;
		} catch (Exception e) {
			throw new SavException(ModuleEnum.UNSPECIFIED, e, e.getMessage());
		}
	}
	

}
