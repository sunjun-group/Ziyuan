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
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.core.JDartLearntest;
import learntest.core.commons.data.LearnTestApproach;
import learntest.core.commons.data.classinfo.JunitTestsInfo;
import learntest.core.commons.data.classinfo.TargetClass;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.core.gentest.GentestParams;
import learntest.core.gentest.GentestResult;
import learntest.core.gentest.TestGenerator;
import learntest.exception.LearnTestException;
import learntest.io.excel.Trial;
import learntest.main.LearnTestConfig;
import learntest.main.LearnTestParams;
import learntest.main.LearnTestParams.LearntestSystemVariable;
import learntest.main.RunTimeInfo;
import learntest.plugin.utils.IMethodUtils;
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
import sav.settings.SAVTimer;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.vm.JavaCompiler;
import sav.strategies.vm.VMConfiguration;

/**
 * @author LLT
 *
 */
public abstract class AbstractLearntestHandler extends AbstractHandler {
	private AppJavaClassPath appClasspath;
	private Logger log = LoggerFactory.getLogger(AbstractLearntestHandler.class);
	
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
		int lineNumber = IMethodUtils.getStartLineNo(cu, method);
		TargetClass targetClass = new TargetClass(className);
		TargetMethod targetMethod = new TargetMethod(targetClass);
		targetMethod.setMethodName(simpleMethodName);
		targetMethod.setLineNum(lineNumber);
		targetMethod.setMethodLength(method.getLength());
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
	

	protected Trial evaluateLearntestForSingleMethod(LearnTestParams params) {
		try {
			// l2t params
			LearnTestParams l2tParams = params;
			// randoop params
			LearnTestParams randoopParam = params.createNew();
			
			RunTimeInfo l2tAverageInfo = new RunTimeInfo();
			RunTimeInfo ranAverageInfo = new RunTimeInfo();
			
			l2tParams.setApproach(LearnTestApproach.L2T);
			log.info("run jdart..");
			RunTimeInfo jdartInfo = runJdart(l2tParams);
			
			log.info("run l2t..");
			runLearntest(l2tAverageInfo, l2tParams);
			
			randoopParam.setApproach(LearnTestApproach.RANDOOP);
			log.info("run randoop..");
			runLearntest(ranAverageInfo, randoopParam);
			
			if (l2tAverageInfo.isNotZero() && ranAverageInfo.isNotZero()) {
				TargetMethod method = params.getTargetMethod();
				return new Trial(method.getMethodFullName(), method.getMethodLength(), method.getLineNum(),
						l2tAverageInfo, ranAverageInfo, jdartInfo);
			}
		} catch (Exception e) {
			handleException(e);
		}
		return null;
	}
	
	private RunTimeInfo runJdart(LearnTestParams params) throws Exception {
		JDartLearntest learntest = new JDartLearntest(getAppClasspath());
		return learntest.jdart(params);
	}

	private RunTimeInfo runLearntest(RunTimeInfo runInfo, LearnTestParams params) throws Exception {
		RunTimeInfo l2tInfo = runLearntest(params);
		if (runInfo != null && l2tInfo != null) {
			runInfo.add(l2tInfo);
		} else {
			runInfo = null;
		}
		Thread.sleep(5000);
		return runInfo;
	}
	
	/**
	 * To test new version of learntest which uses another cfg and jacoco for code coverage. 
	 */
	 public RunTimeInfo runLearntest(LearnTestParams params) throws Exception {
		try {
			SAVTimer.enableExecutionTimeout = true;
			SAVTimer.exeuctionTimeout = 50000000;
			learntest.core.LearnTest learntest = new learntest.core.LearnTest(getAppClasspath());
			RunTimeInfo runtimeInfo = learntest.run(params);
			refreshProject();

			if(runtimeInfo != null) {
				log.info("{} time: {}; coverage: {}", params.getApproach().getName(), runtimeInfo.getTime(), 
						runtimeInfo.getCoverage());
			}
			return runtimeInfo;
		} catch (Exception e) {
			throw new LearnTestException(e);
		}
	}
}
