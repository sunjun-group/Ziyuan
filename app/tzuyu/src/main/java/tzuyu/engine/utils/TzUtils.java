/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.utils;

import tzuyu.engine.model.Statement;
import tzuyu.engine.model.StatementKind;
import tzuyu.engine.model.Variable;

/**
 * @author LLT
 *
 */
public class TzUtils {
	private TzUtils() {}
	
	public static StatementKind getFirstDeclareStmt(Variable var) {
		Statement stmt = var.getDeclaringStatement();
		StatementKind stmtKind = stmt.getAction().getAction();
		if (!var.getVarIndex().isValueOfLastStmt()) {
			return getFirstDeclareStmt(var.getOwner().getInputs(var.getStmtIdx())
					.get(var.getArgIdx()));
		}
		return stmtKind;
	}
}
