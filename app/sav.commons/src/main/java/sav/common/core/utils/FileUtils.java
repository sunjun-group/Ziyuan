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
import java.io.FileWriter;
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
	
	public static void deleteFilesByName(List<String> fileNames) {
		for (String fileName : CollectionUtils.nullToEmpty(fileNames)) {
			File file = new File(fileName);
			if (file.exists()) {
				file.delete();
			}
		}
	}
	
	public static void deleteFileByName(String fileName) {
		if (fileName != null) {
			File file = new File(fileName);
			if (file.exists()) {
				file.delete();
			}
		}
	}
	
	public static void copyFiles(List<File> files, String folderPath) throws FileNotFoundException, IOException {
		File targetFolder = mkDirs(folderPath);
		for (File file : files) {
			InputStream inStream = new FileInputStream(file);
			File copyFile = new File(targetFolder, file.getName());
			while (copyFile.exists()) {
				copyFile = new File(targetFolder, copyFile.getName() + "_00");
			}
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
	
	public static List<File> getFiles(List<String> filePaths) {
		List<File> files = new ArrayList<File>(filePaths.size());
		for (String path : filePaths) {
			File file = new File(path);
			files.add(file);
		}
		return files;
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

	public static void deleteAllFiles(String folderPath) {
		File folder = new File(folderPath);
		if (!folder.exists() || !folder.isDirectory()) {
			return;
		}
		File[] files = folder.listFiles();
		if (!CollectionUtils.isEmpty(files)) {
			deleteFiles(Arrays.asList(files));
		}
	}
	
	public static void write(String file, String log) {
		if (log == null) {
			return;
		}
		FileWriter writer = null;
		try {
			writer = new FileWriter(file, true);
			writer.write(log);
		} catch (Exception e) {
			// ignore
		} finally {
			IOUtils.closeQuietly(writer);
		}

	}
	
	public static void createFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}
	}
	
	public static File getFileCreateIfNotExist(String path) {
		File file = new File(path);
		if (!file.exists()) {
			File folder = file.getParentFile();
			if (!folder.exists()) {
				folder.mkdirs();
			}
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new SavRtException(e);
			}
		}
		return file;
	}

	public static File createTempFile(String prefix, String suffix) {
		try {
			return File.createTempFile(prefix, suffix);
		} catch (IOException e) {
			throw new SavRtException(e);
		}
	}
}
