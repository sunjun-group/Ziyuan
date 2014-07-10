/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.commons.dto;

import org.eclipse.jdt.core.IJavaProject;

/**
 * @author LLT
 * act like a wrapper of the real configuration 
 * (can be the iLaunchConfigurationor or UserPreferences depend on our solution of action launching)
 */
public class TzPreferences {
	protected IJavaProject project;
	
	public TzPreferences() {

	}
	
	public void setJavaProject(IJavaProject project) {
		this.project = project;
	}

	public String getProjectName() {
		return project.getElementName();
	}
	
	public IJavaProject getProject() {
		return project;
	}
}
