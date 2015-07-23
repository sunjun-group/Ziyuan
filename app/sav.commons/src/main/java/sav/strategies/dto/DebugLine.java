/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.dto;
/**
 * @author khanh
 *
 */
/**
 * @author LLT
 *
 */
public class DebugLine extends BreakPoint {
	private int orgLineNo;
	
	public DebugLine(BreakPoint orgBreakpoint, int newLineNo){
		super(orgBreakpoint.getClassCanonicalName(), newLineNo);
		this.setVars(orgBreakpoint.getVars());
		this.orgLineNo = orgBreakpoint.getLineNo();
	}

	public int getOrgLineNo() {
		return orgLineNo;
	}

	@Override
	public String toString() {
		return "DebugLine [orgLineNo=" + orgLineNo + ", id=" + id
				+ ", classCanonicalName=" + classCanonicalName + ", lineNo="
				+ lineNo + "]";
	}
	
}
