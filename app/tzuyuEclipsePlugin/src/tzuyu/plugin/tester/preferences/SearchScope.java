/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.preferences;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;


/**
 * @author LLT
 *
 */
public enum SearchScope {
	SOURCE,
	SOURCE_JARS,
	USER_DEFINED;

	public int getIncludeMask() {
		if (this == SOURCE) {
			return IJavaSearchScope.SOURCES;
		}
		return IJavaSearchScope.SOURCES
				| IJavaSearchScope.APPLICATION_LIBRARIES
				| IJavaSearchScope.SYSTEM_LIBRARIES;
	}
	
	public IJavaSearchScope getIJavaSearchScope(IJavaElement jEle) {
		return SearchEngine.createJavaSearchScope(new IJavaElement[] { jEle },
				getIncludeMask());
	}
}
