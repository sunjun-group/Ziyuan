/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.dto.execute.value;

import java.util.List;

import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 * will be removed, be replaced with MultiDimArrayValue
 */
public class ArrayValue extends ReferenceValue {
	public static final String LENGTH_CODE = "length";
	private List<ArrValueElement> elements; 
	
	public ArrayValue(String id) {
		super(id, false);
	}
	
	public ArrayValue(String id, boolean isNull) {
		super(id, isNull);
	}
	
	public int getLength() {
		ExecValue lengthValue = findVariableById(getChildId(LENGTH_CODE));
		if (lengthValue != null) {
			return ((IntegerValue) lengthValue).getIntegerVal();
		} else {
			return -1;
		}
	}

	public void setLength(int length) {
		ExecValue lengthValue = findVariableById(getChildId(LENGTH_CODE));
		if (lengthValue != null) {
			((IntegerValue) lengthValue).setValue(length);
		} else {
			lengthValue = new IntegerValue(getChildId(LENGTH_CODE), length);
			add(lengthValue);
		}
	}

	public String getElementId(int i) {
		return ExecVarHelper.getArrayElementID(this.varId, i);
	}
	
	public ArrValueElement[] getElementArray(int size) {
		ArrValueElement[] elements = new ArrValueElement[size];
		for (ArrValueElement ele : getElements()) {
			if(ele.getIdx() < size) {
				elements[ele.getIdx()] = ele;
			}
		}
		return elements;
	}
	
	@Override
	public void add(ExecValue child) {
		add(child, false);
	}
	
	@Override
	public void add(ExecValue child, boolean overrideIfExist) {
		super.add(child, overrideIfExist);
		String childId = getElementName(child);
		if (childId.startsWith("[")) {
			int idx = Integer.valueOf(childId.substring(1, childId.indexOf("]")));
			addElement(idx, child);
		}
	}

	public void addElement(int idx, ExecValue value) {
		elements = CollectionUtils.initIfEmpty(elements);
		elements.add(new ArrValueElement(idx, value));
	}
	
	public List<ArrValueElement> getElements() {
		return CollectionUtils.nullToEmpty(elements);
	}

	@Override
	public Double getDoubleVal() {
		return (double) getLength();
	}
	
	@Override
	public ExecVarType getType() {
		return ExecVarType.ARRAY;
	}

	public static ArrayValue convert(ExecValue val) {
		ArrayValue arrValue = new ArrayValue(val.getVarId());
		if (val.getChildren() != null) {
			ReferenceValue refVal = (ReferenceValue) val;
			for (ExecValue child : refVal.getChildren()) {
				arrValue.add(child, true);
			}
		}
		return arrValue;
	}
	
	public List<ExecValue> collectAllValue(List<ExecValue> allValues) {
		for (ArrValueElement ele : CollectionUtils.nullToEmpty(getElements())) {
			if (ele.value.getType() == ExecVarType.ARRAY) {
				((ArrayValue) ele.value).collectAllValue(allValues);
			} else {
				allValues.add(ele.value);
			}
		}
		return allValues;
	}
	
	public String getElementName(ExecValue child) {
		return child.getVarId().substring(this.getVarId().length());
	}
	
	public int[] getLocation(ExecValue element, int dimension) {
		String name = getElementName(element);
		int[] location = new int[dimension];
		int idx = 0;
		int i = 0;
		while ((i = name.indexOf("[", i)) >= 0) {
			int j = name.indexOf("]", i + 1);
			location[idx ++] = Integer.valueOf(name.substring(i + 1, j));
			i = j;
		}
		return location;
	}
	
	public static class ArrValueElement {
		private int idx;
		private ExecValue value;

		public ArrValueElement(int idx, ExecValue value) {
			this.idx = idx;
			this.value = value;
		}

		public int getIdx() {
			return idx;
		}

		public ExecValue getValue() {
			return value;
		}

		@Override
		public String toString() {
			return "[idx=" + idx + "] = " + value + "]";
		}
	}

	@Override
	public ExecValue clone() {
		ArrayValue clone = new ArrayValue(varId);
		clone.valueType = valueType;
		for (ArrValueElement ele : getElements()) {
			clone.addElement(ele.idx, ele.value.clone());
		}
		for (ExecValue child : getChildren()) {
			clone.add(child.clone());
		}
		return clone;
	}
}
