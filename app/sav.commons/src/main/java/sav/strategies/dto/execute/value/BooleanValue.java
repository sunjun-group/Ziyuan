/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.dto.execute.value;

/**
 * @author LLT
 *
 */
public class BooleanValue extends PrimitiveValue {
	private Boolean value;

	public BooleanValue(String id, Boolean value) {
		super(id, String.valueOf(value));
		this.value = value;
	}

	@Override
	public Double getDoubleVal() {
		return getDoubleVal(value);
	}
	
	public static Double getDoubleVal(Boolean value) {
		if (value == null) return null;
		
		if (value) {
			return 1.0;
		} else {
			return 0.0;
		}
	}
	
	public Boolean getBooleanVal() {
		return (value == null) ? null : value;
	}
	
	public static BooleanValue of(String id, boolean value) {
		return new BooleanValue(id, value);
	}
	
	public void setValue(Boolean value) {
		this.value = value;
	}
	
	@Override
	public ExecVarType getType() {
		return ExecVarType.BOOLEAN;
	}

	@Override
	public ExecValue clone() {
		BooleanValue clone = new BooleanValue(varId, value);
		clone.valueType = valueType;
		return clone;
	}
}
