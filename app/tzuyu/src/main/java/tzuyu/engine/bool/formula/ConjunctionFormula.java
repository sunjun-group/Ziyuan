/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.bool.formula;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.bool.Operator;
import tzuyu.engine.bool.Var;
import tzuyu.engine.bool.utils.FormulaUtils;
import tzuyu.engine.model.Formula;
import tzuyu.engine.utils.CollectionUtils;

/**
 * @author LLT
 * 
 */
public abstract class ConjunctionFormula implements Formula {
	protected List<Formula> elements;

	public ConjunctionFormula() {
		elements = new ArrayList<Formula>();
	}
	
	public ConjunctionFormula(Formula left, Formula right) {
		this();
		add(left);
		add(right);
	}

	/**
	 * @deprecated
	 * only for internal usage.
	 * do not use this function,
	 * try {@link FormulaUtils#and(Formula, Formula)}
	 * or {@link FormulaUtils#or(Formula, Formula)}
	 */
	@Deprecated
	public void add(Formula formula) {
		CollectionUtils.addIfNotNullNotExist(elements, formula);
	}

	public List<Var> getReferencedVariables() {
		List<Var> result = new ArrayList<Var>();
	    for (Formula clause : elements) {
	      List<Var> vars = clause.getReferencedVariables();
	      result.removeAll(vars);
	      result.addAll(vars);
	    }
	    return result;
	}
	
	public List<Atom> getAtomics() {
		List<Atom> result = new ArrayList<Atom>();
		for (Formula clause : elements) {
			List<Atom> atoms = clause.getAtomics();
			result.removeAll(atoms);
			result.addAll(atoms);
		}
		return result;
	}
	
	public abstract ConjunctionFormula createNew();

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int size = elements.size();
		for (int index = 0; index < size; index++) {
			Formula clause = elements.get(index);
			sb.append("(");
			sb.append(clause.toString());
			sb.append(")");
			if (index < size - 1) {
				sb.append(getOperator().getCode());
			}
		}
		return sb.toString();
	}

	public abstract Operator getOperator();
	
	public List<Formula> getElements() {
		return elements;
	}
}
