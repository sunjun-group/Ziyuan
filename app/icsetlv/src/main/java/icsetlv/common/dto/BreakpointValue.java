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
import java.util.concurrent.atomic.AtomicInteger;

import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StringUtils;
import sav.strategies.dto.execute.value.ArrayValue;
import sav.strategies.dto.execute.value.BooleanValue;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVarType;
import sav.strategies.dto.execute.value.ReferenceValue;

/**
 * @author LLT
 * 
 */
public class BreakpointValue extends ExecValue {
	private static final AtomicInteger count = new AtomicInteger(0); 
	
	public BreakpointValue() {
		this(StringUtils.lowLineJoin("genId_", count.getAndDecrement()));
	}

	public BreakpointValue(String id) {
		super(id);
	}
	
	public static void resetCount() {
		count.set(0);
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
			for (ExecValue child : CollectionUtils.initIfEmpty(value.getChildren())) {
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
		System.currentTimeMillis();
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
			if (value.getType() == ExecVarType.REFERENCE) {
				labels.add(BooleanValue.getDoubleVal(((ReferenceValue)value).isNull()));
			} else if (value.getType() == ExecVarType.ARRAY) {
				labels.add(BooleanValue.getDoubleVal(((ArrayValue)value).isNull()));
				labels.add((double)((ArrayValue) value).getLength());
			}
			
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

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BreakpointValue) || obj == null) {
			return false;
		}
		BreakpointValue breakpointValue = (BreakpointValue) obj;
		double[] allValues = this.getAllValues();
		double[] values = breakpointValue.getAllValues();
		if (allValues.length != values.length) {
			return false;
		}
		for (int i = 0; i < values.length; i++) {
			if (allValues[i] != values[i]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getStrVal() {
		return null;
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}

	@Override
	public ExecValue clone() {
		BreakpointValue bkpValue = new BreakpointValue(varId);
		List<ExecValue> children = new ArrayList<>();
		for (ExecValue child : getChildren()) {
			children.add(child.clone());
		}
		bkpValue.children = children;
		return bkpValue;
	}
}
