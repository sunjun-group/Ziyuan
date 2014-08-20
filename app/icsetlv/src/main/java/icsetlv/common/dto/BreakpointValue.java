/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.common.dto;




/**
 * @author LLT
 *
 */
public class BreakpointValue extends ExecValue {
	
	public BreakpointValue(String id) {
		super(id);
	}
	
	public String getBkpId() {
		return getVarId();
	}

}
