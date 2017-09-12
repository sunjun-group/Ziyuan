/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.utils;

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
public class JavaFileUtils {

	public static int getMaxIdxOfExistingClass(String srcPath, String pkg, String classPrefix) {
		String jsrcPath = getClassFolder(srcPath, pkg);
		return getMaxIdxOfExistingClass(classPrefix, jsrcPath);
	}

	public static int getMaxIdxOfExistingClass(String jsrcPath, String classPrefix) {
		List<String> existedFiles = listJavaFileNames(jsrcPath, classPrefix);
		return getMaxClassIdx(existedFiles, classPrefix);
	}

	public static int getMaxClassIdx(List<String> existedFiles, String classPrefix) {
		int maxIdx = 0;
		for (String fileName : existedFiles) {
			String suffix = fileName.substring(classPrefix.length(),
					fileName.length() - Constants.JAVA_EXT_WITH_DOT.length());
			try {
				int idx = Integer.valueOf(suffix);
				maxIdx = Math.max(maxIdx, idx);
			} catch (NumberFormatException e) {
				// ignore
			}
		}
		return maxIdx;
	}

	public static String getClassFolder(String srcFolderPath, String pkg) {
		if (!new File(srcFolderPath).exists()) {
			throw new IllegalArgumentException(String.format("src folder %s does not exist", srcFolderPath));
		}
		String classFolder = srcFolderPath;
		if (pkg != null) {
			classFolder += Constants.FILE_SEPARATOR + StringUtils.replace(pkg, ".", Constants.FILE_SEPARATOR);
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
				if (fileName.length < 2 || !Constants.JAVA_EXT.equals(fileName[1])) {
					return false;
				}
				return fileName[0].startsWith(fileNamePrefix);
			}
		}));
	}
	
	public static String getClassPrefix(String className) {
		int countIdx = className.length() - 1;
		while (countIdx > 0) {
			char ch = className.charAt(countIdx);
			if (!Character.isDigit(ch)) {
				break;
			}
			countIdx--;
		}
		if (countIdx > 0 && countIdx < className.length()) {
			return className.substring(0, countIdx + 1);
		}
		return className;
	}
	
	public static File getSourceFile(List<String> srcFolders, String clzName) {
		for (String srcFolder : srcFolders) {
			File file = new File(ClassUtils.getJFilePath(srcFolder, clzName));
			if (file.exists()) {
				return file;
			}
		}
		return null;
	}
}
