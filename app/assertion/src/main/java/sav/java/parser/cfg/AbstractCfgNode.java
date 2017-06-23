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
public abstract class AbstractCfgNode implements CfgNode {
	private Node astNode;

	public AbstractCfgNode(Node astNode) {
		this.astNode = astNode;
	}
	
	public Node getAstNode() {
		return astNode;
	}
	
	@Override
	public String toString() {
		return AstUtils.toString(getAstNode());
	}
}
