/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.junit;

import java.util.List;

import tzuyu.engine.TzConfiguration;
import tzuyu.engine.model.Statement;
import tzuyu.engine.model.StatementKind;
import tzuyu.engine.model.Variable;
import tzuyu.engine.runtime.RArrayDeclaration;
import tzuyu.engine.runtime.RAssignment;
import tzuyu.engine.runtime.RConstructor;
import tzuyu.engine.runtime.RMethod;

/**
 * @author LLT
 *
 */
public class StmtJWriterFactory {
	private VariableRenamer renamer;
	private TzConfiguration config;
	
	public StmtJWriterFactory(TzConfiguration config, VariableRenamer renamer) {
		this.config = config;
		this.renamer = renamer;
	}
	
	public AbstractStmtJWriter getStmtJWriter(Statement stmt, Variable newVar,
			List<Variable> inputVars) {
		StatementKind statement = stmt.getAction().getAction();
		if (statement instanceof RAssignment) {
			if (config.isLongFormat()) {
				return createRAssgnmentWriter((RAssignment) statement,
						newVar);
			}
		} else if (statement instanceof RMethod) {
			return createRMethodWriter((RMethod) statement, newVar, inputVars);
		} else if (statement instanceof RConstructor) {
			return createRConstructorWriter((RConstructor) statement, newVar,
					inputVars);
		} else if (statement instanceof RArrayDeclaration) {
			return createRArrayDeclaration((RArrayDeclaration)statement, newVar, inputVars);
		} else {
			throw new Error("Wrong type of statement: " + statement);
		}
		return null;
	}
	
	public RAssignmentJWriter createRAssgnmentWriter(RAssignment stmt, Variable newVar) {
		return new RAssignmentJWriter(config, renamer, stmt, newVar);
	}
	
	public RMethodJWriter createRMethodWriter(RMethod rmethod, Variable newVar,
			List<Variable> inputVars) {
		return new RMethodJWriter(config, renamer, rmethod, newVar, inputVars);
	}
	
	public RConstructJWriter createRConstructorWriter(RConstructor ctor,
			Variable newVar, List<Variable> inputVars) {
		return new RConstructJWriter(config, renamer, ctor, newVar, inputVars);
	}
	
	public RArrayDeclarationJWriter createRArrayDeclaration(
			RArrayDeclaration rArrayDeclaration, Variable newVar,
			List<Variable> inputVars) {
		return new RArrayDeclarationJWriter(config, renamer, rArrayDeclaration,
				newVar, inputVars);
	}
}
