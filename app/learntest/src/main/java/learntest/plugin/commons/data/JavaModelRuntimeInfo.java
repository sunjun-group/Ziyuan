/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.commons.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.core.RunTimeInfo;
import learntest.plugin.commons.PluginException;
import learntest.plugin.commons.event.IJavaModelRuntimeInfo;
import learntest.plugin.handler.gentest.GentestWorkObject;
import learntest.plugin.handler.gentest.WorkProject;
import learntest.plugin.utils.IProjectUtils;
import sav.common.core.Pair;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class JavaModelRuntimeInfo implements IJavaModelRuntimeInfo {
	private static Logger log = LoggerFactory.getLogger(JavaModelRuntimeInfo.class);
	private GentestWorkObject workObject;
	private Map<IJavaElement, IModelRuntimeInfo> jEleRuntimeInfoMap;
	private Map<IMethod, List<IMethod>> targetMethodTestcasesMap;
	private Map<IMethod, String> testcasesMap;

	public JavaModelRuntimeInfo(GentestWorkObject workObject) {
		this.workObject = workObject;
		jEleRuntimeInfoMap = new HashMap<IJavaElement, IModelRuntimeInfo>();
		targetMethodTestcasesMap = new HashMap<IMethod, List<IMethod>>();
		testcasesMap = new HashMap<IMethod, String>();
	}

	public void add(IModelRuntimeInfo runtimeInfo) {
		if (runtimeInfo != null) {
			runtimeInfo.transferToMap(jEleRuntimeInfoMap);
		}
	}

	/**
	 * filter element which includes no tested method.
	 */
	public Object[] filterUnSelectedElement(Object[] children) {
		List<IJavaElement> filteredList = new ArrayList<IJavaElement>(children.length);
		for (Object child : children) {
			IJavaElement element = (IJavaElement) child;
			try {
				if (isJarPackageFragmentRoot(element) || !CollectionUtils.existIn(element.getElementType(),
						IJavaElement.JAVA_PROJECT, IJavaElement.PACKAGE_FRAGMENT_ROOT, IJavaElement.PACKAGE_FRAGMENT,
						IJavaElement.COMPILATION_UNIT, IJavaElement.TYPE, IJavaElement.METHOD)) {
					continue;
				}
				WorkProject workProject = workObject.getWorkProject(element.getJavaProject());
				if (workProject.isTestingRelevant(element)) {
					filteredList.add(element);
				}
			} catch (PluginException e) {
				log.error(e.getMessage());
			}
		}
		return filteredList.toArray(new IJavaElement[filteredList.size()]);
	}

	private boolean isJarPackageFragmentRoot(IJavaElement element) throws PluginException {
		if (element.getElementType() != IJavaElement.PACKAGE_FRAGMENT_ROOT) {
			return false;
		}
		IPackageFragmentRoot pkgRoot = (IPackageFragmentRoot) element;
		try {
			return pkgRoot.getKind() != IPackageFragmentRoot.K_SOURCE;
		} catch (JavaModelException e) {
			throw PluginException.wrapEx(e);
		}
	}

	@Override
	public IJavaProject[] getProjects() {
		IJavaProject[] projects = new IJavaProject[workObject.getWorkProjects().size()];
		for (int i = 0; i < workObject.getWorkProjects().size(); i++) {
			projects[i] = workObject.getWorkProjects().get(i).getProject();
		}
		return projects;
	}

	@Override
	public IModelRuntimeInfo getCorrespondingRuntimeInfo(IJavaElement element) {
		IModelRuntimeInfo runtimeInfo = jEleRuntimeInfoMap.get(element);
		if ((runtimeInfo == null) && (element.getElementType() == IJavaElement.METHOD)) {
			for (IMethod targetMethod : targetMethodTestcasesMap.keySet()) {
				if (targetMethodTestcasesMap.get(targetMethod).contains(element)) {
					return jEleRuntimeInfoMap.get(targetMethod);
				}
			}
		}
		return runtimeInfo;
	}

	public Object[] getTestcasesOfTargetMethod(IMethod targetMethod) {
		if (testcasesMap.containsKey(targetMethod)) {
			return new Object[0];
		}
		IModelRuntimeInfo modelRuntimeInfo = targetMethod.getAdapter(IModelRuntimeInfo.class);
		if (modelRuntimeInfo != null) {
			List<IMethod> testcases = targetMethodTestcasesMap.get(targetMethod);
			if (testcases == null) {
				MethodRuntimeInfo methodRuntimeInfo = (MethodRuntimeInfo) modelRuntimeInfo;
				RunTimeInfo runtimeInfo = methodRuntimeInfo.getRawRuntimeInfo();
				Collection<String> coveredTestcases = runtimeInfo.getLineCoverageResult().getCoveredTestcases();
				coveredTestcases = StringUtils.sortAlphanumericStrings(new ArrayList<String>(coveredTestcases));
				Pair<List<String>, List<IMethod>> iMethodTestcasesPair = IProjectUtils
						.getIMethods(targetMethod.getJavaProject(), coveredTestcases);
				testcases = iMethodTestcasesPair.b;
				targetMethodTestcasesMap.put(targetMethod, testcases);
				for (int i = 0; i < testcases.size(); i++) {
					testcasesMap.put(testcases.get(i), iMethodTestcasesPair.a.get(i));
				}
			}
			return testcases.toArray();
		}
		return new Object[0];
	}

	@Override
	public List<String> getTestcaseStrings(Object[] testMethods) {
		List<String> testcases = new ArrayList<String>(testMethods.length); 
		for (Object element : testMethods) {
			String testcase = testcasesMap.get(element);
			if (testcase != null) {
				testcases.add(testcase);
			}
		}
		return testcases;
	}
}
