/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.ui.option.classSelector;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

import tzuyu.plugin.core.dto.MethodMnemonic;
import tzuyu.plugin.core.dto.TypeMnemonic;
import tzuyu.plugin.reporter.PluginLogger;

/**
 * @author LLT
 * @author Peter Kalauskas [Randoop, TreeLabelProvider]
 */
class TreeLabelProvider extends LabelProvider {
	private static Image IMG_ERROR = PlatformUI.getWorkbench()
			.getSharedImages()
			.getImage(org.eclipse.ui.ISharedImages.IMG_OBJS_ERROR_TSK);
	private static Image IMG_ENUM = JavaUI.getSharedImages().getImage(
			ISharedImages.IMG_OBJS_ENUM);
	private static Image IMG_CLASS = JavaUI.getSharedImages().getImage(
			ISharedImages.IMG_OBJS_CLASS);

	private static Image IMG_METHOD_PUBLIC = JavaUI.getSharedImages().getImage(
			ISharedImages.IMG_OBJS_PUBLIC);
	private static Image IMG_METHOD_PRIVATE = JavaUI.getSharedImages()
			.getImage(ISharedImages.IMG_OBJS_PRIVATE);
	private static Image IMG_METHOD_PROTECTED = JavaUI.getSharedImages()
			.getImage(ISharedImages.IMG_OBJS_PROTECTED);
	private static Image IMG_METHOD_DEFAULT = JavaUI.getSharedImages()
			.getImage(ISharedImages.IMG_OBJS_DEFAULT);

	private static Image IMG_PACKAGE_FRAGMENT = JavaUI.getSharedImages()
			.getImage(ISharedImages.IMG_OBJS_PACKAGE);
	private IJavaProject project;

	TreeLabelProvider() {

	}
	
	public void setProject(IJavaProject project) {
		this.project = project;
	}

	@Override
	public Image getImage(Object element) {
		Assert.isNotNull(project, "Java project is not set for TreeLabelProvider!!");
		
		TreeNode node = (TreeNode) element;
		Object obj = node.getObject();
		if (obj instanceof String) {
			return IMG_PACKAGE_FRAGMENT;
		} else if (obj instanceof TypeMnemonic) {
			TypeMnemonic typeMnemonic = ((TypeMnemonic) obj);
			if (!typeMnemonic.exists()
					|| !typeMnemonic.getJavaProject().equals(project)) {
				return IMG_ERROR;
			} else {
				return getImageForType(typeMnemonic.getType());
			}
		} else if (obj instanceof MethodMnemonic) {
			if (getImage(node.getParent()).equals(IMG_ERROR)) {
				return IMG_ERROR;
			} else {
				return getImageForMethod(((MethodMnemonic) obj).getMethod());
			}
		}

		return null;
	}

	private Image getImageForMethod(IMethod method) {
		if (method == null || !method.exists()) {
			return IMG_ERROR;
		}

		try {
			int flags = method.getFlags();
			if (Flags.isPublic(flags)) {
				return IMG_METHOD_PUBLIC;
			} else if (Flags.isPrivate(flags)) {
				return IMG_METHOD_PRIVATE;
			} else if (Flags.isProtected(flags)) {
				return IMG_METHOD_PROTECTED;
			} else {
				return IMG_METHOD_DEFAULT;
			}
		} catch (JavaModelException e) {
			PluginLogger.logEx(e);
		}
		return null;
	}

	private Image getImageForType(IType type) {
		try {
			if (type != null && type.exists()) {
				if (type.isEnum()) {
					return IMG_ENUM;
				} else if (type.isClass()) {
					return IMG_CLASS;
				}
			}
		} catch (JavaModelException e) {
			PluginLogger.logEx(e);
		}
		return IMG_ERROR;
	}
}
