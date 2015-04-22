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
import sav.strategies.dto.ClassLocation;

/**
 * @author LLT
 * 
 */
public class DebugLineData {
	private ClassLocation location;
	private InsertType insertType;
	private int debugLine;
	private int offset;

	public DebugLineData(ClassLocation loc) {
		this.location = loc;
	}

	public ClassLocation getLocation() {
		return location;
	}

	public InsertType getInsertType() {
		return insertType;
	}

	public void setInsertType(InsertType insertType) {
		this.insertType = insertType;
	}

	public int getDebugLine() {
		return debugLine;
	}

	public void setDebugLine(int debugLine) {
		this.debugLine = debugLine;
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

	public int getOffset() {
		return offset;
	}

	public static enum InsertType {
		ADD, REPLACE
	}

}
