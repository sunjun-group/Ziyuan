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
	private boolean isNull = false;

	public ReferenceValue(String id) {
		super(id);
	}
	
	public ReferenceValue(String id, boolean isNull) {
		super(id);
		this.isNull = isNull;
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
		return isNull;
	}

	public void setNull(boolean isNull) {
		this.isNull = isNull;
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}

	@Override
	public ExecValue clone() {
		ReferenceValue value = new ReferenceValue(varId, isNull);
		value.children = new ArrayList<ExecValue>(getChildren().size());
		for (ExecValue child : getChildren()) {
			value.children.add(child.clone());
		}
		value.valueType = valueType;
		value.varId = varId;
		return value;
	}

}
