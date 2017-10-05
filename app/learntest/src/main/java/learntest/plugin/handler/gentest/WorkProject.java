/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.handler.gentest;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;

import learntest.plugin.utils.IProjectUtils;
import sav.common.core.Constants;

/**
 * @author LLT
 *
 */
public class WorkProject {
	private IJavaProject project;
	private List<IJavaElement> selectedElements;
	private List<String> toTestElementFullNames = new ArrayList<String>();
	private List<IJavaElement> toTestElements;

	public WorkProject(IJavaProject project) {
		selectedElements = new ArrayList<IJavaElement>();
		this.project = project;
		toTestElements = new ArrayList<IJavaElement>();
	}
	
	public void extend(IJavaElement element) {
		selectedElements.add(element);
		if (!isCovered(element, true)) {
			toTestElements.add(element);
		}
	}

	/**
	 * a weak method which only checks depend on the ordered provided list of elements of a multiple selection.
	 * like 
	 * 		project
	 * 			-- package
	 * 					-- method
	 * Note: will not work well on unordered provided element list!
	 */
	private boolean isCovered(IJavaElement element, boolean updateNameList) {
		String fullName = IProjectUtils.getFullName(element);
		return isCovered(fullName, updateNameList);
	}

	private boolean isCovered(String fullName, boolean updateNameList) {
		boolean covered = false;
		int joinerIdx = fullName.indexOf(Constants.DOT);
		while (joinerIdx > 0) {
			String parent = fullName.substring(0, joinerIdx);
			if (toTestElementFullNames.contains(parent)) {
				covered = true;
				break;
			}
			joinerIdx = fullName.indexOf(Constants.DOT, joinerIdx + 1);
		}
		
		if (joinerIdx < 0) {
			covered = toTestElementFullNames.contains(fullName);
		} 
		if (!covered & updateNameList) {
			toTestElementFullNames.add(fullName);
		}
		return covered;
	}
	
	public boolean isTestingRelevant(IJavaElement element) {
		String fullName = IProjectUtils.getFullName(element);
		if (isCovered(fullName, false)) {
			return true;
		}
		for (String name : toTestElementFullNames) {
			if (name.startsWith(fullName)) {
				return true;
			}
		}
		return false;
	}
	
	public IJavaProject getProject() {
		return project;
	}
	
	public List<IJavaElement> getToTestElements() {
		return toTestElements;
	}

	public List<String> getToTestElementFullNames() {
		return toTestElementFullNames;
	}
}
