/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.cfg;

import japa.parser.ast.Node;


/**
 * @author LLT
 *
 */
public class CfgExitNode implements CfgNode {

	private int beginLine;
	
	@Override
	public Type getType() {
		return Type.EXIT;
	}

	@Override
	public String toString() {
		return "exit";
	}

	@Override
	public Node getAstNode() {
		return null;
	}
	

	@Override
	public int getBeginLine() {
		return beginLine;
	}
	
	public void setStartLine(int startLine) {
		this.beginLine = startLine;
	}

	@Override
	public String getStmtType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTrueBeginLine() {
		// TODO Auto-generated method stub
		return 0;
	}
}
