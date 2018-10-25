/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.dto.execute.value;

import java.util.List;

import com.sun.jdi.ArrayReference;

import sav.common.core.utils.Assert;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 * will be removed, be replaced with MultiDimArrayValue
 */
@Deprecated
public class ArrayValue extends ReferenceValue {
	public static final String LENGTH_CODE = "length";
	private List<ArrValueElement> elements; 

	public ArrayValue(String id) {
		super(id, false);
	}
	
	public ArrayValue(String id, boolean isNull) {
		super(id, isNull);
	}
	
	public String getElementId(int i) {
		return ExecVarHelper.getArrayElementID(this.varId, i);
	}
	
	@Override
	public String getChildId(String childCode) {
		return ExecVarHelper.getArrayChildID(this.varId, childCode);
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
		return elements;
	}

	public void setLength(int length) {
		add(new IntegerValue(getChildId(LENGTH_CODE), length));
	}
	
	public IntegerValue getLengthValue() {
		ExecValue value = findVariableById(getChildId(LENGTH_CODE));
		if (value == null) {
			return null;
		}
		return (IntegerValue) value;
	}

	public void setValue(final ArrayReference ar) {
		Assert.assertTrue(ar != null,
				"Value of ArrayReference is null, in this case, initialize execValue using ReferenceValue.nullValue instead!");
		final int arrayLength = ar.length();
		setLength(arrayLength);
	}

	@Override
	public Double getDoubleVal() {
		String lengthId = getChildId(LENGTH_CODE);
		for (ExecValue child : children) {
			if (lengthId.equals(child.getVarId())) {
				return child.getDoubleVal();
			}
		}
		return super.getDoubleVal();
	}
	
	@Override
	public ExecVarType getType() {
		return ExecVarType.ARRAY;
	}

	public static ArrayValue convert(ExecValue val) {
		ArrayValue arrValue = new ArrayValue(val.getVarId());
		if (val.getChildren() != null) {
			for (ExecValue child : val.getChildren()) {
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

	public ExecValue getElementByFlattenLocation(int i) {
		// TODO Auto-generated method stub
		return null;
	}

}
