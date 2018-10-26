/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.dto.execute.value;

import sav.common.core.utils.PrimitiveUtils;

/**
 * @author LLT
 *
 */
public class PrimitiveValue extends ExecValue {
	private String strVal;
	private String type;
	private ExecVarType execVarType;
	
	public PrimitiveValue(String id, String strVal) {
		super(id);
		this.strVal = strVal;
	}

	public PrimitiveValue(String id, String strVal, String type) {
		this(id, strVal);
		this.type = type;
		execVarType = ExecVarType.primitiveTypeOf(type);
	}

	public String getStrVal() {
		return strVal;
	}
	
	@Override
	public Double getDoubleVal() {
		try {
			return Double.parseDouble(strVal);
		} catch (NumberFormatException e) {
			return super.getDoubleVal();
		}
	}
	
	public static PrimitiveValue valueOf(String varId, String type, String strValue) {
		if (PrimitiveUtils.isString(type)) {
			return new StringValue(varId, strValue);
		} else if (PrimitiveUtils.isBoolean(type)) {
			return new BooleanValue(varId, Boolean.valueOf(strValue));
		} else if (PrimitiveUtils.isByte(type)) {
			return new ByteValue(varId, Byte.valueOf(strValue));
		} else if (PrimitiveUtils.isChar(type)) {
			return new CharValue(varId, Character.valueOf(strValue.charAt(0)));
		} else if (PrimitiveUtils.isDouble(type)) {
			return new DoubleValue(varId, Double.valueOf(strValue));
		} else if (PrimitiveUtils.isFloat(type)) {
			return new FloatValue(varId, Float.valueOf(strValue));
		} else if (PrimitiveUtils.isInteger(type)) {
			return new IntegerValue(varId, Integer.valueOf(strValue));
		} else if (PrimitiveUtils.isShort(type)) {
			return new ShortValue(varId, Short.valueOf(strValue));
		}
		return new PrimitiveValue(varId, strValue);
	}
	
	public static PrimitiveValue valueOf(ExecVar execVar, Number value) {
		PrimitiveValue child = null;
		String id = execVar.getVarId();
		switch (execVar.getType()) {
		case STRING:
			child = new StringValue(id, value.toString());
			break;
		case BOOLEAN:
			child = BooleanValue.of(id, value.intValue() > 0 ? true : false);
			break;
		case BYTE:
			child = ByteValue.of(id, value.byteValue());
			break;
		case CHAR:
			child = CharValue.of(id, (char) value.intValue());
			break;
		case DOUBLE:
			child = DoubleValue.of(id, value.doubleValue());
			break;
		case FLOAT:
			child = FloatValue.of(id, value.floatValue());
			break;
		case INTEGER:
			child = IntegerValue.of(id, value.intValue());
			break;
		case LONG:
			child = LongValue.of(id, value.longValue());
			break;
		case SHORT:
			child = LongValue.of(id, value.longValue());
			break;
		default:
			child = new PrimitiveValue(id, value.toString(), execVar.getValueType());
			break;
		}
		return child;
	}
	
	@Override
	public String toString() {
		return String.format("(%s:%s:%s)", varId, getType(), strVal);
	}

	@Override
	public ExecVarType getType() {
		if (execVarType != null) {
			return execVarType;
		}
		return ExecVarType.PRIMITIVE;
	}

	@Override
	public boolean isPrimitive() {
		return true;
	}
	
	public String getValueType() {
		return type;
	}
}
