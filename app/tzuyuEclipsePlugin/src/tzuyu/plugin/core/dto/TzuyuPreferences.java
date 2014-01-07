/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.core.dto;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;

/**
 * @author LLT
 * act like a wrapper of the real configuration 
 * (can be the iLaunchConfigurationor or UserPreferences depend on our solution of action launching)
 */
public class TzuyuPreferences {
	@Deprecated
	protected IJavaProject project;
	@Deprecated
	// TODO LLT: remove from the configuration.
	private List<String> availableTypes;
	@Deprecated
	private List<String> grayedTypes;
	@Deprecated
	private List<String> checkedTypes;
	
	public TzuyuPreferences() {
		availableTypes = new ArrayList<String>();
		grayedTypes = new ArrayList<String>();
		checkedTypes = new ArrayList<String>();
	}
	
	public void setJavaProject(IJavaProject project) {
		// init configuration by project
		// put into appropriate real configuration.
		this.project = project;
	}
	
	public List<String> getAvailableTypes() {
		return availableTypes;
	}

	public List<String> getCheckedTypes() {
		return checkedTypes;
	}
	
	public List<String> getGrayedTypes() {
		return grayedTypes;
	}

	public List<String> getCheckedMethods(String typeString) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getAvailableMethods(String typeString) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getProjectName() {
		// TODO Auto-generated method stub
		return null;
	}
}
