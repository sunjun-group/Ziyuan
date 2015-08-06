/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.variable;

import sav.common.core.utils.Assert;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.Field;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Value;

/**
 * @author LLT
 *
 */
public class JdiParam {
	private JdiParamType type;
	/* local variable */
	private LocalVariable variable;
	/* field */
	private Field field;
	/* non-static */
	private ObjectReference obj;
	/* static */
	private ReferenceType objType;
	/* for arr */
	private int idx;
	/* value */
	private Value value;
	
	private JdiParam() {}
	
	public LocalVariable getLocalVariable() {
		return variable;
	}

	public Field getField() {
		return field;
	}

	public ObjectReference getObj() {
		return obj;
	}

	public ReferenceType getObjType() {
		return objType;
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}
	
	public JdiParamType getType() {
		return type;
	}
	
	public int getIdx() {
		return idx;
	}
	
	public ArrayReference getArrayRef() {
		Assert.assertTrue(type == JdiParamType.ARRAY_ELEMENT,
				"Expected arrayType, but get ", type.name());
		return (ArrayReference) obj;
	}

	@Override
	public String toString() {
		return "JdiParam [type=" + type + ", variable=" + variable + ", field="
				+ field + ", obj=" + obj + ", objType=" + objType + ", idx="
				+ idx + ", value=" + value + "]";
	}

	public static JdiParam localVariable(LocalVariable variable, Value value) {
		JdiParam param = new JdiParam();
		param.type = JdiParamType.LOCAL_VAR;
		param.variable = variable;
		param.value = value;
		return param;
	}
	
	public static JdiParam staticField(Field field, ReferenceType objType, Value value) {
		JdiParam param = new JdiParam();
		param.type = JdiParamType.STATIC_FIELD;
		param.field = field;
		param.objType = objType;
		param.value = value;
		return param;
	}

	public static JdiParam nonStaticField(Field field, ObjectReference objRef, Value value) {
		JdiParam param = new JdiParam();
		param.type = JdiParamType.NON_STATIC_FIELD;
		param.field = field;
		param.obj = objRef;
		param.value = value;
		return param;
	}
	
	public static JdiParam arrayElement(ArrayReference array, int idx, Value value) {
		JdiParam param = new JdiParam();
		param.type = JdiParamType.ARRAY_ELEMENT;
		param.obj = array;
		param.value = value;
		param.idx = idx;
		return param;
	}

	public enum JdiParamType {
		NON_STATIC_FIELD,
		ARRAY_ELEMENT,
		STATIC_FIELD,
		LOCAL_VAR
	}
}