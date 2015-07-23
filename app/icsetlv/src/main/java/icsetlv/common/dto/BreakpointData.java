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

import libsvm.core.Category;
import libsvm.core.Machine.DataPoint;
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

	public List<DataPoint> toDatapoints(List<String> labels) {
		List<DataPoint> datapoints = new ArrayList<DataPoint>();
		for (BreakpointValue bValue : passValues) {
			datapoints.add(toDataPoint(labels, bValue, Category.POSITIVE));
		}

		for (BreakpointValue bValue : failValues) {
			datapoints.add(toDataPoint(labels, bValue, Category.NEGATIVE));
		}
		return datapoints;
	}
	
	private DataPoint toDataPoint(List<String> labels, BreakpointValue bValue,
			Category category) {
		double[] lineVals = new double[labels.size()];
		int i = 0;
		for (String variableName : labels) {
			final Double value = bValue.getValue(variableName, 0.0);
			lineVals[i++] = value;
		}
		DataPoint dp = new DataPoint(labels.size());
		dp.setCategory(category);
		dp.setValues(lineVals);
		return dp;
	}

	@Override
	public String toString() {
		return "BreakpointData (" + bkp + "), \npassValues=" + passValues
				+ ", \nfailValues=" + failValues + "]";
	}
	
}
