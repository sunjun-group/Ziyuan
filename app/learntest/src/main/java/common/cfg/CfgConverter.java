/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package common.cfg;

import japa.parser.ast.Node;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.BreakStmt;
import japa.parser.ast.stmt.ContinueStmt;
import japa.parser.ast.stmt.DoStmt;
import japa.parser.ast.stmt.EmptyStmt;
import japa.parser.ast.stmt.ExplicitConstructorInvocationStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.stmt.ForeachStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.LabeledStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.SwitchStmt;
import japa.parser.ast.stmt.SynchronizedStmt;
import japa.parser.ast.stmt.ThrowStmt;
import japa.parser.ast.stmt.TryStmt;
import japa.parser.ast.stmt.TypeDeclarationStmt;
import japa.parser.ast.stmt.WhileStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;

/**
 * @author LLT
 * 
 */
public abstract class CfgConverter extends VoidVisitorAdapter<Object> {
	private CFG result;

	protected abstract CFG convert(MethodDeclaration n);

	protected abstract CFG convert(AssertStmt n);

	protected abstract CFG convert(BlockStmt n);

	protected abstract CFG convert(BreakStmt n);

	protected abstract CFG convert(ContinueStmt n);

	protected abstract CFG convert(DoStmt n);

	protected abstract CFG convert(EmptyStmt n);

	protected abstract CFG convert(ExpressionStmt n);

	protected abstract CFG convert(ForeachStmt n);

	protected abstract CFG convert(ForStmt n);

	protected abstract CFG convert(IfStmt n);

	protected abstract CFG convert(LabeledStmt n);

	protected abstract CFG convert(ReturnStmt n);

	protected abstract CFG convert(SynchronizedStmt n);

	protected abstract CFG convert(TryStmt n);

	protected abstract CFG convert(TypeDeclarationStmt n);

	protected abstract CFG convert(WhileStmt n);

	protected abstract CFG convert(ExplicitConstructorInvocationStmt n);

	protected abstract CFG convert(SwitchStmt n);

	protected abstract CFG convert(ThrowStmt n);

	protected abstract CFG newInstance(Node n);

	@Override
	public void visit(MethodDeclaration n, Object arg) {
		result = convert(n);
	}

	@Override
	public void visit(AssertStmt n, Object arg) {
		result = convert(n);
	}

	@Override
	public void visit(BlockStmt n, Object arg) {
		result = convert(n);
	}

	@Override
	public void visit(BreakStmt n, Object arg) {
		result = convert(n);
	}

	@Override
	public void visit(ContinueStmt n, Object arg) {
		result = convert(n);
	}

	@Override
	public void visit(DoStmt n, Object arg) {
		result = convert(n);
	}

	@Override
	public void visit(EmptyStmt n, Object arg) {
		result = convert(n);
	}

	@Override
	public void visit(ExpressionStmt n, Object arg) {
		result = convert(n);
	}

	@Override
	public void visit(ForeachStmt n, Object arg) {
		result = convert(n);
	}

	@Override
	public void visit(ForStmt n, Object arg) {
		result = convert(n);
	}

	@Override
	public void visit(IfStmt n, Object arg) {
		result = convert(n);
	}

	@Override
	public void visit(LabeledStmt n, Object arg) {
		result = convert(n);
	}

	@Override
	public void visit(ReturnStmt n, Object arg) {
		result = convert(n);
	}

	@Override
	public void visit(SynchronizedStmt n, Object arg) {
		super.visit(n, arg);
	}

	@Override
	public void visit(TryStmt n, Object arg) {
		result = convert(n);
	}

	@Override
	public void visit(TypeDeclarationStmt n, Object arg) {
		result = convert(n);
	}

	@Override
	public void visit(WhileStmt n, Object arg) {
		result = convert(n);
	}

	@Override
	public void visit(ExplicitConstructorInvocationStmt n, Object arg) {
		result = convert(n);
	}

	@Override
	public void visit(SwitchStmt n, Object arg) {
		result = convert(n);
	}

	@Override
	public void visit(ThrowStmt n, Object arg) {
		result = convert(n);
	}

	public CFG getCFG() {
		return result;
	}
}
