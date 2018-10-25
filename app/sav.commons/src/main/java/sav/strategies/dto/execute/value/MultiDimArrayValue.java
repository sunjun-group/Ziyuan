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
 * 
 */
public class MultiDimArrayValue extends ReferenceValue {
	private List<ArrayValueElement> elements; 
	private int dimension;
	private int[] length;

	public MultiDimArrayValue(String id) {
		super(id, false);
	}
	
	public MultiDimArrayValue(String id, boolean isNull) {
		super(id, isNull);
	}
	
//	@Override
//	public void add(ExecValue child, boolean overrideIfExist) {
//		super.add(child, overrideIfExist);
//		String childId = getElementName(child);
//		if (childId.startsWith("[")) {
//			int idx = Integer.valueOf(childId.substring(1, childId.indexOf("]")));
//			addElement(idx, child);
//		}
//	}

//	public void addElement(int[] idx, ExecValue value) {
//		elements = CollectionUtils.initIfEmpty(elements);
//		elements.add(new ArrValueElement(idx, value));
//	}
	
	public List<ArrayValueElement> getElements() {
		return elements;
	}

	public int[] getLength() {
		return length;
	}

	public void setLength(int[] length) {
		this.length = length;
	}

	public int getDimension() {
		return dimension;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

//	@Override
//	public Double getDoubleVal() {
//		return (double) length;
//	}
	
	@Override
	public ExecVarType getType() {
		return ExecVarType.ARRAY;
	}

	public static MultiDimArrayValue convert(ExecValue val) {
		MultiDimArrayValue arrValue = new MultiDimArrayValue(val.getVarId());
		if (val.getChildren() != null) {
			for (ExecValue child : val.getChildren()) {
				arrValue.add(child, true);
			}
		}
		return arrValue;
	}
	
	public List<ExecValue> collectAllValue(List<ExecValue> allValues) {
		for (ArrayValueElement ele : CollectionUtils.nullToEmpty(getElements())) {
			if (ele.value.getType() == ExecVarType.ARRAY) {
				((MultiDimArrayValue) ele.value).collectAllValue(allValues);
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
	
	public static class ArrayValueElement {
		private int[] idx;
		private ExecValue value;

		public ArrayValueElement(int[] idx, ExecValue value) {
			this.idx = idx;
			this.value = value;
		}

		public int[] getIdx() {
			return idx;
		}

		public ExecValue getValue() {
			return value;
		}
		
		public String getVarId() {
			return value.getVarId();
		}

		@Override
		public String toString() {
			return "[idx=" + idx + "] = " + value + "]";
		}
	}

}
