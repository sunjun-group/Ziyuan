/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.junit;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import sav.common.core.Constants;

/**
 * @author LLT
 *
 */
public class PrinterUtils {
	private PrinterUtils() {}
	
	public static String getClassFolder(String srcFolderPath, String pkg) {
		if (!new File(srcFolderPath).exists()) {
			throw new IllegalArgumentException(String.format(
					"src folder %s does not exist", srcFolderPath));
		}
		String classFolder = srcFolderPath;
		if (pkg != null) {
			classFolder += Constants.FILE_SEPARATOR + StringUtils.replace(pkg, 
					".", Constants.FILE_SEPARATOR);
		}
		return classFolder;
	}
	
	public static List<String> listJavaFileNames(String folder, final String fileNamePrefix) {
		File directory = new File(folder);
		if (!directory.exists()) {
			return new ArrayList<String>();
		}
		return Arrays.asList(directory.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				String[] fileName = StringUtils.split(name, Constants.DOT);
				if (fileName.length < 2
						|| !Constants.JAVA_EXT.equals(fileName[1])) {
					return false;
				}
				return fileName[0].startsWith(fileNamePrefix);
			}
		}));
	}
}
