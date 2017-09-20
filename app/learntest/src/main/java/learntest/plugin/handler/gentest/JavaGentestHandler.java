/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.handler.gentest;

import java.util.Iterator;
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
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.plugin.LearntestLogger;
import learntest.plugin.commons.PluginException;
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
		ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		String approach = event.getParameter("learntest.approach");
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
			GentestWorkObject workObject = new GentestWorkObject();
			// elements must not be empty as configured in plugin.xml
			for (Object obj : elements) {
				if (obj instanceof IJavaElement) {
					workObject.extend((IJavaElement) obj);
				}
			}
			runGentest(workObject, approach);
		}
		return IStatusUtils.ok();
	}
	
	private void runGentest(GentestWorkObject workObject, LearnTestApproach approach) {
		for (WorkProject workProject : workObject.getWorkProjects()) {
			AppJavaClassPath appClasspath = GentestSettings.initAppJavaClassPath(workProject.getProject());
			LearnTestParams params = new LearnTestParams(appClasspath);
			params.setApproach(approach);
			MethodCollector methodCollector = new MethodCollector(GentestTypeFilter.createFilters(), 
					GentestMethodFilter.createFilters());
			try {
				LearntestLogger.initLog4j(workProject.getProject().getProject().getName());
				for (Iterator<IJavaElement> it = workProject.elementIterator(); it.hasNext();) {
					try {
						IJavaElement element = it.next();
						runGentest(params, element, methodCollector);
					} catch (PluginException e) {
						log.error(e.getMessage());
					}
				}
			} catch (PluginException e) {
				log.error(e.getMessage());
			}
			refreshProject(workProject.getProject());
		}
	}
	
	private void runGentest(LearnTestParams params, IJavaElement element, MethodCollector methodCollector) throws PluginException {
		switch (element.getElementType()) {
		case IJavaElement.COMPILATION_UNIT:
			runGentestOnCompilationUnit(params, (ICompilationUnit) element, methodCollector);
			break;
		case IJavaElement.JAVA_PROJECT:
			runGentestOnProject(params, (IJavaProject) element, methodCollector);
			break;
		case IJavaElement.METHOD:
			runGentestOnMethod(params, (IMethod) element);
			break;
		case IJavaElement.PACKAGE_FRAGMENT:
			runGentestOnPackageFragment(params, (IPackageFragment) element, methodCollector);
			break;
		case IJavaElement.TYPE:
			runGentestOnType(params, (IType) element, methodCollector);
			break;
		case IJavaElement.PACKAGE_FRAGMENT_ROOT:
			runGentestOnPackageFragmentRoot(params, (IPackageFragmentRoot) element, methodCollector);
			break;
		}
	}
	
	/**
	 * For IJavaProject.
	 */
	private void runGentestOnProject(LearnTestParams params, IJavaProject element, MethodCollector collector) {
		try {
			for (IPackageFragment pkgFragment : element.getPackageFragments()) {
				runGentestOnPackageFragment(params, pkgFragment, collector);
			}
		} catch (JavaModelException e) {
			log.error(e.getMessage());
		}
	}
	
	/**
	 * For IPackageFragmentRoot.
	 */
	private void runGentestOnPackageFragmentRoot(LearnTestParams params, IPackageFragmentRoot element,
			MethodCollector methodCollector) {
		try {
			for (IJavaElement child : element.getChildren()) {
				switch (child.getElementType()) {
				case IJavaElement.PACKAGE_FRAGMENT:
					runGentestOnPackageFragment(params, (IPackageFragment) child, methodCollector);
					break;
				case IJavaElement.COMPILATION_UNIT:
					runGentestOnCompilationUnit(params, (ICompilationUnit) child, methodCollector);
					break;
				}
			}
		} catch (JavaModelException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * For IPackageFragment.
	 */
	private void runGentestOnPackageFragment(LearnTestParams params, IPackageFragment element,
			MethodCollector methodCollector) {
		try {
			for (ICompilationUnit cu : element.getCompilationUnits()) {
				runGentestOnCompilationUnit(params, cu, methodCollector);
			}
		} catch (JavaModelException e) {
			log.error(e.getMessage());
		}
	}
	
	/**
	 * For IType.
	 */
	private void runGentestOnType(LearnTestParams params, IType element, MethodCollector methodCollector) {
		try {
			CompilationUnit cu = AstUtils.toAstNode(element.getCompilationUnit());
			methodCollector.reset(false);
			TypeDeclaration type = AstUtils.findNode(cu, element);
			type.accept(methodCollector);
			runGentestOnTargetMethodList(params, methodCollector.getResult(), cu);
		} catch (PluginException e) {
			log.error(e.getMessage());
		}
		
	}

	/**
	 * For ICompilationUnit.
	 */
	private void runGentestOnCompilationUnit(LearnTestParams params, ICompilationUnit icu, MethodCollector collector) {
		CompilationUnit cu = AstUtils.toAstNode(icu);
		collector.reset(false);
		cu.accept(collector);
		try {
			runGentestOnTargetMethodList(params, collector.getResult(), cu);
		} catch (PluginException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * For IMethod.
	 */
	private void runGentestOnMethod(LearnTestParams params, IMethod method) {
		try {
			TargetMethod targetMethod = TargetMethodConverter.toTargetMethod(method);
			runGentestOnSingleMethod(params, targetMethod);
		} catch (PluginException e) {
			log.error(e.getMessage());
		}
	}
	
	/**
	 * for list of MethodDeclaration.
	 */
	private void runGentestOnTargetMethodList(LearnTestParams params, List<MethodDeclaration> targetMethods, CompilationUnit cu)
			throws PluginException {
		List<TargetMethod> validMethods = TargetMethodConverter.toTargetMethods(cu, targetMethods);
		for (TargetMethod method : validMethods) {
			runGentestOnSingleMethod(params, method);
		}
	}

	/**
	 * for Targetmethod.
	 */
	private void runGentestOnSingleMethod(LearnTestParams params, TargetMethod targetMethod) throws PluginException {
		log.info("-----------------------------------------------------------------------------------------------");
		log.info("Working method: {}", targetMethod.getMethodFullName());		
		log.info("-----------------------------------------------------------------------------------------------");
		params.renew(targetMethod);
		runLearntest(params);
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


	protected String getJobName() {
		return "JavaGenTest";
	}

}
