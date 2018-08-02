/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.texteditor.ITextEditor;

import learntest.plugin.LearntestPlugin;
import learntest.plugin.commons.PluginException;

/**
 * @author LLT
 *
 */
public class WorkbenchUtils {
	
	public static IJavaElement[] getSelectedJavaElement(TextSelection selection) throws PluginException {
		try {
			IWorkbenchPage page = LearntestPlugin.getActiveWorkbenchWindow().getActivePage();
			ITextEditor editor = (ITextEditor) page.getActiveEditor();
			IJavaElement elem = JavaUI.getEditorInputJavaElement(editor.getEditorInput());
			if (elem != null && elem.getElementType() == IJavaElement.COMPILATION_UNIT) {
				ICompilationUnit cu = (ICompilationUnit) elem;
				IJavaElement[] elements= cu.codeSelect(selection.getOffset() + selection.getLength(), 0);
				return elements;
			}
		} catch (Exception e) {
			throw PluginException.wrapEx(e);
		}
		return new IJavaElement[0];
	}

	public static ICompilationUnit getActiveCompilationUnit(ITextEditor editor) throws PluginException {
		try {
			IJavaElement elem = JavaUI.getEditorInputJavaElement(editor.getEditorInput());
			if (elem != null && elem.getElementType() == IJavaElement.COMPILATION_UNIT) {
				ICompilationUnit cu = (ICompilationUnit) elem;
				return cu;
			}
		} catch (Exception e) {
			throw PluginException.wrapEx(e);
		}
		return null;
	}
	
	public static List<String> getAllProjects(){
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject[] projects = root.getProjects();
		List<String> projectNames = new ArrayList<>();
		for (int i = 0; i < projects.length; i++) {
			projectNames.add(projects[i].getName());
		}
		return projectNames;
	}
}
