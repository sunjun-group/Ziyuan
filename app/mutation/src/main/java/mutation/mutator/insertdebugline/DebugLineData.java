/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package mutation.mutator.insertdebugline;

import java.util.List;

import japa.parser.ast.Node;

/**
 * @author LLT
 * 
 */
public abstract class DebugLineData {
	private int lineNo;
	private int debugLine;

	public DebugLineData(int lineNo) {
		this.lineNo = lineNo;
	}

	public abstract InsertType getInsertType() ;

	public int getDebugLine() {
		return debugLine;
	}
	
	public void setDebugLine(int debugLine) {
		this.debugLine = debugLine;
	}
	
	public int getLineNo() {
		return lineNo;
	}

	public Node getInsertNode() {
		// by default
		throw new UnsupportedOperationException();
	}
	
	public List<Node> getReplaceNodes() {
		// by default
		throw new UnsupportedOperationException();
	}
	
	public Node getOrgNode() {
		// by default
		throw new UnsupportedOperationException();
	}

	public static enum InsertType {
		ADD, REPLACE
	}

	@Override
	public String toString() {
		return "DebugLineData [lineNo=" + lineNo + ", debugLine=" + debugLine
				+ "]";
	}
	
}
