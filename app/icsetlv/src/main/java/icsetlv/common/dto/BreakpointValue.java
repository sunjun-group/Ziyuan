/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.common.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVarType;

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

	public Double getValue(final String variableId, Double defaultIfNull) {
		Double value = getValue(variableId, this);
		if (value != null) {
			return value;
		}
		return defaultIfNull;
	}

	private Double getValue(final String variableId, final ExecValue value) {
		if (value.getVarId().equals(variableId)) {
			return Double.valueOf(value.getDoubleVal());
		} else {
			for (ExecValue child : CollectionUtils.nullToEmpty(value.getChildren())) {
				Double val = getValue(variableId, child);
				if (val != null) {
					return val;
				}
			}
			return null;
		}
	}
	
	public Set<String> getAllLabels() {
		return getChildLabels(this);
	}

	private Set<String> getChildLabels(final ExecValue value) {
		final Set<String> labels = new HashSet<String>();
		if (value == null || value.getChildren() == null || value.getChildren().isEmpty()) {
			labels.add(value.getVarId());
		} else {
			for (ExecValue child : value.getChildren()) {
				labels.addAll(getChildLabels(child));
			}
		}
		return labels;
	}

	public double[] getAllValues() {
		List<Double> list = getChildValues(this);
		double[] array = new double[list.size()];
		int i = 0;
		for (Double val : list) {
			array[i++] = val.doubleValue();
		}
		return array;
	}

	private List<Double> getChildValues(final ExecValue value) {
		if (value == null || value.getChildren() == null || value.getChildren().isEmpty()) {
			return Arrays.asList(value.getDoubleVal());
		} else {
			List<Double> labels = new ArrayList<Double>();
			for (ExecValue child : value.getChildren()) {
				labels.addAll(getChildValues(child));
			}
			return labels;
		}
	}

	public int getNumberOfAvailableVariables() {
		if (children == null || children.isEmpty()) {
			return 0;
		} else {
			return children.size();
		}
	}
	
	@Override
	public ExecVarType getType() {
		return null;
	}
}
