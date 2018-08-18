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
public class PrimitiveValue extends ExecValue {
	private String strVal;
	private String type;
	
	public PrimitiveValue(String id, String strVal) {
		super(id);
		this.strVal = strVal;
	}

	public PrimitiveValue(String id, String strVal, String type) {
		this(id, strVal);
		this.type = type;
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
	
	public static PrimitiveValue valueOf(ExecVar execVar, Number value) {
		PrimitiveValue child = null;
		String id = execVar.getVarId();
		switch (execVar.getType()) {
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
		return ExecVarType.PRIMITIVE;
	}
	
	public ExecVarType getSpecificVarType() {
		return ExecVarType.primitiveTypeOf(type);
	}

	@Override
	public boolean isPrimitive() {
		return true;
	}
	
	public String getValueType() {
		return type;
	}
}
