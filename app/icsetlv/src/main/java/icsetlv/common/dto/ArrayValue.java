/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.common.dto;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.Value;

/**
 * @author LLT
 * 
 */
public class ArrayValue extends ExecValue {
	private static final String NULL_CODE = "isNull";
	private static final String SUM_CODE = "sum";
	private static final String MAX_CODE = "max";
	private static final String MIN_CODE = "min";
	private static final String LENGTH_CODE = "length";

	public ArrayValue(String id) {
		super(id);
	}

	private void setNull(boolean isNull) {
		add(new PrimitiveValue(getChildId(NULL_CODE), isNull ? "1" : "0"));
	}

	private void setSum(double sum) {
		add(new PrimitiveValue(getChildId(SUM_CODE), String.valueOf(sum)));
	}

	private void setMax(double max) {
		add(new PrimitiveValue(getChildId(MAX_CODE), String.valueOf(max)));
	}

	private void setMin(double min) {
		add(new PrimitiveValue(getChildId(MIN_CODE), String.valueOf(min)));
	}

	private void setLength(int length) {
		add(new PrimitiveValue(getChildId(LENGTH_CODE), String.valueOf(length)));
	}

	public void setValue(final ArrayReference ar) {
		setNull(ar == null);
		final int arrayLength = ar.length();
		setLength(arrayLength);
		double sum = 0.0;
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for (Value value : ar.getValues()) {
			if (com.sun.jdi.PrimitiveValue.class.isAssignableFrom(value.getClass())) {
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
		setSum(sum);
		if (arrayLength > 0) {
			setMin(min);
			setMax(max);
		}
	}

	@Override
	public double getDoubleVal() {
		String lengthId = getChildId(LENGTH_CODE);
		for (ExecValue child : children) {
			if (lengthId.equals(child.getVarId())) {
				return child.getDoubleVal();
			}
		}
		return super.getDoubleVal();
	}
}
