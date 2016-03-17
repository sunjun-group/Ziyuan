package learntest.cfg;


import java.util.List;














import sav.common.core.utils.CollectionUtils;

import learntest.cfg.CfgBranchEdge;
import learntest.cfg.CfgEdge;
import learntest.cfg.CfgFalseEdge;
import learntest.cfg.CfgNode;
import learntest.cfg.CfgTrueEdge;
import learntest.cfg.CfgDecisionNode;
import learntest.cfg.CFG;
import learntest.cfg.CfgEntryNode;
import learntest.cfg.CfgExitNode;
import learntest.cfg.CfgProperty;
import japa.parser.ast.Node;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.Expression;
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
import japa.parser.ast.stmt.SwitchStmt;
import japa.parser.ast.stmt.SynchronizedStmt;
import japa.parser.ast.stmt.ThrowStmt;
import japa.parser.ast.stmt.TryStmt;
import japa.parser.ast.stmt.TypeDeclarationStmt;
import japa.parser.ast.stmt.WhileStmt;

public class CfgCreator extends CfgConverter {
	
	private static final CfgCreator INSTANCE = new CfgCreator();
	
	public CFG toCFG(Node node) {
		if (node != null) {
			node.accept(this, null);
			return getCFG();
		}
		return newInstance(node);
	}
	
	private CfgEntryNode createCfgEntryNode() {
		return new CfgEntryNode();
	}
	
	private CfgExitNode createCfgExitNode() {
		return new CfgExitNode();
	}

	
	protected CFG newInstance(Node n) {
		CFG cfg = new CFG(createCfgEntryNode(), createCfgExitNode());
		cfg.addProperty(CfgProperty.AST_NODE, n);
		return cfg;
	}
	
	public static CFG createCFG(Node node) {
		return getInstance().toCFG(node);
	}
	
	private static CfgCreator getInstance() {
		return INSTANCE;
	}

	@Override
	protected CFG convert(IfStmt n) {
		CFG cfg = newInstance(n);
		CfgDecisionNode decision = new CfgDecisionNode(n.getCondition());
		cfg.addNode(decision);
		cfg.addEdge(cfg.getEntry(), decision);
		CFG ifThen = toCFG(n.getThenStmt());
		CFG elseThen = toCFG(n.getElseStmt());
		attachExecutionBlock(cfg, decision, ifThen, cfg.getExit(), true);
		attachExecutionBlock(cfg, decision, elseThen, cfg.getExit(), false);
		return cfg;
	}
	
	protected CFG convert(WhileStmt n) {
		CFG cfg = newInstance(n);
		CFG body = toCFG(n.getBody());
		CfgDecisionNode decision = new CfgDecisionNode(n.getCondition());
		cfg.addNode(decision);
		cfg.addEdge(cfg.getEntry(), decision);
		attachExecutionBlock(cfg, decision, body, decision, true);
		attachExecutionBlock(cfg, decision, cfg.getExit(), false);
		cfg.addProperty(CfgProperty.LOOP_DECISION_NODE, decision);
		//cfg.solveBreak(null);
		//cfg.solveContinue(null);
		return cfg;
	}
	
	
	/****
	 * deal with statements that do not contain decision condition 
	 */
	@Override
	protected CFG convert(TypeDeclarationStmt n) {
		return convertProcessStmt(n);
	}
	
	@Override
	protected CFG convert(ReturnStmt n) {
		return convertProcessStmt(n);
	}
	
	@Override
	protected CFG convert(BreakStmt n) {
		return  convertProcessStmt(n);
		
	}
	
	@Override
	protected CFG convert(ContinueStmt n) {
		return  convertProcessStmt(n);
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
	protected CFG convert(EmptyStmt n) {
		return convertProcessStmt(n);
	}

	@Override
	protected CFG convert(ExpressionStmt n) {
		return convertProcessStmt(n);
	}
	
	private CFG convertProcessStmt(Statement n) {
		CFG cfg = newInstance(n);
		cfg.addEdge(cfg.getEntry(), cfg.getExit());
		return cfg;
	}
	
	
	/***
	 * attachExcutionBlock
	 */
	private void attachExecutionBlock(CFG cfg, CfgDecisionNode decision,
			CFG thenBlk, CfgNode thenSuccessor, boolean passCond) {
		attachExecutionBlock(cfg, decision, thenBlk,
				CollectionUtils.listOf(thenSuccessor, 1), passCond);
	}
	
	private void attachExecutionBlock(CFG cfg, CfgDecisionNode decision,
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
	
	private void attachExecutionBlock(CFG cfg, CfgDecisionNode decision,
			CfgNode thenSuccessor, boolean passCond) {
		CfgBranchEdge branch = newBranchEdge(decision, thenSuccessor,
				passCond);
		cfg.addEdge(branch);
	}

	private void attachExecutionBlock(CFG cfg, CfgDecisionNode decision,
			List<CfgNode> thenSuccessors, boolean passCond) {
		for (CfgNode thenSuccessor : thenSuccessors) {
			attachExecutionBlock(cfg, decision, thenSuccessor, passCond);
		}
	}
	
	private CfgBranchEdge newBranchEdge(CfgDecisionNode decision, CfgNode dest,
			boolean passCond) {
		Expression cond = (Expression) decision.getAstNode();
		if (passCond) {
			return new CfgTrueEdge(decision, dest, cond);
		} else {
			return new CfgFalseEdge(decision, dest, cond);
		}
	}
	
	@Override
	protected CFG convert(MethodDeclaration n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(AssertStmt n) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected CFG convert(DoStmt n) {
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
	protected CFG convert(LabeledStmt n) {
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
	protected CFG convert(ExplicitConstructorInvocationStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	protected CFG convert(ThrowStmt n) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected CFG convert(SwitchStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

}
