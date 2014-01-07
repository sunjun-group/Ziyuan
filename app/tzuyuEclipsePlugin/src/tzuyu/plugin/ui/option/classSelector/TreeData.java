/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.ui.option.classSelector;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;

/**
 * @author LLT
 * 
 */
public class TreeData {
	protected IJavaProject project;
	private List<String> availableTypes;
	private List<String> grayedTypes;
	private List<String> checkedTypes;

	public TreeData() {
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
