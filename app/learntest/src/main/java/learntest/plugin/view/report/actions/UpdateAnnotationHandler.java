/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.view.report.actions;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;

import learntest.plugin.LearntestPlugin;
import learntest.plugin.commons.data.IModelRuntimeInfo;
import learntest.plugin.commons.event.AnnotationChangeEvent;
import learntest.plugin.commons.event.IJavaModelRuntimeInfo;
import learntest.plugin.view.report.annotation.CoverageAnnotationModel;
import sav.common.core.pattern.IDataProvider;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class UpdateAnnotationHandler extends AbstractHandler {
	public static final String ID = "learntest.plugin.commands.reportView.updateAnnotationByTestcases";
	private ISelectionProvider selectionProvider;
	private IDataProvider<IJavaModelRuntimeInfo> dataProvider;
	
	public UpdateAnnotationHandler(ISelectionProvider selectionProvider) {
		this.selectionProvider = selectionProvider;
		this.dataProvider = LearntestPlugin.getJavaModelRuntimeInfoProvider();
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = selectionProvider.getSelection();
		if (!(selection instanceof StructuredSelection)) {
			return null;
		}
		Object[] elements = ((StructuredSelection) selection).toArray();
		if (CollectionUtils.isEmpty(elements)) {
			return null;
		}
		IModelRuntimeInfo runtimeInfo = dataProvider.getData().getCorrespondingRuntimeInfo((IJavaElement) elements[0]);
		try {
			JavaUI.openInEditor(runtimeInfo.getJavaElement());
			CoverageAnnotationModel.attachCoverageAnnotation();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (runtimeInfo.getJavaElement() != elements[0]) {
			// testcases selected
			List<String> testcases = dataProvider.getData().getTestcaseStrings(elements);
			LearntestPlugin.getJavaGentestEventManager()
					.fireAnnotationChange(new AnnotationChangeEvent((IMethod) runtimeInfo.getJavaElement(), testcases));
			onUpdateAnnotation(runtimeInfo, elements);
		} else {
			LearntestPlugin.getJavaGentestEventManager()
					.fireAnnotationChange(new AnnotationChangeEvent((IMethod) runtimeInfo.getJavaElement(), null));
			onUpdateAnnotation(runtimeInfo, null);
		}
		return null;
	}

	public void onUpdateAnnotation(IModelRuntimeInfo runtimeInfo, Object[] elements) {
		// do nothing by default
	}
}
