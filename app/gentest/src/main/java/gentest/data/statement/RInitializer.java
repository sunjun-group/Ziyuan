/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.data.statement;

/**
 * @author LLT
 *
 */
public class RInitializer extends Statement {

	public RInitializer(RStatementKind type) {
		super(type);
	}

	@Override
	public void accept(StatementVisitor visitor) throws Throwable {
		
	}

}
