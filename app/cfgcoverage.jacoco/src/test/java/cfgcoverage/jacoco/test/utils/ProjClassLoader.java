/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.test.utils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;

/**
 * @author LLT
 *
 */
public class ProjClassLoader {

	public static URLClassLoader getClassLoader(List<String> jarPaths) throws Exception {
		URL[] urls = new URL[jarPaths.size()];
		Enumeration<JarEntry> e = null;
		for (int i = 0; i < jarPaths.size(); i++) {
			String jarPath = jarPaths.get(i);
			File file = new File(jarPath);
			urls[i] = file.toURL();
		}

		URLClassLoader cl = URLClassLoader.newInstance(urls);
		return cl;
	}
}
