/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tools.commons;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * @author LLT
 *
 */
public class ToolsUtils {
	
	public static Properties loadProperties(String fileName) {
		Properties prop = new Properties();
		File file = new File(fileName);
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			prop.load(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
		return prop;
	}
	
	public static ResourceBundle getResourceBundle(String resource) {
		return ResourceBundle.getBundle(resource);
	}
}
