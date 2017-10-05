/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.commons.event;

import java.util.List;

import org.eclipse.jdt.core.IMethod;

/**
 * @author LLT
 *
 */
public class AnnotationChangeEvent {
	private IMethod targetMethod;
	private List<String> selectedTestcases;
	
	public AnnotationChangeEvent(IMethod targetMethod, List<String> testcases) {
		this.targetMethod = targetMethod;
		this.selectedTestcases = testcases;
	}

	public IMethod getTargetMethod() {
		return targetMethod;
	}

	public void setTargetMethod(IMethod targetMethod) {
		this.targetMethod = targetMethod;
	}

	public List<String> getSelectedTestcases() {
		return selectedTestcases;
	}

	public void setSelectedTestcases(List<String> selectedTestcases) {
		this.selectedTestcases = selectedTestcases;
	}

}
