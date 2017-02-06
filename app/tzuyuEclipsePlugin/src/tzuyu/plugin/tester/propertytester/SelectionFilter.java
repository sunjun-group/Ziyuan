/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */
package tzuyu.plugin.tester.propertytester;

import java.util.Iterator;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import sav.common.core.utils.CollectionUtils;
import tzuyu.plugin.commons.constants.PluginConstants;

/**
 * @author LLT
 */
public class SelectionFilter extends PropertyTester {
	private static final String DFA_FILE_TEST_PROPERTY = "isForDfaReader";
	private static final String ANALYSIS_TEST_PROPERTY = "isForAnalysis";
	private static final String TZUYU_TEST_PROPERTY = "isForTzuyuTest"; 
	
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (DFA_FILE_TEST_PROPERTY.equals(property) && 
				receiver instanceof IFile) {
			return testForDfaView((IFile) receiver);
		}
		
		if (ANALYSIS_TEST_PROPERTY.equals(property)) {
			return testForAnalysis(receiver);
		}
		
		if (TZUYU_TEST_PROPERTY.equals(property)) {
			return testForTzuyuTest(receiver);
		}
		
		return false;
	}

	private boolean testForTzuyuTest(Object receiver) {
		Class<?>[] validSingleSelectedTypes = new Class<?>[] {
				IClassFile.class, ICompilationUnit.class, IMethod.class,
				IType.class };
		return isSingleSelectedTypeOrMultiMethods(receiver, validSingleSelectedTypes);
	}

	private boolean testForAnalysis(Object receiver) {
		Class<?>[] validSingleSelectedTypes = new Class<?>[] {
				ICompilationUnit.class, IMethod.class, IType.class };
		return isSingleSelectedTypeOrMultiMethods(receiver, validSingleSelectedTypes);
	}

	private boolean isSingleSelectedTypeOrMultiMethods(Object receiver,
			Class<?>[] validSingleSelectedTypes) {
		if (!(receiver instanceof ISelection)) {
			return false;
		}
		
		if (receiver instanceof ITextSelection) {
			return checkIfTextSelection((ITextSelection)receiver);
		}
		
		if (receiver instanceof IStructuredSelection) {
			return checkIfStructuredSelection((IStructuredSelection) receiver, validSingleSelectedTypes);
		}
		return false;
	}

	/**
	 * menu in editor view.
	 */
	private boolean checkIfTextSelection(ITextSelection selection) {
		return true;
	}

	/**
	 * menu in tree explorer views.
	 */
	private boolean checkIfStructuredSelection(IStructuredSelection selection,
			Class<?>[] validSingleSelectedTypes) {
		// single selection
		if (selection.size() == 1) {
			for (Class<?> clazz : selection.getFirstElement().getClass()
					.getInterfaces()) {
				if (CollectionUtils
						.existIn(clazz, validSingleSelectedTypes)) {
					return true;
				}
			}
			return false;
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

	private boolean testForDfaView(IFile file) {
		return PluginConstants.DFA_FILE_EXTENSION.equals(file.getFileExtension());
	}

}
