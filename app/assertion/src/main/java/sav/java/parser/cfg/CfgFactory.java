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
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.BinaryExpr.Operator;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.BreakStmt;
import japa.parser.ast.stmt.CatchClause;
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
import japa.parser.ast.type.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sav.common.core.utils.CollectionUtils;
import sav.java.parser.cfg.CFG.EdgeUnCompletedType;

/**
 * @author LLT
 *
 */
public class CfgFactory extends CfgConverter {
	private static final CfgFactory INSTANCE = new CfgFactory();
	
	public CFG toCFG(Node node) {
		if (node != null) {
			node.accept(this, null);
			return getCFG();
		}
		return newInstance(node);
	}

	protected CFG convert(MethodDeclaration method) {
		CFG cfg = newInstance(method);
		cfg.addProperty(CfgProperty.PARAMETER, method.getParameters());
		cfg.addEdge(cfg.getEntry(), cfg.getExit());
		CFG body = toCFG(method.getBody());
		cfg.append(body);
		cfg.solveReturn();
		return cfg;
	}
	
	public CFG convert(AssertStmt n) {
		CFG cfg = newInstance(n);
		DecisionNode newNode = new DecisionNode(n);
		/* add new node */
		cfg.addNode(newNode);
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
	protected CFG convert(BlockStmt n) {
		List<Statement> stmts = n.getStmts();
		return convert(stmts, n);
	}

	private CFG convert(List<Statement> stmts, Node n) {
		CFG cfg = newInstance(n);
		cfg.addEdge(cfg.getEntry(), cfg.getExit());
		for (Statement stmt : CollectionUtils.nullToEmpty(stmts)) {
			cfg.append(toCFG(stmt));
		}
		return cfg;
	}
	
	@Override
	protected CFG convert(ContinueStmt n) {
		return convertBreakContinueStmt(n, EdgeUnCompletedType.CONTINUE);
	}

	private CFG convertBreakContinueStmt(Statement n, EdgeUnCompletedType type) {
		CFG cfg = newInstance(n);
		ProcessNode newNode = new ProcessNode(n);
		cfg.addNode(newNode);
		/* from entry to new node */
		cfg.addEdge(cfg.getEntry(), newNode);
		/* from new node to nowhere */
		CfgEdge edge = new CfgEdge(newNode, null);
		cfg.addUncompletedEdge(type, edge);
		return cfg;
	}

	@Override
	protected CFG convert(DoStmt n) {
		CFG cfg = newInstance(n);
		CFG body = toCFG(n.getBody());
		
		/* add edge from entry to body first nodes */
		for (CfgEdge bodyEntryOut : body.getEntryOutEdges()) {
			cfg.addEdge(cfg.getEntry(), bodyEntryOut.getDest());
		}
		
		DecisionNode decision = new DecisionNode(n.getCondition());
		cfg.addNode(decision);
		attachExecutionBlock(cfg, decision, body, decision, true);
		attachExecutionBlock(cfg, decision, cfg.getExit(), false);
		
		cfg.addProperty(CfgProperty.LOOP_DECISION_NODE, decision);
		cfg.solveBreak(null);
		cfg.solveContinue(null);
		return cfg;
	}

	@Override
	protected CFG convert(EmptyStmt n) {
		return convertProcessStmt(n);
	}

	@Override
	protected CFG convert(ExpressionStmt n) {
		return convertProcessStmt(n);
	}
	
	private CFG convertProcessStmt(Statement n) {
		CFG cfg = newInstance(n);
		ProcessNode newNode = new ProcessNode(n);
		cfg.addNode(newNode);
		cfg.addEdge(cfg.getEntry(), newNode);
		cfg.addEdge(newNode, cfg.getExit());
		return cfg;
	}

	@Override
	protected CFG convert(ForeachStmt n) {
		/* translate to a forStmt */
		ForStmt forStmt = ForeachConverter.toForStmt(n);
		return convert(forStmt);
	}

	@Override
	protected CFG convert(ForStmt n) {
		CFG cfg = newInstance(n);
		/* build and attach init nodes */
		CfgNode lastInit = addProcessNodes(cfg, n.getInit(), cfg.getEntry(), true);
		/* decision node */
		DecisionNode decision = new DecisionNode(n.getCompare());
		cfg.addNode(decision);
		cfg.addEdge(lastInit, decision);
		
		/* execution body */
		CFG body = toCFG(n.getBody());
		// add update statement
		CfgNode bodySuccessor = addProcessNodes(cfg, n.getUpdate(), decision, false);
		attachExecutionBlock(cfg, decision, body, bodySuccessor, true);
		
		/* if condition == false -> go to cfg exit */
		CfgFalseEdge falseBranch = new CfgFalseEdge(decision, cfg.getExit(), n.getCompare());
		cfg.addEdge(falseBranch);
		cfg.addProperty(CfgProperty.LOOP_DECISION_NODE, decision);
		/* solve break, continue stmts */
		cfg.solveBreak(null);
		cfg.solveContinue(null);
		return cfg;
	}

	/**
	 * add all process node, and return the first and last nodes
	 * if (join start) -> return last node
	 * if (join end) -> return first node
	 * */
	private CfgNode addProcessNodes(CFG cfg, List<Expression> exprs, CfgNode juncNode, boolean joinAtStart) {
		CfgNode lastNode = null;
		CfgNode firstNode = null;
		if (joinAtStart) {
			firstNode = lastNode = juncNode;
		}
		for (Expression expr: CollectionUtils.nullToEmpty(exprs)) {
			ProcessNode newNode = new ProcessNode(expr);
			cfg.addNode(newNode);
			if (firstNode == null) {
				firstNode = newNode;
			} else {
				cfg.addEdge(lastNode, newNode);
			}
			lastNode = newNode;
		}
		if (joinAtStart) {
			return lastNode;
		} else {
			if (firstNode == null) {
				return juncNode;
			}
			cfg.addEdge(lastNode, juncNode);
			return firstNode;
		}
	}
	
	private void attachExecutionBlock(CFG cfg, DecisionNode decision,
			CFG thenBlk, CfgNode thenSuccessor, boolean passCond) {
		attachExecutionBlock(cfg, decision, thenBlk,
				CollectionUtils.listOf(thenSuccessor, 1), passCond);
	}
	
	private void attachExecutionBlock(CFG cfg, DecisionNode decision,
			CFG thenBlk, List<CfgNode> thenSuccessors, boolean passCond) {
		if (thenBlk == null || thenBlk.isEmpty()) {
			attachExecutionBlock(cfg, decision, thenSuccessors, passCond);
			return;
		}
		cfg.addCFG(thenBlk);
		for (CfgEdge bodyEntryOut : thenBlk.getEntryOutEdges()) {
			CfgBranchEdge branch = newBranchEdge(decision, bodyEntryOut.getDest(), passCond);
			cfg.addEdge(branch);
		}
		for (CfgEdge bodyExitIn : thenBlk.getExitInEdges()) {
			for (CfgNode thenSuccessor : thenSuccessors) {
				cfg.addEdge(bodyExitIn.clone(thenSuccessor));
			}
		}
	}
	
	private void attachExecutionBlock(CFG cfg, DecisionNode decision,
			CfgNode thenSuccessor, boolean passCond) {
		CfgBranchEdge branch = newBranchEdge(decision, thenSuccessor,
				passCond);
		cfg.addEdge(branch);
	}

	private void attachExecutionBlock(CFG cfg, DecisionNode decision,
			List<CfgNode> thenSuccessors, boolean passCond) {
		for (CfgNode thenSuccessor : thenSuccessors) {
			attachExecutionBlock(cfg, decision, thenSuccessor, passCond);
		}
	}

	private CfgBranchEdge newBranchEdge(DecisionNode decision, CfgNode dest,
			boolean passCond) {
		Expression cond = (Expression) decision.getAstNode();
		if (passCond) {
			return new CfgTrueEdge(decision, dest, cond);
		} else {
			return new CfgFalseEdge(decision, dest, cond);
		}
	}

	@Override
	protected CFG convert(IfStmt n) {
		CFG cfg = newInstance(n);
		DecisionNode decision = new DecisionNode(n.getCondition());
		cfg.addNode(decision);
		cfg.addEdge(cfg.getEntry(), decision);
		CFG ifThen = toCFG(n.getThenStmt());
		CFG elseThen = toCFG(n.getElseStmt());
		attachExecutionBlock(cfg, decision, ifThen, cfg.getExit(), true);
		attachExecutionBlock(cfg, decision, elseThen, cfg.getExit(), false);
		return cfg;
	}
	
	@Override
	protected CFG convert(LabeledStmt n) {
		CFG cfg = toCFG(n.getStmt());
		cfg.solveBreak(n.getLabel());
		cfg.solveContinue(n.getLabel());
		return cfg;
	}

	@Override
	protected CFG convert(ReturnStmt n) {
		CFG cfg = newInstance(n);
		ProcessNode newNode = new ProcessNode(n);
		cfg.addNode(newNode);
		cfg.addEdge(cfg.getEntry(), newNode);
		CfgEdge edge = new CfgEdge(newNode, null);
		cfg.addUncompletedEdge(EdgeUnCompletedType.RETURN, edge);
		return cfg;
	}

	@Override
	protected CFG convert(SynchronizedStmt n) {
		return toCFG(n.getBlock());
	}

	@Override
	protected CFG convert(TryStmt n) {
		CFG cfg = newInstance(n);
		CfgNode lastNode = cfg.getEntry();
		for (Node varDecl : CollectionUtils.nullToEmpty(n.getResources())) {
			ProcessNode newNode = new ProcessNode(varDecl);
			cfg.addNode(newNode);
			cfg.addEdge(lastNode, newNode);
			lastNode = newNode;
		}
		cfg.addEdge(lastNode, cfg.getExit());
		CFG body = toCFG(n.getTryBlock());
		cfg.append(body);
		CFG finallyBlk = null;
		if (n.getFinallyBlock() != null) {
			finallyBlk = toCFG(n.getFinallyBlock());
			cfg.append(finallyBlk);
		}
		for (CatchClause catchClause : CollectionUtils.nullToEmpty(n.getCatchs())) {
			CFG catchBlk = toCFG(catchClause.getCatchBlock());
			cfg.addCFG(catchBlk);
			List<String> catchedTypes = new ArrayList<String>();
			for (Type type : catchClause.getExcept().getTypes()) {
				catchedTypes.add(type.toString());
			}
			cfg.solveError(catchedTypes, catchBlk);
			if (finallyBlk == null) {
				/* link to the cfg exit */
				for (CfgEdge edge : catchBlk.getExitInEdges()) {
					cfg.addEdge(edge.clone(cfg.getExit()));
				}
			} else {
				/* link to finally block */
				linkSubCfgsInParent(cfg, catchBlk, finallyBlk);
			}
		}
		return cfg;
	}

	private void linkSubCfgsInParent(CFG parentCfg, CFG prevCfg, CFG nextCfg) {
		for (CfgEdge thisExitIn : prevCfg.getExitInEdges()) {
			for (CfgEdge otherEntryOut : nextCfg.getEntryOutEdges()) {
				parentCfg.addEdge(thisExitIn.clone(otherEntryOut.getDest()));
			}
		}
	}

	@Override
	protected CFG convert(TypeDeclarationStmt n) {
		return convertProcessStmt(n);
	}

	@Override
	protected CFG convert(WhileStmt n) {
		CFG cfg = newInstance(n);
		CFG body = toCFG(n.getBody());
		DecisionNode decision = new DecisionNode(n.getCondition());
		cfg.addNode(decision);
		cfg.addEdge(cfg.getEntry(), decision);
		attachExecutionBlock(cfg, decision, body, decision, true);
		attachExecutionBlock(cfg, decision, cfg.getExit(), false);
		cfg.addProperty(CfgProperty.LOOP_DECISION_NODE, decision);
		cfg.solveBreak(null);
		cfg.solveContinue(null);
		return cfg;
	}

	@Override
	protected CFG convert(ExplicitConstructorInvocationStmt n) {
		return convertProcessStmt(n);
	}

	@Override
	protected CFG convert(SwitchStmt n) {
		CFG cfg = newInstance(n);
		ProcessNode switchNode = new ProcessNode(n.getSelector());
		cfg.addNode(switchNode);
		cfg.addEdge(cfg.getEntry(), switchNode);
		if (CollectionUtils.isEmpty(n.getEntries())) {
			cfg.addEdge(switchNode, cfg.getExit());
			return cfg;
		}
		/*
		 * extract all switch entries.
		 */
		List<DecisionNode> decisions = new ArrayList<DecisionNode>();
		Map<DecisionNode, CFG> entrybodies = new HashMap<DecisionNode, CFG>();
		List<CfgNode> defaultEntryOut = new ArrayList<CfgNode>();
		CFG lastEntrybody = null;
		for (SwitchEntryStmt entry : n.getEntries()) {
			CFG entrybody = convert(entry.getStmts(), entry);
			/* default */
			if (entry.getLabel() == null) {
				if (!entrybody.isEmpty()) {
					for (CfgEdge edge : entrybody.getEntryOutEdges()) {
						defaultEntryOut.add(edge.getDest());
					}
					cfg.addCFG(entrybody);
				}
			} else {
				Expression expr;
				expr = new BinaryExpr(n.getSelector(), entry.getLabel(), Operator.equals);
				AstUtils.copyNodeProperties(entry.getLabel(), expr);
				DecisionNode decision = new DecisionNode(expr);
				decisions.add(decision);
				entrybodies.put(decision, entrybody);
			}
			if (lastEntrybody != null) {
				linkSubCfgsInParent(cfg, lastEntrybody, entrybody);
			}
			lastEntrybody = entrybody;
		}
		/* if there is no default switch entry, set it to exit */
		if (defaultEntryOut.isEmpty()) {
			defaultEntryOut.add(cfg.getExit());
		}
		/*
		 * step 1: detect which case the variable is in
		 * step 2: execute all statement for the case.
		 */
		cfg.addEdge(switchNode, decisions.get(0));
		for (int i = 0; i < decisions.size(); i++) {
			DecisionNode decision = decisions.get(i);
			cfg.addNode(decision);
			CFG entrybody = entrybodies.get(decision);
			List<CfgNode> falseSuccessors = defaultEntryOut;
			if (i < decisions.size() - 1) {
				DecisionNode nextDecision = decisions.get(i + 1);
				falseSuccessors = CollectionUtils.<CfgNode>listOf(nextDecision, 1);
			} 
			attachExecutionBlock(cfg, decision, entrybody, Collections.<CfgNode>emptyList(), true);
			attachExecutionBlock(cfg, decision, falseSuccessors, false);
		}
		cfg.solveBreak(null);
		return cfg;
	}
	
	@Override
	protected CFG convert(ThrowStmt n) {
		CFG cfg = newInstance(n);
		CfgNode newNode = new ProcessNode(n);
		cfg.addNode(newNode);
		cfg.addEdge(cfg.getEntry(), newNode);		
		CfgErrorEdge edge = new CfgErrorEdge(newNode, getErrorType(n));
		cfg.addUncompletedEdge(EdgeUnCompletedType.EXCEPTION, edge);
		return cfg;
	}

	protected CFG newInstance(Node n) {
		CFG cfg = new CFG(createCfgEntryNode(), createCfgExitNode());
		cfg.addProperty(CfgProperty.AST_NODE, n);
		return cfg;
	}
	
	private CfgExitNode createCfgExitNode() {
		return new CfgExitNode();
	}

	private CfgEntryNode createCfgEntryNode() {
		return new CfgEntryNode();
	}

	private String getErrorType(ThrowStmt n) {
		Expression expr = n.getExpr();
		if (expr instanceof ObjectCreationExpr) {
			ObjectCreationExpr oce = (ObjectCreationExpr) expr;
			return oce.getType().toString();
		}
		return expr.toString();
	}
	
	public static CFG createCFG(Node node) {
		return getInstance().toCFG(node);
	}
	
	public static CfgFactory getInstance() {
		return INSTANCE;
	}
}
