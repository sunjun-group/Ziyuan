/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package jdart.handler;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;

/**
 * @author LLT
 *
 */
public class PluginUtils {
	private PluginUtils(){}

	public static String loadAbsolutePath(String resourceRelativePath) {
		try {
			URL fileURL =  new URL("platform:/plugin/jdart/" + resourceRelativePath);
			File file = new File(FileLocator.resolve(fileURL).toURI());
		    return file.getAbsolutePath();
		} catch (URISyntaxException e1) {
		    e1.printStackTrace();
		} catch (IOException e1) {
		    e1.printStackTrace();
		}
		return null;
	}
}
