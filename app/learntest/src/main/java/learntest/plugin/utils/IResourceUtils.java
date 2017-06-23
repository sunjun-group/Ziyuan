/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jdt.core.IJavaProject;

/**
 * @author LLT
 *
 */
public class IResourceUtils {
	private IResourceUtils(){}
	
	public static void getTestTarget(IJavaProject project) {
	}
	
	public static String getResourceAbsolutePath(String pluginId, String resourceRelativePath) {
		try {
			URL fileURL = new URL(getResourceUrl(pluginId, resourceRelativePath));
			File file = new File(FileLocator.resolve(fileURL).toURI());
		    return file.getAbsolutePath();
		} catch (URISyntaxException e1) {
		    e1.printStackTrace();
		} catch (IOException e1) {
		    e1.printStackTrace();
		}
		return null;
	}

	private static String getResourceUrl(String pluginId, String resourceRelativePath) {
		StringBuilder sb = new StringBuilder("platform:/plugin/").append(pluginId).append("/")
				.append(resourceRelativePath);
		return sb.toString();
	}
}
