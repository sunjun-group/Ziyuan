/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.common.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sav.strategies.dto.BreakPoint;

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

	public List<String> getAllVariableLabels(final boolean onlyCommonOnes, final BreakPoint bkp) {
		final Set<String> labels = new HashSet<String>();
		boolean firstloop = true;
		for (BreakpointValue value : getPassValues(bkp)) {
			if (firstloop || !onlyCommonOnes) {
				labels.addAll(value.getAllLabels());
			} else {
				labels.retainAll(value.getAllLabels());
			}
		}
		return new ArrayList<String>(labels);
	}

	@Override
	public String toString() {
		return "TcExecResult [passValues=" + passValues + ", \nfailValues="
				+ failValues + "]";
	}
	
	public String toString(List<BreakPoint> bkps) {
		StringBuilder sb = new StringBuilder("TcExecResult: \n");
		for (BreakPoint bkp : bkps) {
			sb.append(bkp.getId()).append("\n");
			append(sb, bkp);
		}
		return sb.toString();
	}
	
	private String append(StringBuilder sb, BreakPoint bkp) {
		sb.append("passValues: \n");
		append(sb, getPassValues(bkp));
		sb.append("failValues: \n");
		append(sb, getFailValues(bkp));
		return sb.toString();
	}

	private void append(StringBuilder sb, List<BreakpointValue> pv) {
		if (!pv.isEmpty()) {
			List<String> vars = pv.get(0).appendVarId(new ArrayList<String>());
			sb.append(vars).append("\n");
			for (BreakpointValue bkVal : pv) {
				List<Double> values = bkVal.appendVal(new ArrayList<Double>());
				sb.append(values).append("\n");
			}
			
		}
	}
}
