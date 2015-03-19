/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package mutanbug.mutator;

import japa.parser.ast.Node;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.stmt.BreakStmt;
import japa.parser.ast.stmt.ContinueStmt;
import japa.parser.ast.stmt.Statement;

import java.util.ArrayList;
import java.util.List;

import mutanbug.commons.utils.DefaultGenericVisitor;

/**
 * @author LLT
 *
 */
public class StatementMutator extends DefaultGenericVisitor<List<Statement>, Boolean> {
	private Mutator mutator;
	
	public StatementMutator(Mutator mutator) {
		this.mutator = mutator;
	}
	
	public List<Statement> visitNode(Node node) {
		return super.visitNode(node, true);
	}
	
	@Override
	protected List<Statement> getDefaultReturnValue() {
		return new ArrayList<Statement>();
	}
	
	protected boolean accept(Node node) {
		return mutator.needToMutate(node);
	};
	
	@Override
	public List<Statement> visit(BreakStmt n, Boolean arg) {
		return mutator.doMutate(n);
	}
	
	@Override
	public List<Statement> visit(ContinueStmt n, Boolean arg) {
		return mutator.doMutate(n);
	}
}
