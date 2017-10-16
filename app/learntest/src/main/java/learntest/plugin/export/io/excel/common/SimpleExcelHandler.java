/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.export.io.excel.common;

import static learntest.plugin.export.io.excel.TrialExcelConstants.EXCEL_EXT_WITH_DOT;
import static learntest.plugin.export.io.excel.TrialExcelConstants.FILE_IDX_START_CH;
import static learntest.plugin.export.io.excel.TrialExcelConstants.FIRST_FILE_IDX;
import static learntest.plugin.export.io.excel.TrialExcelConstants.TRIAL_NUMBER_LIMIT_PER_FILE;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

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
public abstract class SimpleExcelHandler<T> {
	private Logger log = LoggerFactory.getLogger(SimpleExcelHandler.class);
	protected Pair<File, Integer> fileInfo;
	protected String excelFilePrefix;
	protected SimpleExcelReader reader;
	protected SimpleExcelWriter<T> writer;
	protected boolean isNewFile = false;
	protected String excelRootFolder;
	
	public SimpleExcelHandler(ExcelSettings settings) throws Exception {
		this(settings.getExcelRootFolder(), settings.getExcelFilePrefix(), settings.isAppendLastFile());
	}

	public SimpleExcelHandler(String excelRootFolder, String excelFilePrefix, boolean appendLastFile)
			throws Exception {
		this.excelRootFolder = excelRootFolder;
		reader = initExcelReader();
		fileInfo = getExperimentalExcel(excelFilePrefix, appendLastFile);
		writer = initExcelWriter(fileInfo.a);
	}

	protected abstract SimpleExcelReader initExcelReader();
	protected abstract SimpleExcelWriter<T> initExcelWriter(File file) throws Exception;
	
	public void export(T rowData) throws Exception {
		int lastDataRow = writer.addRowData(rowData);
		if (lastDataRow > ExcelSettings.DEFAULT_MAX_ROW_PER_SHEET) {
			// create new file if exceeding limit line number
			fileInfo = newExperimentalExcelFile(excelFilePrefix, fileInfo.b + 1);
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

	public Pair<File, Integer> getLastExperimentalExcel(String trialFilePrefix) {
		File folder = new File(excelRootFolder);
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
		String filepath = ResourceUtils.appendPath(excelRootFolder,
				StringUtils.concatenate(trialFilePrefix, FILE_IDX_START_CH, String.valueOf(fileIdx), EXCEL_EXT_WITH_DOT));
		File file = new File(filepath);
		isNewFile = true;
		return Pair.of(file, fileIdx);
	}
}
