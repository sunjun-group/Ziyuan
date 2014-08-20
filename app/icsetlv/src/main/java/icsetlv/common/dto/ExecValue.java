/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.common.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author LLT
 *
 */
public abstract class ExecValue {
	protected String varId;
	protected List<ExecValue> children;
	public static final int NOT_NULL_VAL = 1;
	
	protected ExecValue(String id) {
		this.varId = id;
	}
	
	public List<ExecValue> getChildren() {
		return children;
	}
	
	public String getVarId() {
		return varId;
	}
	
	public void add(ExecValue child) {
		if (children == null) {
			children = new ArrayList<ExecValue>();
		}
		children.add(child);
	}
	
	public double getDoubleVal() {
		return NOT_NULL_VAL;
	}
	
	public String getChildId(String childCode) {
		return String.format("%s.%s", varId, childCode);
	}
	
	public String getChildId(int i) {
		return getChildId(String.valueOf(i));
	}
	
	public void retrieveValue(Map<String, double[]> allLongsVals, int i,
			int size) {
		if (!allLongsVals.containsKey(varId)) {
			allLongsVals.put(varId, new double[size]);
		}
		double[] valuesOfVarId = allLongsVals.get(varId);
		valuesOfVarId[i] = getDoubleVal();
		if (children != null) {
			for (ExecValue child : children) {
				child.retrieveValue(allLongsVals, i, size);
			}
		}
	}
	
	@Override
	public String toString() {
		return String.format("(%s:%s)", varId, children);
	}
}
