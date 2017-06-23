/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.dto;

import sav.common.core.Constants;
import sav.common.core.utils.BreakpointUtils;

/**
 * @author LLT
 * 
 */
public class ClassLocation {
	protected String id;
	protected String classCanonicalName;
	protected String methodName; 
	protected int lineNo = Constants.UNKNOWN_LINE_NUMBER; 
	
	public ClassLocation(String className, String methodName, int lineNumber) {
		this.classCanonicalName = className;
		this.methodName = methodName;
		this.lineNo = lineNumber;
	}

	public String getClassCanonicalName() {
		return classCanonicalName;
	}

	public void setClassCanonicalName(String classCanonicalName) {
		this.classCanonicalName = classCanonicalName;
	}

	public String getMethodName() {
		return methodName;
	}

	public int getLineNo() {
		return lineNo;
	}

	public void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}
	
	public String getId() {
		if (id == null) {
			id = BreakpointUtils.getLocationId(this);
		}
		return id;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		return classCanonicalName.hashCode() * prime + lineNo;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		//
		ClassLocation other = (ClassLocation) obj;

		return classCanonicalName.equals(other.getClassCanonicalName())
				&& lineNo == other.getLineNo();
	}

	@Override
	public String toString() {
		return getId();
	}
	
}
