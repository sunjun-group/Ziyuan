/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.utils;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

/**
 * @author LLT
 *
 */
public class PluginUtils {
	private PluginUtils(){}
	
	public static void getJavaHome(IJavaProject project) throws JavaModelException {
		IClasspathEntry[] classpath = project.getRawClasspath();
	}
}
