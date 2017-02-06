/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.preferences;

import java.util.HashSet;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.search.BasicSearchEngine;
import org.eclipse.jdt.internal.core.search.JavaSearchScope;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.JavaUIMessages;
import org.eclipse.jdt.internal.ui.dialogs.OpenTypeSelectionDialog;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementImageProvider;
import org.eclipse.jdt.ui.JavaElementImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;

import tzuyu.plugin.commons.constants.PluginConstants;
import tzuyu.plugin.commons.utils.ResourcesUtils;
import tzuyu.plugin.tester.adapter.PluginReferencesAnalyzer;

/**
 * @author LLT 
 * Because there is restriction warning when using internal package
 *         of eclipse plugin, we should centralize all those cases here, 
 *         TODO LLT: is this safe enough? It's okay if we fix the eclipse version, 
 *         but check the differences between versions.
 */
@SuppressWarnings("restriction")
public class TypeScopeUtils {
	
	public static Image getTypeErrorImg() {
		return JavaPlugin.getImageDescriptorRegistry().get(
				new JavaElementImageDescriptor(
						JavaPluginImages.DESC_OBJS_ERROR, 
						JavaElementImageDescriptor.ERROR,
						JavaElementImageProvider.BIG_SIZE));
	}
	
	public static Image getImplTypeImg(boolean error) {
		int type = 0; 
		if (error) {
			type = JavaElementImageDescriptor.ERROR;
		}
		return JavaPlugin.getImageDescriptorRegistry().get(
				new JavaElementImageDescriptor(
						JavaPluginImages.DESC_OBJS_CLASS, type,
						JavaElementImageProvider.BIG_SIZE));
	}
	
	public static SelectionDialog getAbstractTypeDialog(IJavaProject project, Shell shell) {
		JavaSearchScope scope = getSearchScopeForType(project);
		SelectionDialog dialog = new OpenTypeSelectionDialog(shell, false, 
				PlatformUI.getWorkbench().getProgressService(), 
				scope,
				IJavaSearchConstants.CLASS_AND_INTERFACE);
		dialog.setTitle(JavaUIMessages.OpenTypeAction_dialogTitle);
		dialog.setMessage(JavaUIMessages.OpenTypeAction_dialogMessage);
		return dialog;
	}
	
	private static JavaSearchScope getSearchScopeForType(IJavaProject project) {
		JavaSearchScope scope = (JavaSearchScope) BasicSearchEngine.createJavaSearchScope(
				new IJavaElement[]{project}, false);
		scope = new JavaSearchScope();
		HashSet<IJavaProject> projs = new HashSet<IJavaProject>();
		projs.add(project);
		try {
			scope.add((JavaProject) project, IJavaSearchScope.SOURCES
					| IJavaSearchScope.APPLICATION_LIBRARIES
					| IJavaSearchScope.SYSTEM_LIBRARIES, projs);
		} catch (JavaModelException e) {
			// ignore
		}
		return scope;
	}
	
	public static SelectionDialog getImplTypeDialog(IType selectedType,
			IJavaProject project, Shell shell) throws JavaModelException {
		int elementKinds = IJavaSearchConstants.CLASS;
		JavaSearchScope scope;
		String fullyQualifiedName = selectedType.getFullyQualifiedName();
		if (Enum.class.getName().equals(fullyQualifiedName)) {
			scope = (JavaSearchScope) BasicSearchEngine.createJavaSearchScope(
					new IJavaElement[] { project }, false);
			elementKinds = IJavaSearchConstants.ENUM;
		} else if (PluginConstants.CLASS_CLASS_NAME.equals(fullyQualifiedName) || 
				PluginConstants.OBJECT_CLASS_NAME.equals(fullyQualifiedName)) {
			scope = (JavaSearchScope) BasicSearchEngine.createJavaSearchScope(
					new IJavaElement[] { project }, false);
		} else {
			scope = new JavaSearchScope();
			for (IType ele : PluginReferencesAnalyzer
					.getAllSubtypes(project, selectedType)) {
				if (ResourcesUtils.isPublicNotInterfaceOrAbstract(ele)) {
					scope.add(ele);
				}
			}
		}
		
		SelectionDialog dialog = new OpenTypeSelectionDialog(shell, true, 
				PlatformUI.getWorkbench().getProgressService(), 
				scope,
				elementKinds);
		
		dialog.setTitle(JavaUIMessages.OpenTypeAction_dialogTitle);
		dialog.setMessage(JavaUIMessages.OpenTypeAction_dialogMessage);
		return dialog;
	}
}
