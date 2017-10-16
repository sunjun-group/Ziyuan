/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.test;

import static learntest.core.commons.test.TestSettings.ENABLE_LOG_TEST;

import org.apache.commons.lang.StringUtils;
import org.slf4j.helpers.MessageFormatter;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import sav.common.core.utils.FileUtils;

/**
 * @author LLT
 *
 */
public class TestTool {
	public static final TestTool EMTPY_INSTANCE = new TestTool() {
		@Override
		protected void flush() {
		}
	};
	private String logFileName;
	private StringBuilder sb = new StringBuilder();

	public static boolean isEnable() {
		return ENABLE_LOG_TEST;
	}

	protected String getModuleName() {
		return "Learntest";
	}
	
	protected void flush() {
		appendFile(sb.toString());
		sb = new StringBuilder();
	}
	
	protected void flush(boolean cond) {
		if (cond) {
			flush();
		}
	}

	protected void appendFile(String content) {
		FileUtils.appendFile(getLogFileName(), content);
	}

	protected String getLogFileName() {
		if (logFileName == null) {
			logFileName = FileUtils.getFilePath(TestSettings.LOG_FILES_FOLDER, getModuleName() + ".txt");
		}
		return logFileName;
	}

	public void log(Object... texts) {
		if (!isEnable()) {
			return;
		}
		sb.append(StringUtils.join(texts)).append("\n");
	}
	
	public void logf(String text) {
		if (!isEnable()) {
			return;
		}
		log(text);
		flush();
	}
	
	public void log(String text) {
		if (!isEnable()) {
			return;
		}
		sb.append(text).append("\n");
	}
	
	public void logFormat(String text, Object... args) {
		if (!isEnable()) {
			return;
		}
		sb.append(MessageFormatter.arrayFormat(text, args).getMessage()).append("\n");
	}

	public void logFirstCoverage(double firstCoverage, CfgCoverage cfgCoverage) {
	}
	
}
