/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.java.parser.cfg;

/**
 * @author LLT
 *
 */
public class CfgErrorEdge extends CfgEdge {
	private String errorType;

	public CfgErrorEdge(CfgNode source, String errorType) {
		super(source, null);
		this.errorType = errorType;
	}
	
	public CfgErrorEdge(CfgNode source, CfgNode dest) {
		super(source, dest);
	}

	public String getErrorType() {
		return errorType;
	}
}
