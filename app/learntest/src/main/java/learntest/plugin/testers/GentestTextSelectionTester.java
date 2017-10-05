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
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.StructuredSelection;

import learntest.plugin.commons.PluginException;
import learntest.plugin.utils.WorkbenchUtils;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class GentestTextSelectionTester extends PropertyTester {
	

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof TextSelection) {
			try {
				IJavaElement[] javaElements = WorkbenchUtils.getSelectedJavaElement((TextSelection) receiver);
				for (IJavaElement javaElement : javaElements) {
					if (CollectionUtils.existIn(javaElement.getElementType(), IJavaElement.COMPILATION_UNIT,
							IJavaElement.METHOD, IJavaElement.TYPE, IJavaElement.CLASS_FILE)) {
						return true;
					}
				}
			} catch (PluginException e) {
				return false;
			}
		} else if (receiver instanceof StructuredSelection) {
			return true; // no need to check
		}
		
		return false;
	}

}
