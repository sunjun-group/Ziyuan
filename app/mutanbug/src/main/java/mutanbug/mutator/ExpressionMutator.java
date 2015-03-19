/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package mutanbug.mutator;

import japa.parser.ast.Node;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.CastExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;

import java.util.ArrayList;
import java.util.List;

import mutanbug.commons.utils.DefaultGenericVisitor;

/**
 * @author LLT
 *
 */
public class ExpressionMutator extends DefaultGenericVisitor<List<Expression>, Boolean> {
	private Mutator mutator;
	
	public ExpressionMutator(Mutator mutator) {
		this.mutator = mutator;
	}
	
	public List<Expression> visitNode(Node node) {
		return super.visitNode(node, true);
	}
	
	protected boolean accept(Node node) {
		return mutator.needToMutate(node);
	};
	
	@Override
	protected List<Expression> getDefaultReturnValue() {
		return new ArrayList<Expression>();
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
		return doMutateVarDecExpr(n);
	}

	/**
	 * @param n
	 * @return
	 */
	private List<Expression> doMutateVarDecExpr(VariableDeclarationExpr n) {
		// TODO Auto-generated method stub
		return null;
	}
}