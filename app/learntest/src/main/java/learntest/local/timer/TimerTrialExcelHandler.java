/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.local.timer;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.plugin.export.io.excel.MultiTrial;
import learntest.plugin.export.io.excel.Trial;
import learntest.plugin.export.io.excel.TrialExcelConstants;
import learntest.plugin.export.io.excel.TrialExcelHandler;
import learntest.plugin.export.io.excel.TrialExcelReader;
import learntest.plugin.export.io.excel.common.ExcelSettings;
import learntest.plugin.export.io.excel.common.SimpleExcelHandler;
import learntest.plugin.export.io.excel.common.SimpleExcelReader;
import learntest.plugin.export.io.excel.common.SimpleExcelWriter;

/**
 * @author LLT
 *
 */
public class TimerTrialExcelHandler extends TrialExcelHandler{
	private Logger log = LoggerFactory.getLogger(TimerTrialExcelHandler.class);
	private TrialExcelReader trialReader;
	
	public TimerTrialExcelHandler(ExcelSettings settings) throws Exception {
		super(settings);
	}

	public TimerTrialExcelHandler(String outputFolder, String string) throws Exception {
		super(outputFolder, string);
	}

	protected SimpleExcelReader initExcelReader() {
		trialReader = new TrialExcelReader();
		return trialReader;
	}
	
	protected SimpleExcelWriter<Trial> initExcelWriter(File file) throws Exception {
		return  new TimerTrialExcelWriter(file);
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
	
	@Override
	public void export(Trial rowData) throws Exception {
		if (rowData instanceof MultiTrial) {
			List<Trial> trials = ((MultiTrial) rowData).getTrials();
			for (Trial trial : trials) {
				super.export(trial);
			}
		}else {
			super.export(rowData);
		}
	}
}
