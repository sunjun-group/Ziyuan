/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.io.excel;

import static learntest.io.excel.TrialExcelConstants.EXCEL_EXT_WITH_DOT;
import static learntest.io.excel.TrialExcelConstants.EXCEL_FOLDER;
import static learntest.io.excel.TrialExcelConstants.FILE_IDX_START_CH;
import static learntest.io.excel.TrialExcelConstants.FIRST_FILE_IDX;
import static learntest.io.excel.TrialExcelConstants.TRIAL_NUMBER_LIMIT_PER_FILE;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sav.common.core.Pair;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.ResourceUtils;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class TrialExcelHandler {
	private Logger log = LoggerFactory.getLogger(TrialExcelHandler.class);
	private Pair<File, Integer> fileInfo;
	private String trialFilePrefix;
	private TrialExcelReader reader;
	private TrialExcelWriter writer;
	private boolean isNewFile = false;
	
	public TrialExcelHandler(String trialFilePrefix) throws Exception {
		this(trialFilePrefix, TrialExcelConstants.DEFAULT_EXCEL_APPEND);
	}
	
	public TrialExcelHandler(String trialFilePrefix, boolean appendLastFile) throws Exception {
		reader = new TrialExcelReader();
		fileInfo = getExperimentalExcel(trialFilePrefix, appendLastFile);
		writer = new TrialExcelWriter(fileInfo.a);
	}
	
	public void export(Trial trial) throws Exception {
		int lastDataRow = writer.addRowData(trial);
		if (lastDataRow > TrialExcelConstants.TRIAL_NUMBER_LIMIT_PER_FILE) {
			// create new file if exceeding limit line number
			fileInfo = newExperimentalExcelFile(trialFilePrefix, fileInfo.b + 1);
			writer.reset(fileInfo.a);
		}
	}
	
	private Pair<File, Integer> getExperimentalExcel(String trialFilePrefix, boolean append) throws IOException {
		Pair<File, Integer> lastFileInfo = getLastExperimentalExcel(trialFilePrefix);
		if (lastFileInfo == null) {
			return newExperimentalExcelFile(trialFilePrefix, FIRST_FILE_IDX);
		} else if (!append) {
			return newExperimentalExcelFile(trialFilePrefix, lastFileInfo.b + 1);
		}
		/* check if the file is valid to append or not */
		try {
			reader.reset(lastFileInfo.a);
			if (reader.hasValidHeader() && reader.getLastDataSheetRow() < TRIAL_NUMBER_LIMIT_PER_FILE) {
				// appendable
				return lastFileInfo;
			}
		} catch (Exception e) {
			log.debug(e.getMessage());
			return newExperimentalExcelFile(trialFilePrefix, lastFileInfo.b + 1);
		}
		return newExperimentalExcelFile(trialFilePrefix, FIRST_FILE_IDX);
	}

	public static Pair<File, Integer> getLastExperimentalExcel(String trialFilePrefix) {
		File folder = new File(EXCEL_FOLDER);
		File[] files = folder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File folder, String name) {
				return name.endsWith(EXCEL_EXT_WITH_DOT) && name.startsWith(trialFilePrefix);
			}
		});
		if (CollectionUtils.isEmpty(files)) {
			return null;
		}
		return getLastExperimentalExcel(files);
	}

	private static Pair<File, Integer> getLastExperimentalExcel(File[] files) {
		int lastIdx = -1;
		File lastFile = null;
		for (File file : files) {
			String fileName = file.getName();
			String fileIdxStr = fileName.substring(fileName.lastIndexOf(FILE_IDX_START_CH) + 1,
					fileName.indexOf(EXCEL_EXT_WITH_DOT));
			int fileIdx = NumberUtils.toInt(fileIdxStr, lastIdx);
			if (fileIdx > lastIdx) {
				lastIdx = fileIdx;
				lastFile = file;
			}
		}
		if (lastIdx < 0) {
			return null;
		} 
		
		return Pair.of(lastFile, lastIdx);
	}

	private Pair<File, Integer> newExperimentalExcelFile(String trialFilePrefix, int fileIdx) {
		String filepath = ResourceUtils.appendPath(EXCEL_FOLDER,
				StringUtils.concatenate(trialFilePrefix, FILE_IDX_START_CH, String.valueOf(fileIdx), EXCEL_EXT_WITH_DOT));
		File file = new File(filepath);
		isNewFile = true;
		return Pair.of(file, fileIdx);
	}

	@SuppressWarnings("unchecked")
	public Collection<Trial> readOldTrials() {
		if (isNewFile) {
			return Collections.EMPTY_LIST;
		} 
		try {
			reader.reset(fileInfo.a);
			return reader.readDataSheet().values();
		} catch (Exception e) {
			log.debug(e.getMessage());
			return Collections.EMPTY_LIST;
		}
	}
}
