/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.dto.execute.value;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.Value;

import sav.common.core.utils.Assert;

/**
 * @author LLT
 * 
 */
public class ArrayValue extends ReferenceValue {
	private static final String SUM_CODE = "sum";
	private static final String MAX_CODE = "max";
	private static final String MIN_CODE = "min";
	private static final String LENGTH_CODE = "length";

	public ArrayValue(String id) {
		super(id, false);
	}
	
	public String getElementId(int i) {
		return String.format("%s[%s]", varId, i);
	}

	private void setSum(double sum) {
		// add(new PrimitiveValue(getChildId(SUM_CODE), String.valueOf(sum)));
		add(new DoubleValue(getChildId(SUM_CODE), sum));
	}
	
	private void setMax(double max) {
		// add(new PrimitiveValue(getChildId(MAX_CODE), String.valueOf(max)));
		add(new DoubleValue(getChildId(MAX_CODE), max));
	}

	private void setMin(double min) {
		// add(new PrimitiveValue(getChildId(MIN_CODE), String.valueOf(min)));
		add(new DoubleValue(getChildId(MIN_CODE), min));
	}

	private void setLength(int length) {
		// add(new PrimitiveValue(getChildId(LENGTH_CODE), String.valueOf(length)));
		add(new IntegerValue(getChildId(LENGTH_CODE), length));
	}

	public void setValue(final ArrayReference ar) {
		Assert.assertTrue(ar != null,
				"Value of ArrayReference is null, in this case, initialize execValue using ReferenceValue.nullValue instead!");
		final int arrayLength = ar.length();
		setLength(arrayLength);
		double sum = 0.0;
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for (int i=0; i<arrayLength; i++) {
			Value value = ar.getValue(i);
			if (value != null && com.sun.jdi.PrimitiveValue.class.isAssignableFrom(value.getClass())) {
				com.sun.jdi.PrimitiveValue pv = (com.sun.jdi.PrimitiveValue) value;
				final double doubleValue = pv.doubleValue();
				sum += doubleValue;
				if (min > doubleValue) {
					min = doubleValue;
				}
				if (max < doubleValue) {
					max = doubleValue;
				}
			}
		}
//		setSum(sum);
		if (Double.compare(Double.MAX_VALUE, min) != 0) {
//			setMin(min);
		}

		if (Double.compare(Double.MIN_VALUE, max) != 0) {
//			setMax(max);
		}
	}

	@Override
	public Double getDoubleVal() {
		String lengthId = getChildId(LENGTH_CODE);
		for (ExecValue child : children) {
			if (lengthId.equals(child.getVarId())) {
				return child.getDoubleVal();
			}
		}
		return super.getDoubleVal();
	}
	
	@Override
	public ExecVarType getType() {
		return ExecVarType.ARRAY;
	}
}
