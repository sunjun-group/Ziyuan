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

import sav.common.core.utils.CollectionUtils;

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

	@Override
	public String getChildId(String childCode) {
		return childCode;
	}

	@Override
	protected boolean needToRetrieveValue() {
		return false;
	}

	public Double getValue(final String variableId) {
		for (ExecValue child : CollectionUtils.nullToEmpty(children)) {
			if (child.getVarId().equals(variableId)) {
				return Double.valueOf(child.getDoubleVal());
			}
		}
		return null;
	}

	public List<String> getAllLabels() {
		if (children == null) {
			return null;
		}
		List<String> labels = new ArrayList<String>(children.size());
		for (ExecValue child : children) {
			labels.add(child.getVarId());
		}
		return labels;
	}
	
	public double[] getAllValues() {
		if (children == null) {
			return null;
		}
		double[] values = new double[children.size()];
		int i = 0;
		for (ExecValue child : children) {
			values[i++] = child.getDoubleVal();
		}
		return values;
	}

	public int getNumberOfAvailableVariables() {
		if (children == null || children.isEmpty()) {
			return 0;
		} else {
			return children.size();
		}
	}
}
