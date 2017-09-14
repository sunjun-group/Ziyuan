/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.CoreException;

import learntest.plugin.utils.IStatusUtils;

/**
 * @author LLT
 *
 */
public class LearntestLogger {

	public static void initLog4j(String projectName) throws CoreException {
		try {
			ResourceBundle log4j = ResourceBundle.getBundle("learntest_log4j");
			Properties props = new Properties();
			for (String key : log4j.keySet()) {
				props.setProperty(key, log4j.getString(key));
			}
			String logFilePath = getLearntestLogFile(projectName);
			System.out.println("log file: " + logFilePath);
			props.setProperty("log4j.appender.file.File", logFilePath);
			PropertyConfigurator.configure(props);
		} catch (Exception e) {
			throw new CoreException(IStatusUtils.exception(e, e.getMessage()));
		}
	}

	/**
	 * TODO LLT: using reset configuration to set the corresponding log file for each project, instead of
	 * using global log file for workspace. 
	 */
	private static String getLearntestLogFile(String projectName) throws CoreException {
		try {
			String outputFolder = ProjectSetting.getLearntestOutputFolder(projectName);
			File logFile = new File(outputFolder, "learntest-eclipse.log");
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
			return logFile.getAbsolutePath();
		} catch (IOException e) {
			throw new CoreException(IStatusUtils.exception(e, e.getMessage()));
		}
	}
	
}
