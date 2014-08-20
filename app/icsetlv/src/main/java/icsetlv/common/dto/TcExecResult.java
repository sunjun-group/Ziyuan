/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.common.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LLT 
 * Testcases execution result.
 */
public class TcExecResult {

	private List<BreakpointValue> passValues;
	private List<BreakpointValue> failValues;

	public TcExecResult(List<BreakpointValue> passVals,
			List<BreakpointValue> failVals) {
		this.passValues = passVals;
		this.failValues = failVals;
	}

	public List<BreakpointValue> getPassValues(BreakPoint bkp) {
		return getValuesOfBkp(bkp.getId(), passValues);
	}

	public List<BreakpointValue> getFailValues(BreakPoint bkp) {
		return getValuesOfBkp(bkp.getId(), failValues);
	}
	
	private List<BreakpointValue> getValuesOfBkp(String bkpId,
			List<BreakpointValue> allValues) {
		List<BreakpointValue> result = new ArrayList<BreakpointValue>();
		for (BreakpointValue val : allValues) {
			if (val.getBkpId().equals(bkpId)) {
				result.add(val);
			}
		}
		return result;
	}

	@Override
	public String toString() {
		return "TcExecResult [passValues=" + passValues + ", failValues="
				+ failValues + "]";
	}
}
