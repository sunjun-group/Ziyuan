/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data.testtarget;

import java.util.List;

/**
 * @author LLT
 *
 */
public class TargetClass {
	private String className;
	private String classSimpleName;
	private List<String> fields;
	private List<TargetMethod> targetMethods;
	
	public TargetClass() {
		
	}
	
	public List<String> getFields() {
		return fields;
	}
	
	public String getClassName() {
		return className;
	}
}
