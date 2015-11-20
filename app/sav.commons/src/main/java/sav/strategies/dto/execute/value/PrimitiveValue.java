/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.dto.execute.value;



/**
 * @author LLT, modified by Yun Lin
 *
 */
public class PrimitiveValue extends ExecValue {
	
	private String strVal;
	
	private String primitiveType;

	public PrimitiveValue(String id, String strVal, String type) {
		super(id);
		this.strVal = strVal;
		this.primitiveType = type;
	}

	public String getStrVal() {
		return strVal;
	}
	
	@Override
	public double getDoubleVal() {
		try {
			return Double.parseDouble(strVal);
		} catch (NumberFormatException e) {
			return super.getDoubleVal();
		}
	}
	
	@Override
	public String toString() {
		return String.format("(%s:%s)", varId, strVal);
	}

	@Override
	public ExecVarType getType() {
		return ExecVarType.PRIMITIVE;
	}
	
	public String getPrimitiveType(){
		return this.primitiveType;
	}
	
	public void setPrimitiveType(String type){
		this.primitiveType = type;
	}
}
