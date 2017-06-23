/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data.decision;

import java.util.ArrayList;
import java.util.List;

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
	private List<List<CategoryCalculator>> preconditions = new ArrayList<List<CategoryCalculator>>();

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
	 * @param list
	 * @param condFromDivicers condition which builds by dominatee dividers
	 */
	public void addPreconditions(List<List<CategoryCalculator>> dominateePreconds, CategoryCalculator condFromDivicers) {
		if (dominateePreconds.isEmpty()) {
			List<CategoryCalculator> cur = new ArrayList<CategoryCalculator>();
			cur.add(condFromDivicers);
			this.preconditions.add(cur);
		} else {
			List<List<CategoryCalculator>> orgPreconditions = new ArrayList<List<CategoryCalculator>>(preconditions);
			for (List<CategoryCalculator> list : orgPreconditions) {
				List<CategoryCalculator> cur = new ArrayList<CategoryCalculator>(list);
				cur.add(condFromDivicers);
				this.preconditions.add(cur);
			}
		}		
	}
}
