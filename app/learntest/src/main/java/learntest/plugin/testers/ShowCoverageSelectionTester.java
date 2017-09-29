/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.testers;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.IStructuredSelection;

import learntest.plugin.commons.data.IModelRuntimeInfo;

/**
 * @author LLT
 *
 */
public class ShowCoverageSelectionTester extends PropertyTester {
	public static final String PARAMETER = "coverageType";
	
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		ShowType type = ShowType.valueOf(expectedValue.toString());
		if (!(receiver instanceof IStructuredSelection)) {
			return false;
		}
		IStructuredSelection selection = (IStructuredSelection) receiver;
		return allowEnable(selection.toArray(), type);
	}
	
	private boolean allowEnable(Object[] elements, ShowType type) {
		IModelRuntimeInfo sameRuntimeInfo = null;
		for (Object element : elements) {
			IJavaElement javaElement = (IJavaElement) element;
			IModelRuntimeInfo runtimeInfo = javaElement.getAdapter(IModelRuntimeInfo.class);
			if (!type.accept(element == runtimeInfo.getJavaElement()) || 
					((sameRuntimeInfo != null) && (sameRuntimeInfo != runtimeInfo))) {
				return false;
			}
			sameRuntimeInfo = runtimeInfo;
		}
		return true;
	}

	private static enum ShowType {
		FULL(false), 
		BY_SELECTED_TESTCASES(true);
		
		private boolean forTestcaseSelected;
		private ShowType(boolean forTestcaseSelected) {
			this.forTestcaseSelected = forTestcaseSelected;
		}

		public boolean accept(boolean isNotTestcase) {
			return forTestcaseSelected != isNotTestcase;
		}
	}
}
