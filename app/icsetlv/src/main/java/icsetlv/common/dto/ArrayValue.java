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
public class ArrayValue extends ExecValue {
	private static final String LENGTH_CODE = "length";

	public ArrayValue(String id) {
		super(id);
	}

	public void setLength(int length) {
		add(new PrimitiveValue(getChildId(LENGTH_CODE), String.valueOf(length)));
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
