/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.handler.gentest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;

import learntest.plugin.utils.IProjectUtils;

/**
 * @author LLT
 *
 */
public class WorkProject {
	private IJavaProject project;
	private List<IJavaElement> elementsToTest;

	public WorkProject(IJavaProject project) {
		elementsToTest = new ArrayList<IJavaElement>();
		this.project = project;
	}
	
	public void extend(IJavaElement element) {
		elementsToTest.add(element);
	}
	
	public IJavaProject getProject() {
		return project;
	}
	
	public Iterator<IJavaElement> elementIterator() {
		return new Iterator<IJavaElement>() {
			private static final int INVALID_IDX = -1;
			int currentIdx = INVALID_IDX;
			int nextIdx = INVALID_IDX;
			List<String> visitedElements = new ArrayList<String>();
			
			@Override
			public IJavaElement next() {
				if (hasNext()) {
					IJavaElement result = elementsToTest.get(nextIdx);
					currentIdx = nextIdx;
					nextIdx = INVALID_IDX;
					return result;
				}
				return null;
			}
			
			@Override
			public boolean hasNext() {
				if (nextIdx != INVALID_IDX) {
					return true;
				}
				updateNextIdx();
				return nextIdx != INVALID_IDX;
			}

			private void updateNextIdx() {
				for (int i = (currentIdx + 1); i < elementsToTest.size(); i++) {
					IJavaElement ele = elementsToTest.get(i);
					String fullName = IProjectUtils.getFullName(ele);
					boolean visited = false;
					for (String visitedEle : visitedElements) {
						if (fullName.startsWith(visitedEle)) {
							visited = true;
							break;
						}
					}
					if (!visited) {
						visitedElements.add(fullName);
						nextIdx = i;
						return;
					}
				}
				nextIdx = INVALID_IDX;
			}
		};
	}
}
