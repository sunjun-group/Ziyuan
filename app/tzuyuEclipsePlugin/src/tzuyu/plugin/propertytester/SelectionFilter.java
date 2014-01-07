/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */
package tzuyu.plugin.propertytester;

import java.util.Iterator;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import tzuyu.engine.utils.CollectionUtils;

/**
 * @author LLT
 */
public class SelectionFilter extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (!(receiver instanceof ISelection)) {
			return false;
		}
		if (receiver instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) receiver;
			// single selection
			if (selection.size() == 1) {
				return testIfOneSelected(selection.getFirstElement());
			}
			// multiple selection
			ITypeRoot typeRoot = null;
			for (Iterator<?> it = selection.iterator(); it.hasNext();) {
				// only methods and in the same class are accepted.
				Object element = it.next();
				if (!(element instanceof IMethod)) {
					return false;
				} 
				IMethod method = (IMethod) element;
				if (typeRoot == null) {
					typeRoot = method.getTypeRoot();
				}
				if (!typeRoot.equals(method.getTypeRoot())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private boolean testIfOneSelected(Object element) {
		for (Class<?> clazz : element.getClass().getInterfaces()) {
			if (CollectionUtils.existInArray(clazz, getValidSelectedClasses())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * all accepted type of selection element.
	 */
	private Class<?>[] getValidSelectedClasses() {
		return new Class<?>[] { ICompilationUnit.class, IMethod.class,
				IType.class };

	}

}
