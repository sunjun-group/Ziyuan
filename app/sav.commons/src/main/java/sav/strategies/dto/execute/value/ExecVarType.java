/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.dto.execute.value;

import static sav.common.core.utils.PrimitiveUtils.*;

/**
 * @author LLT
 *
 */
public enum ExecVarType {
	REFERENCE,
	BOOLEAN,
	INTEGER,
	BYTE,
	CHAR,
	DOUBLE,
	FLOAT,
	LONG,
	SHORT,
	STRING,
	PRIMITIVE,
	ARRAY;

	public static ExecVarType primitiveTypeOf(String type) {
		if (isBoolean(type)) {
			return BOOLEAN;
		}
		if (isInteger(type)) {
			return ExecVarType.INTEGER;
		}
		if (isByte(type)) {
			return BYTE;
		}
		if (isChar(type)) {
			return CHAR;
		}
		if (isDouble(type)) {
			return DOUBLE;
		}
		if (isFloat(type)) {
			return FLOAT;
		}
		if (isLong(type)) {
			return ExecVarType.LONG;
		}
		if (isShort(type)) {
			return SHORT;
		}
		if (isString(type)) {
			return ExecVarType.STRING;
		}
		return null;
	}
}
