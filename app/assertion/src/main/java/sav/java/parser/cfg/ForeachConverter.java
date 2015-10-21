/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.java.parser.cfg;

import static sav.java.parser.cfg.AstUtils.markNodeAsFake;
import static sav.java.parser.cfg.AstUtils.markNodesAsFake;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.Node;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.stmt.ForeachStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.visitor.CloneVisitor;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sav.common.core.SavRtException;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class ForeachConverter {
	private static Logger log = LoggerFactory.getLogger(ForeachConverter.class);
	
	public static ForStmt toForStmt(ForeachStmt n) {
		ForStmt forStmt = new ForStmt();
		/* init */
		VariableDeclarationExpr varDecl = n.getVariable();
		List<Expression> fakeIteratorInit = newIteratorInitExpr(n, varDecl);
		forStmt.setInit(fakeIteratorInit);
		/* compare */
		Expression fakeCompare = newFakeCompare(varDecl);
		forStmt.setCompare(fakeCompare);
		/* body */
		BlockStmt fakeBodyStmt = new BlockStmt();
		List<Statement> stmts = new ArrayList<Statement>();
		// variable initialization
		VariableDeclarationExpr fakeVarDeclExpr = newVarInitExpr(varDecl);
		ExpressionStmt fakeVarDecl = new ExpressionStmt(fakeVarDeclExpr);
		stmts.add(fakeVarDecl);
		if (n.getBody() instanceof BlockStmt) {
			stmts.addAll(CollectionUtils.nullToEmpty(((BlockStmt) n.getBody())
					.getStmts()));
		}
		fakeBodyStmt.setStmts(stmts);
		forStmt.setBody(fakeBodyStmt);
		
		/* mark node as fake */
		markNodesAsFake(fakeIteratorInit, n.getIterable());
		markNodeAsFake(fakeCompare, varDecl);
		markNodeAsFake(fakeBodyStmt, n.getBody());
		markNodeAsFake(fakeVarDeclExpr, varDecl);
		markNodeAsFake(fakeVarDecl, varDecl);
		return forStmt;
	}

	private static VariableDeclarationExpr newVarInitExpr(
			VariableDeclarationExpr varDecl) {
		CloneVisitor cloner = new CloneVisitor();
		VariableDeclarationExpr newExpr = (VariableDeclarationExpr) varDecl.accept(cloner, null);
		newExpr.getVars().get(0).setInit(newExpression("tempIt.next()", varDecl));
		return newExpr;
	}

	private static Expression newFakeCompare(VariableDeclarationExpr varDecl) {
		return newExpression("tempIt.hasNext()", varDecl);
	}


	private static List<Expression> newIteratorInitExpr(ForeachStmt n,
			VariableDeclarationExpr varDecl) {
		String initExpr = String.format("Iterator<%s> tempIt = %s.iterator()",
				varDecl.getType(), n.getIterable());
		List<Expression> fakeInit = CollectionUtils.listOf(newExpression(initExpr,
				varDecl));
		return fakeInit;
	}

	private static Expression newExpression(String exprStr, Node n) {
		try {
			Expression expr = JavaParser.parseExpression(exprStr);
			return expr;
		} catch (ParseException e) {
			log.error(e.getMessage());
			throw new SavRtException(e);
		}
	}
}
