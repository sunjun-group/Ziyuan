/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.iface;

import refiner.bool.BooleanIsFalseAtom;
import refiner.bool.CharEqualsValueAtom;
import refiner.bool.EnumEqualsConstant;
import refiner.bool.FieldVar;
import refiner.bool.LIAAtom;
import refiner.bool.LIATerm;
import refiner.bool.ObjectIsNullAtom;
import refiner.bool.StringEqualsValueAtom;
import tzuyu.engine.bool.CNF;
import tzuyu.engine.bool.CNFClause;
import tzuyu.engine.bool.ConjunctionFormula;
import tzuyu.engine.bool.DNF;
import tzuyu.engine.bool.DNFTerm;
import tzuyu.engine.bool.False;
import tzuyu.engine.bool.Literal;
import tzuyu.engine.bool.NotFormula;
import tzuyu.engine.bool.True;

/**
 * @author LLT
 *
 */
public abstract class BoolVisitor {

	public void visit(ConjunctionFormula conjFormula) {
		// do nothing by default 
	}

	public void visit(NotFormula notFormula) {
		// do nothing by default 		
	}

	public void visit(True formula) {
		// do nothing by default 		
	}

	public void visit(False false1) {
		// do nothing by default 		
	}

	public void visit(Literal literal) {
		// do nothing by default 				
	}

	public void visit(DNFTerm dnfTerm) {
		// do nothing by default
	}

	public void visit(DNF dnf) {
		// do nothing by default
	}

	public void visit(CNFClause cnfClause) {
		// do nothing by default
	}

	public void visit(CNF cnf) {
		// do nothing by default
	}

	public void visit(LIAAtom liaAtom) {
		// do nothing by default		
	}
	
	public void visit(LIATerm liaTerm) {
		// do nothing by default
	}

	public void visit(ObjectIsNullAtom objectIsNullAtom) {
		// do nothing by default		
	}

	public void visit(FieldVar fieldVar) {
		// do nothing by default
	}

	public void visit(StringEqualsValueAtom atom) {
		// do nothing by default
	}

	public void visit(EnumEqualsConstant enumEqualsConstant) {
		// do nothing by default
	}

	public void visit(CharEqualsValueAtom charEqualsValueAtom) {
		// do nothing by default
	}

	public void visit(BooleanIsFalseAtom atom) {
		// do nothing by default
	}

}
