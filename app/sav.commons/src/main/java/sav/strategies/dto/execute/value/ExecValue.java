/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.dto.execute.value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT, modified by Yun Lin
 *
 */
public abstract class ExecValue implements GraphNode{
	
	private boolean isVisited = false;
	protected ExecValue parent;
	/**
	 * indicate a variable name
	 */
	protected String varId;
	protected List<ExecValue> children = new ArrayList<>();
	
	protected boolean isElementOfArray = false;
	
	
	public static final int NOT_NULL_VAL = 1;
	
	protected ExecValue(String id) {
		this.varId = id;
	}
	
	@Override
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
	
	/**
	 * the value of this node will be stored in allLongsVals.get(varId)[i];
	 * 
	 * @param allLongsVals: a map of Variable and its values in all testcases.
	 * @param i: current index of allLongsVals.get(varId)
	 * @param size: size of allLongsVals
	 */
	public void retrieveValue(Map<String, double[]> allLongsVals, int i,
			int size) {
		if (needToRetrieveValue()) {
			if (!allLongsVals.containsKey(varId)) {
				allLongsVals.put(varId, new double[size]);
			}
			
			double[] valuesOfVarId = allLongsVals.get(varId);
			valuesOfVarId[i] = getDoubleVal();
		}
		if (children != null) {
			for (ExecValue child : children) {
				child.retrieveValue(allLongsVals, i, size);
			}
		}
	}
	
	public List<Double> appendVal(List<Double> values) {
		if (needToRetrieveValue()) {
			values.add(getDoubleVal());
		}
		for (ExecValue child : CollectionUtils.initIfEmpty(children)) {
			child.appendVal(values);
		}
		return values;
	}
	
	public List<String> appendVarId(List<String> vars) {
		if (needToRetrieveValue()) {
			vars.add(varId);
		}
		for (ExecValue child : CollectionUtils.initIfEmpty(children)) {
			child.appendVarId(vars);
		}
		return vars;
	}
	
	/**
	 * TODO: to improve, varId of a child is always 
	 * started with its parent's varId
	 */
	public ExecValue findVariableById(String varId) {
		if (this.varId.equals(varId)) {
			return this;
		} else {
			for (ExecValue child : CollectionUtils.initIfEmpty(children)) {
				ExecValue match = child.findVariableById(varId);
				if (match != null) {
					return match;
				}
			}
			return null;
		}
	}
	
	/* only affect for the current execValue, not for its children */
	protected boolean needToRetrieveValue() {
		return true;
	}

	@Override
	public String toString() {
		return String.format("(%s:%s)", varId, children);
	}
	
	public boolean isElementOfArray() {
		return isElementOfArray;
	}

	public void setElementOfArray(boolean isElementOfArray) {
		this.isElementOfArray = isElementOfArray;
	}
	
	
	@Override
	public boolean isVisited() {
		return isVisited;
	}

	@Override
	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}
	
	@Override
	public ExecValue getParent() {
		return parent;
	}

	public void setParent(ExecValue parent) {
		this.parent = parent;
	}
	
	@Override
	public boolean match(GraphNode node) {
		if(node instanceof GraphNode){
			ExecValue thatValue = (ExecValue)node;
			if(thatValue.getVarId().equals(this.getVarId()) && 
					thatValue.getType().equals(this.getType())){
				return true;
			}
		}
		return false;
	}

	public abstract ExecVarType getType();
}
