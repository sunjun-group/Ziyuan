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
public class StringValue extends PrimitiveValue {
	
	public StringValue(String id, String val) {
		super(id, val);
	}

	@Override
	public ExecVarType getType() {
		return ExecVarType.STRING;
	}
	
	@Override
	protected boolean needToRetrieveValue() {
		return false;
	}
}
