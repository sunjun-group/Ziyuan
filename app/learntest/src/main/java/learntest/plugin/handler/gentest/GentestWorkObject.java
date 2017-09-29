/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.handler.gentest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;

/**
 * @author LLT
 */
public class GentestWorkObject {
	private Map<String, WorkProject> projMap;
	private List<WorkProject> workProjects;
	
	public GentestWorkObject() {
		projMap = new HashMap<String, WorkProject>();
		workProjects = new ArrayList<WorkProject>();
	}
	
	public void extend(IJavaElement element) {
		IJavaProject javaProject = element.getJavaProject();
		String prjName = javaProject.getElementName();
		WorkProject workProject = projMap.get(prjName);
		if (workProject == null) {
			workProject = new WorkProject(javaProject);
			projMap.put(prjName, workProject);
			workProjects.add(workProject);
		}
		workProject.extend(element);
	}
	
	public List<WorkProject> getWorkProjects() {
		return workProjects;
	}
	
	public WorkProject getWorkProject(IJavaProject project) {
		return projMap.get(project.getElementName());
	}
}
