/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.java.parser.cfg;

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
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.stmt.SwitchEntryStmt;
import japa.parser.ast.stmt.SwitchStmt;
import japa.parser.ast.stmt.SynchronizedStmt;
import japa.parser.ast.stmt.ThrowStmt;
import japa.parser.ast.stmt.TryStmt;
import japa.parser.ast.stmt.TypeDeclarationStmt;
import japa.parser.ast.stmt.WhileStmt;
import sav.common.core.utils.CollectionUtils;
import sav.java.parser.cfg.CFG.EdgeUnCompletedType;

/**
 * @author LLT
 *
 */
public class CfgFactory extends CfgConverter {
	
	public CFG createCFG(Node node) {
		if (node != null) {
			node.accept(this, null);
			return getCFG();
		}
		return newInstance(node);
	}

	protected CFG convert(MethodDeclaration method) {
		CFG cfg = newInstance(method);
		cfg.addProperty(CfgProperty.PARAMETER, method.getParameters());
		CFG body = createCFG(method.getBody());
		cfg.append(body);
		return cfg;
	}
	
	public CFG convert(AssertStmt n) {
		CFG cfg = newInstance(n);
		CfgNode newNode = new ProcessNode(n);
		/* add new node */
		cfg.addVertex(newNode);
		/* add link from entry to condition */
		cfg.addEdge(cfg.getEntry(), newNode);
		/* add link true branch to exit node */
		CfgTrueEdge trueBranch = new CfgTrueEdge(newNode, cfg.getExit(), 
				n.getCheck());
		cfg.addEdge(trueBranch);
		/* add link false branch to .. nowhere */
		CfgFalseEdge falseBranch = new CfgFalseEdge(newNode, null,
				n.getCheck());
		cfg.addEdge(falseBranch);
		/* add uncompleted edge */
		cfg.addUncompletedEdge(EdgeUnCompletedType.EXCEPTION, falseBranch);
		return cfg;
	}
	
	@Override
	protected CFG convert(BreakStmt n) {
		return convertBreakContinueStmt(n, EdgeUnCompletedType.BREAK);
	}

	@Override
	protected CFG convert(ContinueStmt n) {
		return convertBreakContinueStmt(n, EdgeUnCompletedType.CONTINUE);
	}

	private CFG convertBreakContinueStmt(Statement n, EdgeUnCompletedType type) {
		CFG cfg = newInstance(n);
		ProcessNode newNode = new ProcessNode(n);
		/* from entry to new node */
		cfg.addEdge(cfg.getEntry(), newNode);
		/* from new node to nowhere */
		CfgEdge edge = new CfgEdge(newNode, null);
		cfg.addUncompletedEdge(type, edge);
		return cfg;
	}

	@Override
	protected CFG convert(DoStmt n) {
		CFG cfg = createCFG(n.getBody());
		DecisionNode cond = new DecisionNode(n.getCondition());
		cfg.append(cond);
		/* if condition == true -> go to entry */
		CfgTrueEdge trueBranch = new CfgTrueEdge(cond, cfg.getEntry(), n.getCondition());
		cfg.addEdge(trueBranch);
		/* if condition == false -> go to exit */
		CfgFalseEdge falseBranch = new CfgFalseEdge(cond, cfg.getExit(), n.getCondition());
		cfg.addEdge(falseBranch);
		return cfg;
	}

	@Override
	protected CFG convert(EmptyStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(ExpressionStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(ForeachStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(ForStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(IfStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(LabeledStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(ReturnStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(SwitchEntryStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(SynchronizedStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(TryStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(TypeDeclarationStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(WhileStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(ExplicitConstructorInvocationStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(SwitchStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(ThrowStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(BlockStmt n) {
		CFG cfg = newInstance(n);
		for (Statement stmt : CollectionUtils.nullToEmpty(n.getStmts())) {
			cfg.append(createCFG(stmt));
		}
		return cfg;
	}
	
	protected CFG newInstance(Node node) {
		CFG cfg = new CFG(createCfgEntryNode(), createCfgExitNode());
		cfg.addProperty(CfgProperty.AST_NODE, node);
		return cfg;
	}
	
	private CfgExitNode createCfgExitNode() {
		return new CfgExitNode();
	}

	private CfgEntryNode createCfgEntryNode() {
		return new CfgEntryNode();
	}

}
