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
public class CfgEntryNode implements CfgNode {

	@Override
	public Type getType() {
		return Type.ENTRY;
	}

	@Override
	public String toString() {
		return "entry";
	}

	@Override
	public Node getAstNode() {
		return null;
	}
}
