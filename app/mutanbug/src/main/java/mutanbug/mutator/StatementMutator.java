/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package mutanbug.mutator;

import japa.parser.ast.stmt.BreakStmt;
import japa.parser.ast.stmt.ContinueStmt;
import japa.parser.ast.stmt.Statement;

import java.util.List;

/**
 * @author LLT
 *
 */
public class StatementMutator extends MutatorVisitor<Statement> {
	
	public StatementMutator(Mutator mutator) {
		super(mutator);
	}
	
	@Override
	public List<Statement> visit(BreakStmt n, Boolean arg) {
		return mutator.doMutate(n);
	}
	
	@Override
	public List<Statement> visit(ContinueStmt n, Boolean arg) {
		return mutator.doMutate(n);
	}
}
