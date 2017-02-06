/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.java.parser.cfg;

import japa.parser.ast.Node;

/**
 * @author LLT
 *
 */
public class DecisionNode extends AbstractCfgNode {

	public DecisionNode(Node astNode) {
		super(astNode);
	}

	@Override
	public Type getType() {
		return Type.DECISIONS;
	}

	@Override
	public String toString() {
		return AstUtils.toString(getAstNode()) + "?";
	}
}
