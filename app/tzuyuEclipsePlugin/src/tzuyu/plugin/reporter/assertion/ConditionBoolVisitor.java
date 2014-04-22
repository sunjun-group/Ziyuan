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

import tzuyu.engine.bool.FieldVar;
import tzuyu.engine.bool.LIATerm;
import tzuyu.engine.bool.formula.ConjunctionFormula;
import tzuyu.engine.bool.formula.Eq;
import tzuyu.engine.bool.formula.LIAAtom;
import tzuyu.engine.bool.formula.NotEq;
import tzuyu.engine.bool.formula.NotFormula;
import tzuyu.engine.iface.BoolVisitor;
import tzuyu.engine.model.Formula;
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
	public void visit(NotFormula formula) {
		sb.append("!(");
		formula.getChild().accept(this);
		sb.append(")");
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
		sb.append(liaAtom.getOperator().getCode());
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
	
	@Override
	public <T> void visit(Eq<T> eq) {
		eq.getKey().accept(this);
		sb.append(" == ");
		sb.append(eq.getValueBox().getDisplayValue());
	}
	
	@Override
	public <T> void visit(NotEq<T> eq) {
		eq.getKey().accept(this);
		sb.append(" != ");
		sb.append(eq.getValueBox().getDisplayValue());
	}
	
	@Override
	public void visit(ConjunctionFormula cond) {
		int size = cond.getElements().size();
		for (int index = 0; index < size; index++) {
			Formula clause = cond.getElements().get(index);
			sb.append("(");
			sb.append(clause.toString());
			sb.append(")");
			if (index < size - 1) {
				sb.append(cond.getOperator());
			}
		}
	}
	
	public String getResult() {
		return sb.toString();
	}
	
}
