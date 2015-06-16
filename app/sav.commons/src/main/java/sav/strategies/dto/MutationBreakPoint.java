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
public class MutationBreakPoint extends BreakPoint{
	private int oldLineNumber;
	
	public MutationBreakPoint(BreakPoint oldBreakPoint, int newLineNumber){
		super(oldBreakPoint.getClassCanonicalName(), newLineNumber);
		this.setVars(oldBreakPoint.getVars());
		this.oldLineNumber = oldBreakPoint.getLineNo();
	}

	/**
	 * Use the line number before mutation
	 */
	@Override
	public String toString() {
		return "MutationBreakPoint [classCanonicalName=" + classCanonicalName
				+ ", methodName=" + methodSign + ", lineNo=" + oldLineNumber
				+ ", vars=" + getVars() + ", charStart=" + getCharStart() + ", charEnd="
				+ getCharEnd() + "]";
	}
	
	
}
