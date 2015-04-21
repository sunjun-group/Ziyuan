/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package mutation.mutator.insertdebugline;

import japa.parser.ast.Node;
import sav.strategies.dto.ClassLocation;

/**
 * @author LLT
 *
 */
public class AddedLineData extends DebugLineResult {
	private Node insertStmt;
	
	public AddedLineData(ClassLocation loc, Node insertStmt) {
		super(loc);
		this.insertStmt = insertStmt;
		setInsertType(InsertType.ADD);
	}
	
}
