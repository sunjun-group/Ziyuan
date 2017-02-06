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
public class CfgTrueEdge extends CfgBranchEdge {

	public CfgTrueEdge(CfgNode source, CfgNode dest, Expression cond) {
		super(source, dest, cond);
	}

	@Override
	public Type getType() {
		return Type.TRUE;
	}
	
	@Override
	public CfgEdge clone(CfgNode newDest) {
		CfgTrueEdge edge = new CfgTrueEdge(getSource(), newDest, getCondition());
		edge.setProperties(getProperties());
		return edge;
	}
	
	@Override
	public String getLabel() {
		return "TRUE";
	}
}
