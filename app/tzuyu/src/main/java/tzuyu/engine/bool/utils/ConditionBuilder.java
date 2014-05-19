/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.bool.utils;

import tzuyu.engine.bool.FieldVar;
import tzuyu.engine.bool.LIATerm;
import tzuyu.engine.bool.formula.ConjunctionFormula;
import tzuyu.engine.bool.formula.FieldAtom;
import tzuyu.engine.bool.formula.LIAAtom;
import tzuyu.engine.bool.formula.NotFormula;
import tzuyu.engine.iface.ExpressionVisitor;
import tzuyu.engine.model.Formula;
import tzuyu.engine.runtime.RMethod;
import tzuyu.engine.utils.Assert;
import tzuyu.engine.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class ConditionBuilder extends ExpressionVisitor {
	private StringBuilder sb;
	private String[] paramNames;

	public ConditionBuilder(RMethod method) {
		this();
		paramNames = method.getParamNames();
	}

	public ConditionBuilder() {
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
		sb.append(liaAtom.getOperator().getCodeWithSpace());
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
			pName = getParameterName(fieldVar);
		}
		sb.append(pName);
	}

	protected String getParameterName(FieldVar fieldVar) {
		int argIndex = fieldVar.getArgIndex() - 1;
		Assert.assertTrue(paramNames != null && argIndex <= paramNames.length,
				"argIndex and paramNames not match: argIndex=" + argIndex
						+ ", paramNames=" + paramNames);
		return paramNames[argIndex];
	}
	
	@Override
	public void visitFieldAtom(FieldAtom atom) {
		atom.getKey().accept(this);
		sb.append(atom.getOperator().getCodeWithSpace());
		sb.append(atom.getDisplayValue());
	}
	
	@Override
	public void visitConjunctionFormula(ConjunctionFormula cond) {
		int size = cond.getElements().size();
		for (int index = 0; index < size; index++) {
			Formula clause = cond.getElements().get(index);
			sb.append("(");
			clause.accept(this);
			sb.append(")");
			if (index < size - 1) {
				sb.append(cond.getOperator().getCodeWithSpace());
			}
		}
	}
	
	public String getResult() {
		return sb.toString();
	}
}
