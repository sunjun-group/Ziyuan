/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.common.dto;


import java.util.List;

import sav.strategies.dto.BreakPoint;

/**
 * @author LLT
 * 
 */
public class BreakpointData {
	private BreakPoint bkp;
	private List<BreakpointValue> passValues;
	private List<BreakpointValue> failValues;

	public BreakPoint getBkp() {
		return bkp;
	}

	public void setBkp(BreakPoint bkp) {
		this.bkp = bkp;
	}

	public List<BreakpointValue> getPassValues() {
		return passValues;
	}

	public void setPassValues(List<BreakpointValue> passValues) {
		this.passValues = passValues;
	}

	public List<BreakpointValue> getFailValues() {
		return failValues;
	}

	public void setFailValues(List<BreakpointValue> failValues) {
		this.failValues = failValues;
	}

}
