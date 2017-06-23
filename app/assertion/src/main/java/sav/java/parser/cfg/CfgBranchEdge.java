/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.java.parser.cfg;

import japa.parser.ast.expr.Expression;

/**
 * @author LLT
 *
 */
public class CfgBranchEdge extends CfgEdge {
	private Expression condition;

	public CfgBranchEdge(CfgNode source, CfgNode dest, Expression cond) {
		super(source, dest);
		this.condition = cond;
	}

	public Expression getCondition() {
		return condition;
	}

	public void setCondition(Expression condition) {
		this.condition = condition;
	}
	
	@Override
	public CfgEdge clone(CfgNode newDest) {
		CfgBranchEdge edge = new CfgBranchEdge(getSource(), newDest, getCondition());
		edge.setProperties(getProperties());
		return edge;
	}
	
	@Override
	public Type getType() {
		return Type.BRANCH;
	}

	@Override
	public String toString() {
		String label = getLabel();
		label = label.isEmpty() ? label : String.format("   [%s]", label);
		return String.format("%s  %s---->  %s", getSource().toString(), label, getDest());
	}
}
