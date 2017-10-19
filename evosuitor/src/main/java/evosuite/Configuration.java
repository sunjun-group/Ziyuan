/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package evosuite;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import evosuite.EvosuiteRunner.EvosuiteResult;
import sav.strategies.dto.AppJavaClassPath;

/**
 * @author LLT
 *
 */
public class Configuration {
	private List<String> sourceFolder;
	private String configFile;
	private String evoBaseDir;
	private String evosuitSrcFolder;

	public Configuration(AppJavaClassPath appClasspath) {
		sourceFolder = Arrays.asList(appClasspath.getSrc());
	}

	public List<String> getSourceFolder() {
		return sourceFolder;
	}

	public void setSourceFolder(List<String> sourceFolder) {
		this.sourceFolder = sourceFolder;
	}

	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	public String getResourceConfigFile() {
		return Configuration.class.getClassLoader().getResource(configFile).getFile();
	}

	public String getEvoBaseDir() {
		return evoBaseDir;
	}

	public void setEvoBaseDir(String evoBaseDir) {
		this.evoBaseDir = evoBaseDir;
	}

	public List<String> loadValidMethods() {
		try {
			List<?> objs = org.apache.commons.io.FileUtils.readLines(new File(configFile));
			List<String> lines = new ArrayList<String>(objs.size());
			for (Object obj : objs) {
				lines.add((String) obj);
			}
			return lines;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getEvosuitSrcFolder() {
		return evosuitSrcFolder;
	}

	public void setEvosuitSrcFolder(String evosuitSrcFolder) {
		this.evosuitSrcFolder = evosuitSrcFolder;
	}

	public void updateResult(String classMethod, int line, EvosuiteResult result) {
		// do nothing
	}
}
