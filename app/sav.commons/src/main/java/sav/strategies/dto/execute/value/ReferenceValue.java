/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.dto.execute.value;

import java.util.ArrayList;

/**
 * @author LLT
 *
 */
public class ReferenceValue extends ExecValue {
	public static final String NULL_CODE = "isNull";

	public ReferenceValue(String id) {
		super(id);
	}
	
	public ReferenceValue(String id, boolean isNull) {
		super(id);
		add(BooleanValue.of(getChildId(NULL_CODE), isNull));
	}
	
	public static ReferenceValue nullValue(String id) {
		return new ReferenceValue(id, true);
	}
	
	@Override
	public ExecVarType getType() {
		return ExecVarType.REFERENCE;
	}
	
	public String getFieldName(ExecValue child) {
		return child.getVarId().substring(this.getVarId().length() + 1);
	}

	@Override
	public String getStrVal() {
		return null;
	}
	
	public boolean isNull() {
		ExecValue isNullVal = findVariableById(getChildId(NULL_CODE));
		if (isNullVal == null) {
			return false;
		}
		return ((BooleanValue) isNullVal).getBooleanVal();
	}

	public void setNull(boolean isNull) {
		ExecValue isNullVal = findVariableById(getChildId(NULL_CODE));
		if (isNullVal == null) {
			isNullVal = BooleanValue.of(getChildId(NULL_CODE), isNull);
			add(isNullVal);
		} else {
			((BooleanValue)isNullVal).setValue(isNull);
		}
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}

	@Override
	public ExecValue clone() {
		ReferenceValue value = new ReferenceValue(varId, isNull());
		value.children = new ArrayList<ExecValue>(getChildren().size());
		for (ExecValue child : getChildren()) {
			value.children.add(child.clone());
		}
		value.valueType = valueType;
		value.varId = varId;
		return value;
	}
}
