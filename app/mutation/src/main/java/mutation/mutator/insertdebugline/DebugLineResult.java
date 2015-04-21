/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package mutation.mutator.insertdebugline;

import sav.strategies.dto.ClassLocation;

/**
 * @author LLT
 * 
 */
public class DebugLineResult {
	private ClassLocation location;
	private InsertType insertType;

	public DebugLineResult(ClassLocation loc) {
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



	public static enum InsertType {
		ADD, REPLACE
	}

}
