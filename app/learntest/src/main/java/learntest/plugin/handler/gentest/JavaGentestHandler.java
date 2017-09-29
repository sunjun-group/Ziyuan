/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.handler.gentest;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.core.LearnTestParams;
import learntest.core.RunTimeInfo;
import learntest.core.commons.data.LearnTestApproach;
import learntest.core.commons.data.classinfo.MethodInfo;
import learntest.plugin.LearntestLogger;
import learntest.plugin.LearntestPlugin;
import learntest.plugin.commons.PluginException;
import learntest.plugin.commons.data.ClassRuntimeInfo;
import learntest.plugin.commons.data.IModelRuntimeInfo;
import learntest.plugin.commons.data.JavaModelRuntimeInfo;
import learntest.plugin.commons.data.MethodRuntimeInfo;
import learntest.plugin.commons.data.PackageRootRuntimeInfo;
import learntest.plugin.commons.data.PackageRuntimeInfo;
import learntest.plugin.commons.data.ProjectRuntimeInfo;
import learntest.plugin.commons.data.TypeRunTimeInfo;
import learntest.plugin.commons.event.IJavaGentestEventManager;
import learntest.plugin.commons.event.JavaGentestEvent;
import learntest.plugin.handler.TargetMethodConverter;
import learntest.plugin.handler.gentest.filter.GentestMethodFilter;
import learntest.plugin.handler.gentest.filter.GentestTypeFilter;
import learntest.plugin.utils.AstUtils;
import learntest.plugin.utils.IStatusUtils;
import learntest.plugin.utils.WorkbenchUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.TextFormatUtils;
import sav.settings.SAVTimer;
import sav.strategies.dto.AppJavaClassPath;

/**
 * @author LLT
 *
 */
public class JavaGentestHandler extends AbstractHandler {
	private static Logger log = LoggerFactory.getLogger(JavaGentestHandler.class);
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		final String approach = event.getParameter("learntest.approach");
		Job job = new Job(getJobName()) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					return execute(LearnTestApproach.valueOf(approach), selection, monitor);
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

	protected IStatus execute(LearnTestApproach approach, ISelection arg, IProgressMonitor monitor) throws CoreException {
		Object[] elements = null;
		if (arg instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) arg;
			elements = selection.toArray();
		} else if (arg instanceof TextSelection) {
			TextSelection selection = (TextSelection) arg;
			try {
				elements = WorkbenchUtils.getSelectedJavaElement(selection);
			} catch (PluginException e) {
				log.error(e.getMessage());
			}
		}
		if (CollectionUtils.isNotEmpty(elements)) {
			getEventManager().fireGentestStartEvent();
			GentestWorkObject workObject = new GentestWorkObject();
			// elements must not be empty as being configured in plugin.xml
			for (Object obj : elements) {
				if (obj instanceof IJavaElement) {
					workObject.extend((IJavaElement) obj);
				}
			}
			JavaModelRuntimeInfo runtimeInfo = runGentest(workObject, approach);
			getEventManager().fireOnChangedEvent(new JavaGentestEvent(runtimeInfo));
		}
		return IStatusUtils.ok();
	}
	
	private JavaModelRuntimeInfo runGentest(GentestWorkObject workObject, LearnTestApproach approach) {
		JavaModelRuntimeInfo result = new JavaModelRuntimeInfo(workObject);
		for (WorkProject workProject : workObject.getWorkProjects()) {
			AppJavaClassPath appClasspath = GentestSettings.initAppJavaClassPath(workProject.getProject());
			LearnTestParams params = new LearnTestParams(appClasspath);
			params.setApproach(approach);
			MethodCollector methodCollector = new MethodCollector(GentestTypeFilter.createFilters(), 
					GentestMethodFilter.createFilters());
			try {
				LearntestLogger.initLog4j(workProject.getProject().getProject().getName());
				for (IJavaElement element : workProject.getToTestElements()) {
					try {
						IModelRuntimeInfo runtimeInfo = runGentest(params, element, methodCollector);
						result.add(runtimeInfo);
					} catch (PluginException e) {
						log.error(e.getMessage());
					}
				}
			} catch (PluginException e) {
				log.error(e.getMessage());
			}
			refreshProject(workProject.getProject());
		}
		return result;
	}
	
	private IModelRuntimeInfo runGentest(LearnTestParams params, IJavaElement element, MethodCollector methodCollector) throws PluginException {
		switch (element.getElementType()) {
		case IJavaElement.JAVA_PROJECT:
			return runGentestOnProject(params, (IJavaProject) element, methodCollector);
		case IJavaElement.PACKAGE_FRAGMENT_ROOT:
			return runGentestOnPackageFragmentRoot(params, (IPackageFragmentRoot) element, methodCollector);
		case IJavaElement.PACKAGE_FRAGMENT:
			return runGentestOnPackageFragment(params, (IPackageFragment) element, methodCollector);
		case IJavaElement.COMPILATION_UNIT:
			return runGentestOnCompilationUnit(params, (ICompilationUnit) element, methodCollector);
		case IJavaElement.TYPE:
			return runGentestOnType(params, (IType) element, methodCollector);
		case IJavaElement.METHOD:
			return runGentestOnMethod(params, (IMethod) element);
		}
		return null;
	}
	
	/**
	 * For IJavaProject.
	 */
	private ProjectRuntimeInfo runGentestOnProject(LearnTestParams params, IJavaProject element, MethodCollector collector) {
		try {
			ProjectRuntimeInfo result = new ProjectRuntimeInfo(element);
			for (IPackageFragmentRoot pkgFragment : element.getPackageFragmentRoots()) {
				PackageRootRuntimeInfo runtimeInfo = runGentestOnPackageFragmentRoot(params, pkgFragment, collector);
				if (runtimeInfo != null) {
					result.add(runtimeInfo);
				}
			}
			return result;
		} catch (JavaModelException e) {
			log.error(e.getMessage());
			return null;
		}
	}
	
	/**
	 * For IPackageFragmentRoot.
	 */
	private PackageRootRuntimeInfo runGentestOnPackageFragmentRoot(LearnTestParams params, IPackageFragmentRoot element,
			MethodCollector methodCollector) {
		try {
			PackageRootRuntimeInfo result = new PackageRootRuntimeInfo(element);
			for (IJavaElement child : element.getChildren()) {
				if (child.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
					PackageRuntimeInfo runtimeInfo = runGentestOnPackageFragment(params, (IPackageFragment) child,
							methodCollector);
					if (runtimeInfo != null) {
						result.add(runtimeInfo);
					}
				}
			}
			return result;
		} catch (JavaModelException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	/**
	 * For IPackageFragment.
	 */
	private PackageRuntimeInfo runGentestOnPackageFragment(LearnTestParams params, IPackageFragment element,
			MethodCollector methodCollector) {
		try {
			PackageRuntimeInfo result = new PackageRuntimeInfo(element);
			for (ICompilationUnit cu : element.getCompilationUnits()) {
				ClassRuntimeInfo runtimeInfo = runGentestOnCompilationUnit(params, cu, methodCollector);
				if (runtimeInfo != null) {
					result.add(runtimeInfo);
				}
			}
			return result;
		} catch (JavaModelException e) {
			log.error(e.getMessage());
			return null;
		}
	}
	
	/**
	 * For ICompilationUnit.
	 */
	private ClassRuntimeInfo runGentestOnCompilationUnit(LearnTestParams params, ICompilationUnit icu,
			MethodCollector collector) {
		CompilationUnit cu = AstUtils.toAstNode(icu);
		collector.reset(false);
		cu.accept(collector);
		try {
			ClassRuntimeInfo classRuntimeInfo = new ClassRuntimeInfo(icu);
			for (IType type : icu.getTypes()) {
				classRuntimeInfo.add(runGentestOnType(params, type, collector));
			}
			return classRuntimeInfo;
		} catch (JavaModelException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	/**
	 * For IType.
	 */
	private TypeRunTimeInfo runGentestOnType(LearnTestParams params, IType element, MethodCollector methodCollector) {
		try {
			CompilationUnit cu = AstUtils.toAstNode(element.getCompilationUnit());
			methodCollector.reset(false);
			TypeDeclaration type = AstUtils.findNode(cu, element);
			type.accept(methodCollector);
			List<IMethod> allMethods = new ArrayList<IMethod>();
			getAllMethods(element, allMethods);
			List<MethodRuntimeInfo> runtimeInfos = runGentestOnTargetMethodList(params, methodCollector.getResult(), cu,
					allMethods);
			return new TypeRunTimeInfo(element, runtimeInfos);
		} catch (PluginException e) {
			log.error(e.getMessage());
			return null;
		} catch (JavaModelException e) {
			log.error(e.getMessage());
			return null;
		}
	}
	
	private void getAllMethods(IType element, List<IMethod> allMethods) throws JavaModelException {
		CollectionUtils.addAll(allMethods, element.getMethods());
		for (IType type : element.getTypes()) {
			getAllMethods(type, allMethods);
		}
	}

	/**
	 * For IMethod.
	 */
	private MethodRuntimeInfo runGentestOnMethod(LearnTestParams params, IMethod method) {
		try {
			MethodInfo targetMethod = TargetMethodConverter.toTargetMethod(method);
			RunTimeInfo runtimeInfo = runGentestOnSingleMethod(params, targetMethod);
			return new MethodRuntimeInfo(method, runtimeInfo);
		} catch (PluginException e) {
			log.error(e.getMessage());
			return null;
		}
	}
	
	/**
	 * for list of MethodDeclaration.
	 */
	private List<MethodRuntimeInfo> runGentestOnTargetMethodList(LearnTestParams params,
			List<MethodDeclaration> targetMethods, CompilationUnit cu, List<IMethod> iMethods) throws PluginException {
		List<MethodRuntimeInfo> result = new ArrayList<MethodRuntimeInfo>();
		for (MethodDeclaration targetMethod : targetMethods) {
			MethodInfo method = TargetMethodConverter.toTargetMethod(cu, targetMethod);
			try {
				RunTimeInfo runtimeInfo = runGentestOnSingleMethod(params, method);
				result.add(new MethodRuntimeInfo(AstUtils.findImethod(targetMethod, iMethods), runtimeInfo));
			} catch (PluginException e) {
				log.error(e.getMessage());
			}
		}
		return result;
	}

	/**
	 * for Targetmethod.
	 */
	private RunTimeInfo runGentestOnSingleMethod(LearnTestParams params, MethodInfo targetMethod) throws PluginException {
		log.info("-----------------------------------------------------------------------------------------------");
		log.info("Working method: {}", targetMethod.getMethodFullName());		
		log.info("-----------------------------------------------------------------------------------------------");
		params.renew(targetMethod);
		return runLearntest(params);
	}
	
	private RunTimeInfo runLearntest(LearnTestParams params) throws PluginException {
		try {
			SAVTimer.enableExecutionTimeout = true;
			SAVTimer.exeuctionTimeout = 50000000;
			learntest.core.LearnTest learntest = new learntest.core.LearnTest(params.getAppClasspath());
			RunTimeInfo runtimeInfo = learntest.run(params);

			if (runtimeInfo != null) {
				if (runtimeInfo.getLineCoverageResult() != null) {
					log.info("Line coverage result:");
					log.info(runtimeInfo.getLineCoverageResult().getDisplayText());
				}
				log.info("{} RESULT:", StringUtils.upperCase(params.getApproach().getName()));
				log.info("TIME: {}; COVERAGE: {}; CNT: {}", TextFormatUtils.printTimeString(runtimeInfo.getTime()),
						runtimeInfo.getCoverage(), runtimeInfo.getTestCnt());
				log.info("TOTAL COVERAGE INFO: \n{}", runtimeInfo.getCoverageInfo());
			}
			return runtimeInfo;
		} catch (Exception e) {
			throw PluginException.wrapEx(e);
		}
	}
	
	public void refreshProject(IJavaProject project) {
		try {
			project.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (CoreException e) {
			log.warn("Error when refreshing project: {}", e.getMessage());
		}
	}
	
	protected IJavaGentestEventManager getEventManager() {
		return LearntestPlugin.getJavaGentestEventManager();
	}

	protected String getJobName() {
		return "JavaGenTest";
	}

}
