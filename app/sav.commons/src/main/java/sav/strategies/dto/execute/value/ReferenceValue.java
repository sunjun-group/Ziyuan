/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.dto.execute.value;

import com.sun.jdi.ClassType;

/**
 * @author LLT, modified by Yun Lin
 *
 */
@SuppressWarnings("restriction")
public class ReferenceValue extends ExecValue {
	protected static final String NULL_CODE = "isNull";
	
	private ClassType classType; 
	
	private long referenceID = -1;

	public ReferenceValue(String id, boolean isNull) {
		super(id);
		add(BooleanValue.of(getChildId(NULL_CODE), isNull));
	}
	
	public ReferenceValue(String id, boolean isNull, long referenceID, ClassType type) {
		super(id);
		add(BooleanValue.of(getChildId(NULL_CODE), isNull));
		setReferenceID(referenceID);
		setClassType(type);
	}
	
	public void setClassType(ClassType type) {
		this.classType = type;
	}
	
	public ClassType getClassType(){
		return classType;
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		if(classType != null){
			buffer.append(classType.name() + ": ");			
		}
		else{
			buffer.append("unknown type: ");
		}
		
		buffer.append(referenceID);
		String print = buffer.toString();
		
		return print;
	}
	
	public static ReferenceValue nullValue(String id) {
		return new ReferenceValue(id, true);
	}
	
	public long getReferenceID() {
		return referenceID;
	}

	public void setReferenceID(long referenceID) {
		this.referenceID = referenceID;
	}

	@Override
	public ExecVarType getType() {
		return ExecVarType.REFERENCE;
	}
	
}
