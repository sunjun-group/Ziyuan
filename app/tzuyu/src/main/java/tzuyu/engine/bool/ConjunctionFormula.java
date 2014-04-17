/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.bool;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.iface.BoolVisitor;
import tzuyu.engine.model.Formula;

/**
 * @author LLT
 * 
 */
public abstract class ConjunctionFormula implements Formula {
	protected Formula left;
	protected Formula right;

	public ConjunctionFormula(Formula left, Formula right) {
		this.left = left;
		this.right = right;
	}

	public List<Var> getReferencedVariables() {
		List<Var> leftVars = left.getReferencedVariables();
		List<Var> rightVars = right.getReferencedVariables();
		List<Var> result = new ArrayList<Var>(leftVars);
		result.removeAll(rightVars);
		result.addAll(rightVars);
		return result;
	}
	
	public List<Atom> getAtomics() {
		List<Atom> leftAtoms = left.getAtomics();
		List<Atom> rightAtoms = right.getAtomics();
		leftAtoms.removeAll(rightAtoms);
		leftAtoms.addAll(rightAtoms);
		return leftAtoms;
	}
	
	@Override
	public String toString() {
		return left.toString() + getOperation() + right.toString();
	}

	@Override
	public void accept(BoolVisitor visitor) {
		visitor.visit(this);
	}

	public Formula getLeft() {
		return left;
	}

	public Formula getRight() {
		return right;
	}
	
	public abstract String getOperation();
}
