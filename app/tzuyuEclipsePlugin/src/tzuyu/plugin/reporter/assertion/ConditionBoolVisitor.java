/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.reporter.assertion;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

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
import tzuyu.engine.bool.Literal;
import tzuyu.engine.bool.NotFormula;
import tzuyu.engine.iface.BoolVisitor;
import tzuyu.engine.utils.StringUtils;

/**
 * @author LLT
 * 
 */
public class ConditionBoolVisitor extends BoolVisitor {
	private IMethod method;
	private StringBuilder sb;

	public ConditionBoolVisitor(IMethod method) {
		this.method = method;
		sb = new StringBuilder();
	}

	@Override
	public void visit(ConjunctionFormula conj) {
		sb.append("(");
		conj.getLeft().accept(this);
		sb.append(")");
		sb.append(conj.getOperation());
		sb.append("(");
		conj.getRight().accept(this);
		sb.append(")");
	}

	@Override
	public void visit(NotFormula formula) {
		sb.append("!(");
		formula.getChild().accept(this);
		sb.append(")");
	}
	
	@Override
	public void visit(Literal literal) {
		if (literal.isNegation()) {
			sb.append("!");
		}
		literal.getAtom().accept(this);
	}
	
	/**
	 * TODO LLT: refactor a little bit?? (all methods below)
	 * something like and(a, b)
	 * and(a, and(b, c)) instead of this thing.
	 */
	@Override
	public void visit(DNFTerm dnfTerm) {
		int size = dnfTerm.getChildren().size();
		for (int index = 0; index < size; index++) {
			dnfTerm.getChildren().get(index).accept(this);
			if (index < size - 1) {
				sb.append(" && ");
			}
		}
	}
	
	@Override
	public void visit(DNF dnf) {
		StringBuilder sb = new StringBuilder();
		int size = dnf.getChildren().size();
		for (int index = 0; index < size; index++) {
			sb.append("(");
			dnf.getChildren().get(index).accept(this);
			sb.append(")");
			if (index < size - 1) {
				sb.append("||");
			}
		}
	}
	
	@Override
	public void visit(CNFClause cnfClause) {
		int size = cnfClause.getChildren().size();
		for (int i = 0; i < size; i++) {
			cnfClause.getChildren().get(i).accept(this);
			if (i < size - 1) {
				sb.append("||");
			}
		}
	}
	
	@Override
	public void visit(CNF cnf) {
		int size = cnf.getChildren().size();
		for (int i = 0; i < size; i++) {
			sb.append("(");
			cnf.getChildren().get(i).accept(this);			
			sb.append(")");
			if (i < size - 1) {
				sb.append("&&");
			}
		}
	}
	
	@Override
	public void visit(LIAAtom liaAtom) {
		int size = liaAtom.getMVFOExpr().size();
		for (int index = 0; index < size; index++) {
			liaAtom.getMVFOExpr().get(index).accept(this);
			if (index != size - 1) {
				sb.append("+");
			}
		}

		sb.append(" ");
		sb.append(liaAtom.getOperator().getOperator());
		sb.append(" ");
		sb.append(liaAtom.getConstant());
	}
	
	@Override
	public void visit(LIATerm liaTerm) {
		if (liaTerm.getCoefficient() != 1) {
			sb.append("").append(liaTerm.getCoefficient()).append("*");
		}
		liaTerm.getVariable().accept(this);
	}
	
	@Override
	public void visit(ObjectIsNullAtom atom) {
		atom.getKey().accept(this);
		sb.append(" == ").append("null");	
	}
	
	@Override
	public void visit(FieldVar fieldVar) {
		String pName = fieldVar.getName();
		if (StringUtils.isEmpty(pName)) {
			try {
				pName = method.getParameterNames()[fieldVar.getArgIndex() - 1];
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		sb.append(pName);
	}
	
	/**
	 * TODO LLT: totally duplicate code, will refactor further.
	 */
	@Override
	public void visit(StringEqualsValueAtom atom) {
		atom.getKey().accept(this);
		sb.append(" == ").append(atom.getValue());	
	}
	
	@Override
	public void visit(EnumEqualsConstant atom) {
		atom.getKey().accept(this);
		sb.append(" == ").append(atom.getConstantVal());	
	}
	
	@Override
	public void visit(CharEqualsValueAtom atom) {
		atom.getKey().accept(this);
		sb.append(" == ").append(atom.getValue());	
	}
	
	@Override
	public void visit(BooleanIsFalseAtom atom) {
		atom.getKey().accept(this);
		sb.append(" == ").append("false");	
	}
	
	public String getResult() {
		return sb.toString();
	}
}
