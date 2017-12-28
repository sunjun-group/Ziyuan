/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.commons.event;

import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;

import learntest.plugin.commons.data.IModelRuntimeInfo;

/**
 * @author LLT
 *
 */
public interface IJavaModelRuntimeInfo {
	
	/**
	 * @return projects which contain target testing method(s).
	 */
	IJavaProject[] getProjects();

	IModelRuntimeInfo getCorrespondingRuntimeInfo(IJavaElement element);

	List<String> getTestcaseStrings(Object[] elements);

}
