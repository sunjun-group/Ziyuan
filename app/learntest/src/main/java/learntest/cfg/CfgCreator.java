package learntest.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sav.common.core.utils.CollectionUtils;
import learntest.cfg.ForeachConverter;
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
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.BinaryExpr.Operator;
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

public class CfgCreator extends CfgConverter {

	private static final CfgCreator INSTANCE = new CfgCreator();
	private List<CfgDecisionNode> temporaryDecisionNodeList = new ArrayList<CfgDecisionNode>();
	private boolean hasBreakStmt = false;
	private boolean hasContinueStmt = false;
	private CfgDecisionNode breakStmtToParentStmt;
	private CfgDecisionNode continueStmtToParentStmt;
	private Map<String, Integer> decisionNodeMap = new HashMap<String, Integer>();
	private Map<String, CfgEdge> cfgEdgeMap = new HashMap<String, CfgEdge>();
	private int decisionNodeIndex = 0;
	private List<CfgDecisionNode> temporaryBreakNodeList = new ArrayList<CfgDecisionNode>();
	// private List<CfgDecisionNode> temporaryContinueNodeList = new
	// ArrayList<CfgDecisionNode>();
	private List<Boolean> temporaryBreakTrueOrFalseList = new ArrayList<Boolean>();

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
		CfgDecisionNode decision = new CfgDecisionNode(n.getCondition(), "if");
		cfg.addNode(decision);
		temporaryDecisionNodeList.add(decision);
		decisionNodeMap.put(n.toString(), decisionNodeIndex++);
		cfg.addEdge(cfg.getEntry(), decision);

		CFG ifThen = toCFG(n.getThenStmt());
		if (hasBreakStmt) {
			hasBreakStmt = false;
			if (breakStmtToParentStmt != null) {
				attachExecutionBlock(cfg, decision, ifThen,
						breakStmtToParentStmt, true);
			} else {
				attachExecutionBlock(cfg, decision, ifThen, decision, true);
				temporaryBreakNodeList.add(decision);
				temporaryBreakTrueOrFalseList.add(true);
			}
		} else if (hasContinueStmt) {
			hasContinueStmt = false;
			attachExecutionBlock(cfg, decision, ifThen,
					continueStmtToParentStmt, true);
		} else {
			attachExecutionBlock(cfg, decision, ifThen, cfg.getExit(), true);
		}

		CFG elseThen = toCFG(n.getElseStmt());
		if (hasBreakStmt) {
			hasBreakStmt = false;
			if (breakStmtToParentStmt != null) {
				attachExecutionBlock(cfg, decision, elseThen,
						breakStmtToParentStmt, false);
			} else {
				attachExecutionBlock(cfg, decision, elseThen, decision, false);
				temporaryBreakNodeList.add(decision);
				temporaryBreakTrueOrFalseList.add(false);
			}

		} else if (hasContinueStmt) {
			hasContinueStmt = false;
			attachExecutionBlock(cfg, decision, elseThen,
					continueStmtToParentStmt, false);
		} else {
			attachExecutionBlock(cfg, decision, elseThen, cfg.getExit(), false);
		}

		return cfg;
	}

	@Override
	protected CFG convert(WhileStmt n) {
		CFG cfg = newInstance(n);
		CfgDecisionNode decision = new CfgDecisionNode(n.getCondition(),
				"while");
		temporaryDecisionNodeList.add(decision);
		decisionNodeMap.put(n.toString(), decisionNodeIndex++);
		cfg.addNode(decision);
		cfg.addEdge(cfg.getEntry(), decision);
		CFG body = toCFG(n.getBody());
		attachExecutionBlock(cfg, decision, body, decision, true);
		attachExecutionBlock(cfg, decision, cfg.getExit(), false);
		cfg.addProperty(CfgProperty.LOOP_DECISION_NODE, decision);

		dealWithBreakStmt(n, cfg);
		return cfg;
	}

	@Override
	protected CFG convert(ForStmt n) {
		CFG cfg = newInstance(n);
		CfgDecisionNode decision = new CfgDecisionNode(n.getCompare(), "for");
		temporaryDecisionNodeList.add(decision);
		decisionNodeMap.put(n.toString(), decisionNodeIndex++);
		cfg.addNode(decision);
		cfg.addEdge(cfg.getEntry(), decision);
		/* execution body */
		CFG body = toCFG(n.getBody());
		attachExecutionBlock(cfg, decision, body, decision, true);
		/* if condition == false -> go to cfg exit */
		CfgFalseEdge falseBranch = new CfgFalseEdge(decision, cfg.getExit(),
				n.getCompare());
		cfg.addEdge(falseBranch);
		cfg.addProperty(CfgProperty.LOOP_DECISION_NODE, decision);

		dealWithBreakStmt(n, cfg);

		return cfg;
	}

	@Override
	protected CFG convert(ForeachStmt n) {
		/* translate to a forStmt */
		ForStmt forStmt = ForeachConverter.toForStmt(n);
		/* similar to deal with forStmt */
		CFG cfg = newInstance(n);
		CfgDecisionNode decision = new CfgDecisionNode(forStmt.getCompare(),
				"foreach");
		temporaryDecisionNodeList.add(decision);
		decisionNodeMap.put(forStmt.toString(), decisionNodeIndex++);
		cfg.addNode(decision);
		cfg.addEdge(cfg.getEntry(), decision);

		/* execution body */
		CFG body = toCFG(n.getBody());
		attachExecutionBlock(cfg, decision, body, decision, true);
		/* if condition == false -> go to cfg exit */
		CfgFalseEdge falseBranch = new CfgFalseEdge(decision, cfg.getExit(),
				forStmt.getCompare());
		cfg.addEdge(falseBranch);
		cfg.addProperty(CfgProperty.LOOP_DECISION_NODE, decision);

		dealWithBreakStmt(n, cfg);

		return cfg;
	}

	@Override
	protected CFG convert(DoStmt n) {
		CFG cfg = newInstance(n);
		CfgDecisionNode decision = new CfgDecisionNode(n.getCondition(),
				"do while");
		cfg.addNode(decision);
		temporaryDecisionNodeList.add(decision);
		decisionNodeMap.put(n.toString(), decisionNodeIndex++);

		CFG body = toCFG(n.getBody());

		/* add edge from entry to body first nodes */
		for (CfgEdge bodyEntryOut : body.getEntryOutEdges()) {
			cfg.addEdge(cfg.getEntry(), bodyEntryOut.getDest());
		}

		attachExecutionBlock(cfg, decision, body, decision, true);
		attachExecutionBlock(cfg, decision, cfg.getExit(), false);

		cfg.addProperty(CfgProperty.LOOP_DECISION_NODE, decision);

		dealWithBreakStmt(n, cfg);

		return cfg;
	}

	@Override
	protected CFG convert(SwitchStmt n) {
		CFG cfg = newInstance(n);
		/*
		 * extract all switch entries.
		 */
		List<CfgDecisionNode> decisions = new ArrayList<CfgDecisionNode>();
		List<List<Statement>> statementsList = new ArrayList<List<Statement>>();
		SwitchEntryStmt defaultEntry = null;
		for (SwitchEntryStmt entry : n.getEntries()) {
			Expression expr;
			if (entry.getLabel() != null) {
				expr = new BinaryExpr(n.getSelector(), entry.getLabel(),
						Operator.equals);
				CfgDecisionNode decision = new CfgDecisionNode(expr,
						"switch if");
				decisions.add(decision);
				statementsList.add(entry.getStmts());
				cfg.addNode(decision);
				temporaryDecisionNodeList.add(decision);
				decisionNodeMap.put(n.toString(), decisionNodeIndex++);
			} else {
				defaultEntry = entry;
			}
		}

		if (defaultEntry != null) {
			decisions
					.add(new CfgDecisionNode(n.getSelector(), "switch default"));
			statementsList.add(defaultEntry.getStmts());
			cfg.addNode(decisions.get(decisions.size() - 1));
			temporaryDecisionNodeList.add(decisions.get(decisions.size() - 1));
			decisionNodeMap.put(n.toString(), decisionNodeIndex++);
		}

		cfg.addEdge(cfg.getEntry(), decisions.get(0));
		for (int i = 0; i < decisions.size(); i++) {
			CfgDecisionNode decision = decisions.get(i);
			CFG tempCfg = null;
			for (int j = 0; j < statementsList.get(i).size(); j++) {
				CFG trueEdgeCfg = toCFG(statementsList.get(i).get(j));
				if (tempCfg == null) {
					tempCfg = trueEdgeCfg;
				} else {
					tempCfg.append(trueEdgeCfg);
				}
			}

			if ((i + 1) > (decisions.size() - 1)) {
				if(defaultEntry == null){
				attachExecutionBlock(cfg, decision, tempCfg, cfg.getExit(),
						true);
				attachExecutionBlock(cfg, decision, null, cfg.getExit(),
						false);
				}
				else{
					if(tempCfg == null){
						cfg.addEdge(decision, cfg.getExit());
					}
					else{
					attachExecutionBlock(cfg, decision, tempCfg, cfg.getExit(),
							true);
					attachExecutionBlock(cfg, decision, null, cfg.getExit(),
							false);
					CfgDecisionNode destNode = null;
					List<CfgEdge> list= new ArrayList<CfgEdge> (cfg.getOutEdges(decision));
					for(CfgEdge edge : list){
						if(edge.getDest() != cfg.getExit()){
							destNode = (CfgDecisionNode) edge.getDest();
						}
						cfg.removeEdge(edge);
					}
					cfg.addEdge(decision, destNode);
					}

				}
			} else {
				cfg.addEdge(newBranchEdge(decision, decisions.get(i + 1), false));
				if (hasBreakStmt) {
					hasBreakStmt = false;
					attachExecutionBlock(cfg, decision, tempCfg, cfg.getExit(),
							true);
				} else {
					attachExecutionBlock(cfg, decision, tempCfg,
							decisions.get(i + 1), true);
				}
			}

		}
		return cfg;
	}

	/***
	 * deal with break statement
	 */

	protected void dealWithBreakStmt(Object n, CFG cfg) {
		if (temporaryBreakNodeList.size() != 0
				&& ((Node) n).contains(temporaryBreakNodeList.get(0)
						.getAstNode().getParentNode())) {
			for (int i = 0; i < temporaryBreakNodeList.size(); i++) {
				cfg.removeEdge(cfgEdgeMap.get(temporaryBreakNodeList.get(i)
						.toString()
						+ temporaryBreakNodeList.get(i).toString()
						+ temporaryBreakTrueOrFalseList.get(i)));
				cfgEdgeMap.remove(temporaryBreakNodeList.get(i).toString()
						+ temporaryBreakNodeList.get(i).toString()
						+ temporaryBreakTrueOrFalseList.get(i));
				CfgBranchEdge branch = newBranchEdge(
						temporaryBreakNodeList.get(i), cfg.getExit(),
						temporaryBreakTrueOrFalseList.get(i));
				cfg.addEdge(branch);
				cfgEdgeMap.put(temporaryBreakNodeList.get(i).toString()
						+ cfg.getExit().toString()
						+ temporaryBreakTrueOrFalseList.get(i), branch);
			}
		}
		temporaryBreakNodeList.removeAll(temporaryBreakNodeList);
		temporaryBreakTrueOrFalseList.removeAll(temporaryBreakTrueOrFalseList);
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
		hasBreakStmt = true;
		Node node = null;
		int count = 0;
		int index = 0;
		boolean flag = true;// used to mark the first while(for , for each
							// statement)
		breakStmtToParentStmt = null;
		// make sure the index
		if (n.getParentNode() != null) {
			node = (Node) n;
			while (node.getParentNode() != null) {
				node = node.getParentNode();
				if ((node.getClass().getTypeName().toString())
						.equals("japa.parser.ast.stmt.WhileStmt")
						|| (node.getClass().getTypeName().toString())
								.equals("japa.parser.ast.stmt.ForStmt")
						|| (node.getClass().getTypeName().toString())
								.equals("japa.parser.ast.stmt.ForeachStmt")
						|| (node.getClass().getTypeName().toString())
								.equals("japa.parser.ast.stmt.DoStmt")) {
					if (flag) {
						index = decisionNodeMap.get(node.toString());
						flag = false;
					}
					count++;
					if (count == 2) {
						breakStmtToParentStmt = temporaryDecisionNodeList
								.get(index);
						count = 0;
						break;
					}
					index--;
				} else if ((node.getClass().getTypeName().toString())
						.equals("japa.parser.ast.stmt.IfStmt")) {
					if (!flag) {
						index--;
					}

				} else if ((node.getClass().getTypeName().toString())
						.equals("japa.parser.ast.stmt.SwitchStmt")) {
					if (flag) {
						break;
					}
				}

			}
		}
		return convertProcessStmt(n);
	}

	@Override
	protected CFG convert(ContinueStmt n) {
		hasContinueStmt = true;
		Node node = null;
		int index = 0;
		continueStmtToParentStmt = null;
		// make sure the index
		if (n.getParentNode() != null) {
			node = (Node) n;
			while (node.getParentNode() != null) {
				node = node.getParentNode();
				if ((node.getClass().getTypeName().toString())
						.equals("japa.parser.ast.stmt.WhileStmt")
						|| (node.getClass().getTypeName().toString())
								.equals("japa.parser.ast.stmt.ForStmt")
						|| (node.getClass().getTypeName().toString())
								.equals("japa.parser.ast.stmt.ForeachStmt")
						|| (node.getClass().getTypeName().toString())
								.equals("japa.parser.ast.stmt.DoStmt")) {
					index = decisionNodeMap.get(node.toString());
					continueStmtToParentStmt = temporaryDecisionNodeList
							.get(index);
					break;
				}
			}
		}
		return convertProcessStmt(n);
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

			CfgBranchEdge branch = newBranchEdge(decision,
					bodyEntryOut.getDest(), passCond);
			cfg.addEdge(branch);
			cfgEdgeMap.put(decision.toString()
					+ bodyEntryOut.getDest().toString() + passCond, branch);
		}
		for (CfgEdge bodyExitIn : thenBlk.getExitInEdges()) {
			for (CfgNode thenSuccessor : thenSuccessors) {
				cfg.addEdge(bodyExitIn.clone(thenSuccessor));
			}
		}
	}

	private void attachExecutionBlock(CFG cfg, CfgDecisionNode decision,
			CfgNode thenSuccessor, boolean passCond) {
		CfgBranchEdge branch = newBranchEdge(decision, thenSuccessor, passCond);
		cfg.addEdge(branch);
		cfgEdgeMap.put(decision.toString() + thenSuccessor.toString()
				+ passCond, branch);

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

}
