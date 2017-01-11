/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value;

import java.util.List;

import gentest.core.data.statement.RArrayAssignment;
import gentest.core.data.statement.RArrayConstructor;
import gentest.core.data.statement.RAssignment;
import gentest.core.data.statement.RConstructor;
import gentest.core.data.statement.REvaluationMethod;
import gentest.core.data.statement.Rmethod;
import gentest.core.data.statement.Statement;
import gentest.core.data.statement.StatementVisitor;

/**
 * @author LLT
 *
 */
public class StatementCloner implements StatementVisitor {
	private List<Statement> returnedStmts;
	private int varIdOffset;

	public StatementCloner(List<Statement> stmts, int varIdOffset) {
		this.returnedStmts = stmts;
		this.varIdOffset = varIdOffset;
	}

	private void transferVarIdsAndCommit(Statement stmt, Statement newStmt) {
		newStmt.setInVarIds(toNewStmtVarIds(stmt.getInVarIds()));
		newStmt.setOutVarId(toNewStmtVarId(stmt.getOutVarId()));
		returnedStmts.add(newStmt);
	}
	
	private int[] toNewStmtVarIds(int[] varIds) {
		int[] newVarIds = new int[varIds.length];
		for (int i = 0; i < varIds.length; i++) {
			newVarIds[i] = toNewStmtVarId(varIds[i]);
		}
		return newVarIds;
	}
	
	private int toNewStmtVarId(int varId) {
		if (varId != Statement.INVALID_VAR_ID) {
			return varId + varIdOffset;
		}
		return varId;
	}

	@Override
	public boolean visit(RAssignment stmt) {
		RAssignment newStmt = new RAssignment(stmt.getType(), stmt.getValue());
		transferVarIdsAndCommit(stmt, newStmt);
		return true;
	}

	@Override
	public boolean visitRmethod(Rmethod stmt) {
		Rmethod newStmt = new Rmethod(stmt.getMethod(),
				toNewStmtVarId(stmt.getReceiverVarId()));
		transferVarIdsAndCommit(stmt, newStmt);
		return true;
	}

	@Override
	public boolean visit(RConstructor stmt) {
		RConstructor newStmt = new RConstructor(stmt.getConstructor());
		transferVarIdsAndCommit(stmt, newStmt);
		return true;
	}

	@Override
	public boolean visit(REvaluationMethod stmt) {
		REvaluationMethod newStmt = new REvaluationMethod(stmt.getMethod());
		transferVarIdsAndCommit(stmt, newStmt);
		return true;
	}

	@Override
	public boolean visit(RArrayConstructor stmt) {
		RArrayConstructor newStmt = new RArrayConstructor(stmt.getSizes(),
				stmt.getOutputType(), stmt.getContentType());
		transferVarIdsAndCommit(stmt, newStmt);
		return true;
	}

	@Override
	public boolean visit(RArrayAssignment stmt) {
		RArrayAssignment newStmt = new RArrayAssignment(
				toNewStmtVarId(stmt.getArrayVarID()), stmt.getIndex(),
				toNewStmtVarId(stmt.getLocalVariableID()));
		transferVarIdsAndCommit(stmt, newStmt);
		return true;
	}

}
