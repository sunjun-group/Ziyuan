/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data.decision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import cfgcoverage.jacoco.analysis.data.BranchRelationship;
import cfgcoverage.jacoco.analysis.data.CfgNode;
import libsvm.core.CategoryCalculator;
import libsvm.core.Divider;
import sav.common.core.formula.Formula;

/**
 * @author LLT
 *
 */
public class Precondition {
	private Formula trueFalse;
	private Formula oneMore;
	private List<Divider> dividers;
	private List<List<CategoryCalculator>> preconditions = new ArrayList<List<CategoryCalculator>>(); /** each element of list is a path of dominator indeed*/
	private HashMap<CfgNode, BranchRelationship> path = new HashMap<>(); /** dominator node take which branch to reach it */
	private boolean visited = false;

	public Formula getTrueFalse() {
		return trueFalse;
	}

	public void setTrueFalse(Formula trueFalse) {
		this.trueFalse = trueFalse;
	}

	public Formula getOneMore() {
		return oneMore;
	}

	public void setOneMore(Formula oneMore) {
		this.oneMore = oneMore;
	}

	public List<Divider> getDividers() {
		return dividers;
	}

	public void setDividers(List<Divider> dividers) {
		this.dividers = dividers;
	}

	public List<List<CategoryCalculator>> getPreconditions() {
		return preconditions;
	}

	public void setPreconditions(List<List<CategoryCalculator>> preconditions) {
		this.preconditions = preconditions;
	}

	public void addPreconditions(List<List<CategoryCalculator>> dominateePreconds) {
		for (List<CategoryCalculator> precond : dominateePreconds) {
			this.preconditions.add(new ArrayList<CategoryCalculator>(precond));
		}
	}

	/**
	 * 
	 * 	each element of preconditions is a list of CC where every CC is 'and' relationship, i.e. Positive iff every CC return Positive
	 * 	but each element is 'or' relationship, i.e. positive iff one element return Positive
	 * 
	 * @param dominatorPreconds 
	 * @param condFromDivicers condition which builds by dominatee dividers
	 */
	public void addPreconditions(List<List<CategoryCalculator>> dominatorPreconds, CategoryCalculator condFromDivicers) {
		if (dominatorPreconds.isEmpty()) {
			List<CategoryCalculator> cur = new ArrayList<CategoryCalculator>();
			cur.add(condFromDivicers);
			this.preconditions.add(cur);
		} else {
//			List<List<CategoryCalculator>> orgPreconditions = new ArrayList<List<CategoryCalculator>>(preconditions);
//			for (List<CategoryCalculator> list : orgPreconditions) {
//				List<CategoryCalculator> cur = new ArrayList<CategoryCalculator>(list);
//				cur.add(condFromDivicers);
//				this.preconditions.add(cur);
//			}
			HashMap<String, Integer> map = new HashMap<>();
			for (List<CategoryCalculator> list : dominatorPreconds) {
				StringBuffer sbBuffer = new StringBuffer();
				for (int i =0; i<list.size(); i++){
					sbBuffer.append(list.get(i).toString()+",");
				}
				if (!map.containsKey(sbBuffer.toString())) {
					map.put(sbBuffer.toString(), 1);
					List<CategoryCalculator> cur = new ArrayList<CategoryCalculator>(list);
					cur.add(condFromDivicers);
					this.preconditions.add(cur);
				}
			}
		}		
	}
	
	public Precondition clone() {
		Precondition precondition = new Precondition();
		precondition.setTrueFalse(trueFalse);
		
		List<Divider> dividers = new LinkedList<>();
		dividers.addAll(this.dividers);
		precondition.setDividers(dividers);
		
		List<List<CategoryCalculator>> preconditions = new ArrayList<List<CategoryCalculator>>();
		preconditions.addAll(this.preconditions);
		precondition.setPreconditions(preconditions);
		
		return precondition;
	}

	public HashMap<CfgNode, BranchRelationship> getPath() {
		return path;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	
	
}
