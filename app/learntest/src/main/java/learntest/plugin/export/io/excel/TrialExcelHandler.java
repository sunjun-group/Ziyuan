/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.export.io.excel;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.plugin.export.io.excel.common.ExcelSettings;
import learntest.plugin.export.io.excel.common.SimpleExcelHandler;
import learntest.plugin.export.io.excel.common.SimpleExcelReader;
import learntest.plugin.export.io.excel.common.SimpleExcelWriter;

/**
 * @author LLT
 *
 */
public class TrialExcelHandler extends SimpleExcelHandler<Trial>{
	private Logger log = LoggerFactory.getLogger(TrialExcelHandler.class);
	private TrialExcelReader trialReader;
	
	public TrialExcelHandler(ExcelSettings settings) throws Exception {
		super(settings);
	}

	public TrialExcelHandler(String outputFolder, String string) throws Exception {
		super(outputFolder, string, TrialExcelConstants.DEFAULT_EXCEL_APPEND);
	}

	protected SimpleExcelReader initExcelReader() {
		trialReader = new TrialExcelReader();
		return trialReader;
	}
	
	protected SimpleExcelWriter<Trial> initExcelWriter(File file) throws Exception {
		return  new TrialExcelWriter(file);
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Trial> readOldTrials() {
		if (isNewFile) {
			return Collections.EMPTY_LIST;
		} 
		try {
			trialReader.reset(fileInfo.a);
			return trialReader.readDataSheet().values();
		} catch (Exception e) {
			log.debug(e.getMessage());
			return Collections.EMPTY_LIST;
		}
	}
}
