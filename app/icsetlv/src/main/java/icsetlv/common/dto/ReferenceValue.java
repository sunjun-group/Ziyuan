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
public class ReferenceValue extends ExecValue {
	protected static final String NULL_CODE = "isNull";

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
}
