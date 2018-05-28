/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package evosuite.plugin;

/**
 * @author LLT
 *
 */
public class EvosuitePreferenceData {
	private String projectName;
	private String evosuiteWorkingDir;
	private String targetMethodListFile;

	public String getEvosuiteWorkingDir() {
		return evosuiteWorkingDir;
	}

	public void setEvosuiteWorkingDir(String evosuiteWorkingDir) {
		this.evosuiteWorkingDir = evosuiteWorkingDir;
	}

	public String getTargetMethodListFile() {
		return targetMethodListFile;
	}

	public void setTargetMethodListFile(String targetMethodListFile) {
		this.targetMethodListFile = targetMethodListFile;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

}
