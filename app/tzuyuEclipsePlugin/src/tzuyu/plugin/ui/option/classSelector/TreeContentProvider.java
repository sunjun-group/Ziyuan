/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.ui.option.classSelector;

import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import tzuyu.plugin.core.dto.MethodMnemonic;
import tzuyu.plugin.core.dto.TypeMnemonic;
import tzuyu.plugin.core.utils.AdaptablePropertyTester;
import tzuyu.plugin.reporter.PluginLogger;

/**
 * @author LLT
 * @author Peter Kalauskas (Randoop)
 */
class TreeContentProvider implements ITreeContentProvider {
	private TreeInput treeInput;
	private Map<IType, List<String>> fCheckedMethodsByType;

	public TreeContentProvider(TreeInput treeInput) {
		this.treeInput = treeInput;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public void dispose() {
	}

	public boolean hasChildren(Object element) {
		TreeNode node = (TreeNode) element;
		if (node.getObject() instanceof TypeMnemonic) {
			try {
				IType type = ((TypeMnemonic) node.getObject()).getType();
				if (type != null) {
					return type.getMethods().length != 0;
				}
			} catch (JavaModelException e) {
				PluginLogger.logEx(e);
			}
		}
		return node.hasChildren();
	}

	public Object getParent(Object element) {
		return ((TreeNode) element).getParent();
	}

	public Object[] getElements(Object inputElement) {
		return treeInput.getRoots();
	}

	public Object[] getChildren(Object parentElement) {
		TreeNode typeNode = (TreeNode) parentElement;

		if (typeNode.getObject() instanceof TypeMnemonic) {
			if (!typeNode.hasChildren()) {
				final boolean typeChecked = typeNode.isChecked();
				final boolean typeGrayed = typeNode.isGrayed();

				boolean allChecked = true;
				boolean noneChecked = true;

				IType type = ((TypeMnemonic) typeNode.getObject()).getType();

				if (type != null) {
					List<String> checkedMethods = fCheckedMethodsByType
							.get(type);
					fCheckedMethodsByType.remove(type);

					try {
						IMethod[] methods = type.getMethods();
						for (IMethod method : methods) {
							if (AdaptablePropertyTester.isTestable(method)) {
								MethodMnemonic methodMnemonic = new MethodMnemonic(
										method);

								boolean methodChecked;
								if (typeChecked && typeGrayed) {
									if (checkedMethods != null) {
										methodChecked = checkedMethods
												.contains(methodMnemonic
														.toString());
									} else {
										methodChecked = false;
									}
								} else {
									methodChecked = typeChecked;
								}

								allChecked &= methodChecked;
								noneChecked &= !methodChecked;

								TreeNode methodNode = typeNode.addChild(
										methodMnemonic, methodChecked, false);
								methodNode.updateRelatives();
							}
						}
					} catch (JavaModelException e) {
						PluginLogger.logEx(e);
					}

					if (allChecked) {
						typeNode.setChecked(true);
						typeNode.setGrayed(false);
					} else if (noneChecked) {
						typeNode.setChecked(false);
						typeNode.setGrayed(false);
					} else {
						typeNode.setChecked(true);
						typeNode.setGrayed(true);
					}

					typeNode.updateRelatives();
					return typeNode.getChildren();
				}
			}
		}
		return typeNode.getChildren();
	}
}