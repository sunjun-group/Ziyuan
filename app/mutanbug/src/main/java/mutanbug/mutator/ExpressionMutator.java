/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package mutanbug.mutator;

import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.CastExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;

import java.util.List;

/**
 * @author LLT
 *
 */
public class ExpressionMutator extends MutatorVisitor<Expression> {
	
	public ExpressionMutator(Mutator mutator) {
		super(mutator);
	}
	
	@Override
	public List<Expression> visit(AssignExpr n, Boolean arg) {
		return mutator.doMutateAssignExpr(n);
	}
	
	@Override
	public List<Expression> visit(BinaryExpr n, Boolean arg) {
		return mutator.doMutateBinaryExpr(n);
	}
	
	@Override
	public List<Expression> visit(CastExpr n, Boolean arg) {
		return mutator.doMutateCastExpr(n);
	}
	
	@Override
	public List<Expression> visit(FieldAccessExpr n, Boolean arg) {
		return mutator.doMutateFieldAccessExpr(n);
	}
	
	@Override
	public List<Expression> visit(MethodCallExpr n, Boolean arg) {
		return mutator.doMutateMethodCallExpr(n);
	}
	
	@Override
	public List<Expression> visit(NameExpr n, Boolean arg) {
		return mutator.doMutateNameExpr(n);
	}
	
	@Override
	public List<Expression> visit(UnaryExpr n, Boolean arg) {
		return mutator.doMutateUnaryExpr(n);
	}
	
	@Override
	public List<Expression> visit(VariableDeclarationExpr n, Boolean arg) {
		return mutator.doMutateVarDecExpr(n);
	}

}