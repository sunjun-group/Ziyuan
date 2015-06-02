/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.mutantbug;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import sav.common.core.Logger;
import sav.common.core.SavRtException;

import mutation.utils.FileUtils;

/**
 * @author LLT
 *
 */
public class FilesBackup {
	private Logger<?> log = Logger.getDefaultLogger();
	private File backupDir;
	/* map to store files and backup files*/
	private Map<File, File> backupMap;
	
	private FilesBackup(){
		backupMap = new HashMap<File, File>();
	}
	
	public void backup(String filePath) {
		backup(new File(filePath));
	}

	public void backup(File file) {
		File tempDir = FileUtils.createTempFolder("backup");
		try {
			File backupFile = FileUtils.copyFileToDirectory(file,
					tempDir, true);
			backupMap.put(file, backupFile);
			log.debug("backup ", file.getAbsolutePath());
		} catch (IOException e) {
			throw new SavRtException(e);
		}
	}
	
	public void restoreAll() {
		for (Entry<File, File> entry : backupMap.entrySet()) {
			restore(entry.getKey(), entry.getValue());
		}
		backupMap.clear();
	}
	
	public void restore(File file) {
		File backupFile = backupMap.remove(file);
		restore(file, backupFile);
	}

	private void restore(File file, File backupFile) {
		if (backupFile == null) {
			throw new SavRtException("Cannot find backup for file: ", file.getAbsolutePath());
		}
		try {
			FileUtils.copyFile(backupFile, file, true);
			log.debug("restore ", file.getAbsolutePath());
		} catch (IOException e) {
			throw new SavRtException(e);
		}
		backupFile.delete();
	}
	
	public void open() {
		backupDir = FileUtils.createTempFolder("backup");
		log.debug("create backup folder ", backupDir.getAbsolutePath());
	}
	
	public void close() {
		backupDir.delete();
		backupDir = null;
	}
	
	public boolean isClose() {
		return backupDir == null;
	}
	
	public static FilesBackup startBackup() {
		FilesBackup backup = new FilesBackup();
		backup.open();
		return backup;
	}

}
