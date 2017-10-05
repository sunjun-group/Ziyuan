/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.view.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.model.WorkbenchContentProvider;

import learntest.plugin.commons.data.JavaModelRuntimeInfo;
import learntest.plugin.commons.event.IJavaModelRuntimeInfo;

/**
 * @author LLT
 *
 */
public class ReportContentProvider extends WorkbenchContentProvider {
	public static final Object LOADING = new Object();
	private JavaModelRuntimeInfo javaModelRuntimeInfo;
	private ViewSettings viewSettings;
	private Map<Integer, List<IJavaElement>> cachedGroupByElementMap = new HashMap<Integer, List<IJavaElement>>();
	
	public ReportContentProvider(ViewSettings settings) {
		this.viewSettings = settings;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		super.inputChanged(viewer, oldInput, newInput);
		javaModelRuntimeInfo = (JavaModelRuntimeInfo) newInput;
		cachedGroupByElementMap.clear();
	}
	
	@Override
	public Object[] getElements(Object element) {
		IJavaModelRuntimeInfo runtimeInfo = (IJavaModelRuntimeInfo) element;
		if (runtimeInfo != null) {
			IJavaProject[] projects = runtimeInfo.getProjects();
			switch (viewSettings.getGroupBy()) {
			case PROJECT:
				return projects;
			case METHOD:
				return getElementsByType(projects, IJavaElement.METHOD);
			}
		}
		return new Object[0];
	}
	
	private Object[] getElementsByType(IJavaProject[] projects, int elementType) {
		List<IJavaElement> elements = cachedGroupByElementMap.get(elementType);
		if (elements == null) {
			elements = new ArrayList<IJavaElement>();
			appendTypes(projects, elements, elementType);
			cachedGroupByElementMap.put(elementType, elements);
		}
		return elements.toArray();
	}
	
	private void appendTypes(Object[] elements, List<IJavaElement> result, int type) {
		for (Object element : elements) {
			IJavaElement javaElement = (IJavaElement) element;
			if (javaElement.getElementType() == type) {
				result.add(javaElement);
			} else {
				appendTypes(getChildren(javaElement), result, type);
			}
		}
	}

	@Override
	public Object[] getChildren(Object element) {
		Object[] children = super.getChildren(element);
		if (((IJavaElement) element).getElementType() == IJavaElement.METHOD) {
			return javaModelRuntimeInfo.getTestcasesOfTargetMethod((IMethod)element);
		}
		return javaModelRuntimeInfo.filterUnSelectedElement(children);
	}
	
}
