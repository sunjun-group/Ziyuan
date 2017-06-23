/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.commons.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.JavaRuntime;

import tzuyu.plugin.commons.exception.PluginException;

/**
 * @author LLT
 * 
 */
public class ClassLoaderUtils {
	private ClassLoaderUtils() {
	}

	public static URLClassLoader getClassLoader(IJavaProject project)
			throws PluginException {
		String[] classPathEntries;
		try {
			classPathEntries = JavaRuntime
					.computeDefaultRuntimeClassPath(project);
			List<URL> urlList = new ArrayList<URL>();
			for (int i = 0; i < classPathEntries.length; i++) {
				String entry = classPathEntries[i];
				IPath path = new Path(entry);
				URL url = path.toFile().toURI().toURL();
				urlList.add(url);
			}

			ClassLoader parentClassLoader = project.getClass().getClassLoader();
			URL[] urls = (URL[]) urlList.toArray(new URL[urlList.size()]);
			URLClassLoader classLoader = new URLClassLoader(urls,
					parentClassLoader);
			return classLoader;
		} catch (CoreException e) {
			PluginException.wrapEx(e);
		} catch (MalformedURLException e) {
			PluginException.wrapEx(e);
		}
		return null;
	}
}
