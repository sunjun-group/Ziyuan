/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package evosuite.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import evosuite.core.EvosuiteRunner.EvosuiteResult;
import microbat.instrumentation.cfgcoverage.CoverageOutput;
import microbat.instrumentation.cfgcoverage.graph.CFGInstance;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.FileUtils;

/**
 * @author LLT
 *
 */
public class Configuration {
	private String targetMethodListFile;
	private String evoBaseDir;
	private String evosuitSrcFolder;
	private EvosuiteNewExcelWriter excelWriter;
	private String coverageInfoLogFile;
	private GraphCoverageCsvRecorder recorder;

	public Configuration(String excelFilePath, String graphCoverageFilePath) throws Exception {
		excelWriter = new EvosuiteNewExcelWriter(new File(excelFilePath));
		recorder = new GraphCoverageCsvRecorder(graphCoverageFilePath);
	}

	public String getTestMethodListFile() {
		return targetMethodListFile;
	}

	public void setMethodListFile(String configFile) {
		this.targetMethodListFile = configFile;
	}

	public String getResourceConfigFile() {
		return Configuration.class.getClassLoader().getResource(targetMethodListFile).getFile();
	}

	public String getEvoBaseDir() {
		return evoBaseDir;
	}

	public void setEvoBaseDir(String evoBaseDir) {
		this.evoBaseDir = evoBaseDir;
	}

	public List<String> loadValidMethods() {
		try {
			List<?> objs = org.apache.commons.io.FileUtils.readLines(new File(targetMethodListFile));
			List<String> lines = new ArrayList<String>(objs.size());
			for (Object obj : objs) {
				String line = (String) obj;
				if (!line.startsWith("#")) {
					lines.add(line);
				}
			}
			return lines;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void setCoverageInfoLogFile(String path) {
		this.coverageInfoLogFile = path;
		StringBuilder sb = new StringBuilder( "\nStart new session - ").append(new Date())
				.append("\n==========================================================================================\n");
		FileUtils.appendFile(path, sb.toString());
	}

	public String getEvosuitSrcFolder() {
		return evosuitSrcFolder;
	}

	public void setEvosuitSrcFolder(String evosuitSrcFolder) {
		this.evosuitSrcFolder = evosuitSrcFolder;
	}

	public void updateResult(String classMethod, int line, EvosuiteResult result, CoverageOutput graphCoverage, CFGInstance cfgInstance) {
		writeToExcel(classMethod, line, result);
		recorder.record(classMethod, line, graphCoverage, result, cfgInstance);
		writeToCoverageInfoLog(classMethod, line, result);
	}

	private void writeToCoverageInfoLog(String classMethod, int line, EvosuiteResult result) {
		if (coverageInfoLogFile == null) {
			return;
		}
		StringBuilder sb = new StringBuilder().append(classMethod).append(".").append(line)
				.append("\n------------------------------------------------------------------------------------------\n");
		if (result == null) {
			sb.append("Error!\n");
		} else {
			for (String infoLine : CollectionUtils.nullToEmpty(result.coverageInfo)) {
				sb.append(infoLine).append("\n");
			}
		}
		sb.append("------------------------------------------------------------------------------------------\n");
		FileUtils.appendFile(coverageInfoLogFile, sb.toString());
	}

	private void writeToExcel(String classMethod, int line, EvosuiteResult result) {
		if (excelWriter == null) {
			return;
		}
		ExportData data = new ExportData();
		data.setMethodName(classMethod);
		data.setStartLine(line);
		data.setEvoResult(result);
		try {
			excelWriter.addRowData(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void logError(String methodFullName, int line) {
		updateResult(methodFullName, line, null, null, null);
	}
}
