/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;

import sav.common.core.Constants;
import sav.common.core.SavRtException;

/**
 * @author LLT
 *
 */
public class FileUtils {
	private FileUtils(){}
	
	public static File getFileInTempFolder(String fileName) {
		File tmpdir = new File(System.getProperty("java.io.tmpdir"));
		File file = new File(tmpdir, fileName);
		return file;
	}
	
	public static void appendFile(String fileName, String content) {
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new SavRtException("cannot create file " + fileName);
			}
		}
		FileOutputStream stream;
		try {
			stream = new FileOutputStream(file, true);
			stream.write(content.getBytes());
			stream.close();
		} catch (FileNotFoundException e) {
			throw new SavRtException(e);
		} catch (IOException e) {
			throw new SavRtException(e);
		}
	}
	
	public static void deleteFiles(List<File> files) {
		for (File file : CollectionUtils.nullToEmpty(files)) {
			file.delete();
		}
	}
	
	public static void copyFiles(List<File> files, String folderPath) throws FileNotFoundException, IOException {
		File targetFolder = mkDirs(folderPath);
		for (File file : files) {
			InputStream inStream = new FileInputStream(file);
			File copyFile = new File(targetFolder, file.getName());
			IOUtils.copy(inStream, new FileOutputStream(copyFile));
		}
	}

	public static File mkDirs(String folderPath) {
		File targetFolder = new File(folderPath);
		if (!targetFolder.exists()) {
			targetFolder.mkdirs();
		}
		return targetFolder;
	}
	
	

	public static void copyFilesSilently(List<File> files, String folderPath) {
		try {
			copyFiles(files, folderPath);
		} catch (Exception e) {
			// do nothing
		}
	}

	public static List<String> getFileNames(List<File> files) {
		List<String> names = new ArrayList<String>(CollectionUtils.getSize(files));
		for (File file : CollectionUtils.nullToEmpty(files)) {
			names.add(file.getName());
		}
		return names;
	}

	public static List<String> getFilePaths(List<File> files) {
		List<String> paths = new ArrayList<String>(CollectionUtils.getSize(files));
		for (File file : CollectionUtils.nullToEmpty(files)) {
			paths.add(file.getAbsolutePath());
		}
		return paths;
	}

	public static String getFilePath(String... fragments) {
		return StringUtils.join(Arrays.asList(fragments), Constants.FILE_SEPARATOR);
	}
}
