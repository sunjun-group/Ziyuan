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
public class CfgFalseEdge extends CfgBranchEdge {

	public CfgFalseEdge(CfgNode source, CfgNode dest, Expression cond) {
		super(source, dest, cond);
	}

	@Override
	public Type getType() {
		return Type.FALSE;
	}
	
	@Override
	public CfgEdge clone(CfgNode newDest) {
		CfgFalseEdge edge = new CfgFalseEdge(getSource(), newDest, getCondition());
		edge.setProperties(getProperties());
		return edge;
	}
	
	@Override
	public String getLabel() {
		return "FALSE";
	}
}
