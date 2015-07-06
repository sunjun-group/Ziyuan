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
	
	public StringValue(String id, String val) {
		super(id, val);
		add(new PrimitiveValue(getChildId(LENGTH_CODE), String.valueOf(val.length())));
	}

}
