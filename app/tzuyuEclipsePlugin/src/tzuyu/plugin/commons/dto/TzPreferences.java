/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.commons.dto;

import org.eclipse.jdt.core.IJavaProject;
import org.osgi.service.prefs.Preferences;

/**
 * @author LLT
 * act like a wrapper of the real configuration 
 * (can be the iLaunchConfigurationor or UserPreferences depend on our solution of action launching)
 */
public abstract class TzPreferences {
	public static final String ATT_OUTPUT_FOLDER = "outputSourceFolder";
	public static final String ATT_OUTPUT_PACKAGE = "outputPackage";
	public static final String ATT_TYPE_SEARCH_SCOPE = "typeSearchScopes";
	
	protected IJavaProject project;
	
	public TzPreferences() {

	}
	
	public abstract void read(Preferences pref);
	
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
