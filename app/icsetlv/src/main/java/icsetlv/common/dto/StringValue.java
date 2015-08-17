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
public class StringValue extends PrimitiveValue {
	private static final String LENGTH_CODE = "length";
	private static final String IS_EMPTY = "isEmpty";
	
	public StringValue(String id, String val) {
		super(id, val);
		add(new BooleanValue(getChildId(IS_EMPTY), val.isEmpty()));
		add(new PrimitiveValue(getChildId(LENGTH_CODE), String.valueOf(val.length())));
	}

	@Override
	public ExecVarType getType() {
		return ExecVarType.STRING;
	}
}
