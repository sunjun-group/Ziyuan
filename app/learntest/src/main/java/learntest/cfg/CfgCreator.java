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
	private List<CfgEdge> temporaryReturnEdgeList = new ArrayList<CfgEdge>();
	private boolean hasBreakStmt = false;
	private boolean hasBreakNotToExitStmt = false;;
	private boolean hasContinueStmt = false;
	private boolean hasReturnStmt = false;
	private CfgDecisionNode breakStmtToParentStmt;
	private CfgDecisionNode continueStmtToParentStmt;
	private Map<String, Integer> decisionNodeMap = new HashMap<String, Integer>();
	private Map<String, CfgEdge> cfgEdgeMap = new HashMap<String, CfgEdge>();
	private int decisionNodeIndex = 0;
	private List<CfgDecisionNode> temporaryBreakNodeList = new ArrayList<CfgDecisionNode>();
	private List<CfgDecisionNode> temporaryBreakNodeNotToExitList = new ArrayList<CfgDecisionNode>();
	private List<Boolean> temporaryBreakTrueOrFalseList = new ArrayList<Boolean>();
	private List<Boolean> temporaryBreakTrueOrFalseNotToExitList = new ArrayList<Boolean>();

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
		CfgDecisionNode decision = new CfgDecisionNode(n.getCondition(), "if",
				false);
		cfg.addNode(decision);
		temporaryDecisionNodeList.add(decision);
		decisionNodeMap.put(n.toString(), decisionNodeIndex++);
		cfg.addEdge(cfg.getEntry(), decision);
		
		setTrueBeginLine( n ,   decision);
		
		CFG ifThen = toCFG(n.getThenStmt());
		if (hasBreakStmt) {
			hasBreakStmt = false;
			if (breakStmtToParentStmt != null) {
				attachExecutionBlock(cfg, decision, ifThen,
						breakStmtToParentStmt, true);
			} else {
				attachExecutionBlock(cfg, decision, ifThen, decision, true);
				if (hasBreakNotToExitStmt) {
					hasBreakNotToExitStmt = false;
					temporaryBreakNodeNotToExitList.add(decision);
					temporaryBreakTrueOrFalseNotToExitList.add(true);
				} else {
					temporaryBreakNodeList.add(decision);
					temporaryBreakTrueOrFalseList.add(true);
				}
			}

		} else if (hasContinueStmt) {
			hasContinueStmt = false;
			attachExecutionBlock(cfg, decision, ifThen,
					continueStmtToParentStmt, true);
		} else if (hasReturnStmt) {
			hasReturnStmt = false;
			attachExecutionBlock(cfg, decision, ifThen, decision, true);
			temporaryReturnEdgeList.add(cfgEdgeMap.get(decision.toString()
					+ decision.toString() + true));

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
				if (hasBreakNotToExitStmt) {
					hasBreakNotToExitStmt = false;
					temporaryBreakNodeNotToExitList.add(decision);
					temporaryBreakTrueOrFalseNotToExitList.add(false);
				} else {
					temporaryBreakNodeList.add(decision);
					temporaryBreakTrueOrFalseList.add(false);
				}
			}

		} else if (hasContinueStmt) {
			hasContinueStmt = false;
			attachExecutionBlock(cfg, decision, elseThen,
					continueStmtToParentStmt, false);
		} else if (hasReturnStmt) {
			hasReturnStmt = false;
			attachExecutionBlock(cfg, decision, elseThen, decision, false);
			temporaryReturnEdgeList.add(cfgEdgeMap.get(decision.toString()
					+ decision.toString() + false));
		} else {
			attachExecutionBlock(cfg, decision, elseThen, cfg.getExit(), false);
		}

		return cfg;
	}

	@Override
	protected CFG convert(WhileStmt n) {
		CFG cfg = newInstance(n);
		CfgDecisionNode decision = new CfgDecisionNode(n.getCondition(),
				"while", true);
		temporaryDecisionNodeList.add(decision);
		decisionNodeMap.put(n.toString(), decisionNodeIndex++);
		cfg.addNode(decision);
		cfg.addEdge(cfg.getEntry(), decision);
		setTrueBeginLine( n ,   decision);
		dealParentLoopLine(n,  decision);
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
		CfgDecisionNode decision = new CfgDecisionNode(n.getCompare(), "for",
				true);
		temporaryDecisionNodeList.add(decision);
		decisionNodeMap.put(n.toString(), decisionNodeIndex++);
		cfg.addNode(decision);
		cfg.addEdge(cfg.getEntry(), decision);
		setTrueBeginLine( n ,   decision);
		dealParentLoopLine(n,  decision);
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
				"foreach", true);
		temporaryDecisionNodeList.add(decision);
		decisionNodeMap.put(forStmt.toString(), decisionNodeIndex++);
		cfg.addNode(decision);
		cfg.addEdge(cfg.getEntry(), decision);
		setTrueBeginLine( n ,   decision);
		dealParentLoopLine(n,  decision);
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
				"do while", true);
		cfg.addNode(decision);
		temporaryDecisionNodeList.add(decision);
		decisionNodeMap.put(n.toString(), decisionNodeIndex++);
		setTrueBeginLine( n ,   decision);
		dealParentLoopLine(n,  decision);

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
		List<Integer> array = new ArrayList<Integer>();
		List<List<CfgDecisionNode>> destList = new ArrayList<List<CfgDecisionNode>>();
		List<List<Boolean>> branchFlagList = new ArrayList<List<Boolean>>();
		for (SwitchEntryStmt entry : n.getEntries()) {
			Expression expr;
			if (entry.getLabel() != null) {
				expr = new BinaryExpr(n.getSelector(), entry.getLabel(),
						Operator.equals);
				CfgDecisionNode decision = new CfgDecisionNode(expr,
						"switch if", false);
				decisions.add(decision);
				statementsList.add(entry.getStmts());
				cfg.addNode(decision);
				decision.setBeginLine(entry.getBeginLine());
				temporaryDecisionNodeList.add(decision);
				decisionNodeMap.put(n.toString(), decisionNodeIndex++);
				setTrueBeginLine( entry ,  decision);
			} else {
				defaultEntry = entry;
			}
		}

		if (defaultEntry != null) {
			decisions.add(new CfgDecisionNode(n.getSelector(),
					"switch default", false));
			statementsList.add(defaultEntry.getStmts());
			cfg.addNode(decisions.get(decisions.size() - 1));
			temporaryDecisionNodeList.add(decisions.get(decisions.size() - 1));
			decisionNodeMap.put(n.toString(), decisionNodeIndex++);
			decisions.get(decisions.size() - 1).setBeginLine(defaultEntry.getBeginLine());
		    setTrueBeginLine( defaultEntry ,  decisions.get(decisions.size() - 1));
		}

		cfg.addEdge(cfg.getEntry(), decisions.get(0));
		for (int i = 0; i < decisions.size(); i++) {
			CfgDecisionNode decision = decisions.get(i);
			CFG tempCfg = null;
			if (statementsList.get(i) != null) {
				for (int j = 0; j < statementsList.get(i).size(); j++) {
					CFG trueEdgeCfg = toCFG(statementsList.get(i).get(j));
					if (tempCfg == null) {
						tempCfg = trueEdgeCfg;
					} else {
						tempCfg.append(trueEdgeCfg);
					}
				}
			} else {
				tempCfg = new CFG(createCfgEntryNode(), createCfgExitNode());
			}
			if ((i + 1) > (decisions.size() - 1)) {
				if (defaultEntry == null) {
					if (hasReturnStmt) {
						dealWithReturnStmt(cfg, tempCfg, decision);

						attachExecutionBlock(cfg, decision, null,
								cfg.getExit(), false);
					} else {
						attachExecutionBlock(cfg, decision, tempCfg,
								cfg.getExit(), true);
						attachExecutionBlock(cfg, decision, null,
								cfg.getExit(), false);
					}

				} else {
					if (tempCfg.getVertices().isEmpty()) {
						if (hasReturnStmt) {
							hasReturnStmt = false;
							attachExecutionBlock(cfg, decision, decision, true);
							temporaryReturnEdgeList.add(cfgEdgeMap.get(decision
									.toString() + decision.toString() + true));
						} else {
							cfg.addEdge(decision, cfg.getExit());
						}
					} else {
						if (hasReturnStmt) {
							dealWithReturnStmt(cfg, tempCfg, decision);

							attachExecutionBlock(cfg, decision, null,
									cfg.getExit(), false);
						} else {
							attachExecutionBlock(cfg, decision, tempCfg,
									cfg.getExit(), true);
							attachExecutionBlock(cfg, decision, null,
									cfg.getExit(), false);
						}
						CfgDecisionNode destNode = null;
						List<CfgEdge> list = new ArrayList<CfgEdge>(
								cfg.getOutEdges(decision));
						for (CfgEdge edge : list) {
							if (edge.getDest() != cfg.getExit()) {
								destNode = (CfgDecisionNode) edge.getDest();
							}
							cfg.removeEdge(edge);
						}
						if (destNode != null) {
							cfg.addEdge(decision, destNode);
						} else {
							cfg.addEdge(decision, cfg.getExit());
						}
					}

				}
			} else {
				cfg.addEdge(newBranchEdge(decision, decisions.get(i + 1), false));
				if (hasBreakStmt) {
					hasBreakStmt = false;
					attachExecutionBlock(cfg, decision, tempCfg, cfg.getExit(),
							true);
				} else if (hasReturnStmt) {
					dealWithReturnStmt(cfg, tempCfg, decision);
				} else {

					List<CfgEdge> inExitList = new ArrayList<CfgEdge>(
							tempCfg.getInEdges(tempCfg.getExit()));
					List<CfgDecisionNode> destNodeList = new ArrayList<CfgDecisionNode>();
					List<Boolean> branchFlag = new ArrayList<Boolean>();
					for (CfgEdge edge : inExitList) {
						if (edge.getSource() != tempCfg.getEntry()) {
							destNodeList
									.add((CfgDecisionNode) edge.getSource());
						}
						if (edge.getClass().getSimpleName()
								.equals("CfgFalseEdge")) {
							branchFlag.add(false);
						} else {
							branchFlag.add(true);
						}
					}

					destList.add(destNodeList);
					branchFlagList.add(branchFlag);
					attachExecutionBlock(cfg, decision, tempCfg,
							decisions.get(i + 1), true);
					List<CfgEdge> inExitList2 = new ArrayList<CfgEdge>(
							cfg.getInEdges(decisions.get(i + 1)));
					array.add(i + 1);
					for (CfgEdge edge : inExitList2) {
						if (destNodeList.contains(edge.getSource())) {
							cfg.removeEdge(edge);
						}
					}

				}
			}
		}

		for (int index = 0; index < array.size(); index++) {
			List<CfgEdge> outDecisionList2 = new ArrayList<CfgEdge>(
					cfg.getOutEdges(decisions.get(array.get(index))));
			for (CfgEdge edge : outDecisionList2) {
				if (edge.getClass().getSimpleName().equals("CfgTrueEdge")) {
					CfgDecisionNode destNode = (CfgDecisionNode) edge.getDest();
					for (int j = 0; j < destList.get(index).size(); j++) {
						cfg.addEdge(newBranchEdge(destList.get(index).get(j),
								destNode, branchFlagList.get(index).get(j)));
					}
					break;
				}
			}
		}
		
		//remove default node
		cfg.remove(decisions.get(decisions.size() - 1));
		
		return cfg;
	}

	/***
	 * deal with break statement
	 */

	protected void dealWithBreakStmt(Object n, CFG cfg) {
		// deal with break that not lead to exit
		if (temporaryBreakNodeNotToExitList.size() != 0
				&& ((Node) n).contains(temporaryBreakNodeNotToExitList.get(0)
						.getAstNode().getParentNode())) {
			for (int i = 0; i < temporaryBreakNodeNotToExitList.size(); i++) {
				cfg.removeEdge(cfgEdgeMap.get(temporaryBreakNodeNotToExitList
						.get(i).toString()
						+ temporaryBreakNodeNotToExitList.get(i).toString()
						+ temporaryBreakTrueOrFalseNotToExitList.get(i)));
				cfgEdgeMap.remove(temporaryBreakNodeNotToExitList.get(i)
						.toString()
						+ temporaryBreakNodeNotToExitList.get(i).toString()
						+ temporaryBreakTrueOrFalseNotToExitList.get(i));
				CfgBranchEdge branch = newBranchEdge(
						temporaryBreakNodeNotToExitList.get(i), cfg.getExit(),
						temporaryBreakTrueOrFalseNotToExitList.get(i));
				cfg.addEdge(branch);
				cfgEdgeMap
						.put(temporaryBreakNodeNotToExitList.get(i).toString()
								+ cfg.getExit().toString()
								+ temporaryBreakTrueOrFalseNotToExitList.get(i),
								branch);
			}
		}
		temporaryBreakNodeNotToExitList
				.removeAll(temporaryBreakNodeNotToExitList);
		temporaryBreakTrueOrFalseNotToExitList
				.removeAll(temporaryBreakTrueOrFalseNotToExitList);
	}

	protected CFG dealWithBreakStmt(CFG cfg) {
		if (temporaryBreakNodeList.size() != 0) {
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

		return cfg;
	}

	/****
	 * deal with returnStmt
	 */
	protected void dealWithReturnStmt(CFG cfg, CFG tempCfg,
			CfgDecisionNode decision) {
		hasReturnStmt = false;
		if (!tempCfg.getVertices().isEmpty()) {
			List<CfgEdge> returnEdgeList = new ArrayList<CfgEdge>(
					tempCfg.getExitInEdges());
			for (CfgEdge edge : returnEdgeList) {
				tempCfg.removeEdge(edge);
				if (edge.getType().toString().equals("TRUE")) {
					attachExecutionBlock(tempCfg,
							(CfgDecisionNode) edge.getSource(),
							(CfgDecisionNode) edge.getSource(), true);
					temporaryReturnEdgeList.add(cfgEdgeMap.get(edge.getSource()
							.toString() + edge.getSource().toString() + true));
				} else {
					attachExecutionBlock(tempCfg,
							(CfgDecisionNode) edge.getSource(),
							(CfgDecisionNode) edge.getSource(), false);
					temporaryReturnEdgeList.add(cfgEdgeMap.get(edge.getSource()
							.toString() + edge.getSource().toString() + false));
				}

			}
			attachExecutionBlock(cfg, decision, tempCfg, cfg.getExit(), true);
		} else {
			attachExecutionBlock(cfg, decision, null, decision, true);
			temporaryReturnEdgeList.add(cfgEdgeMap.get(decision.toString()
					+ decision.toString() + true));
		}
	}

	protected CFG dealWithReturnStmt(CFG cfg) {
		if (temporaryReturnEdgeList.size() != 0) {
			for (CfgEdge edge : temporaryReturnEdgeList) {
				cfg.removeEdge(edge);
				if (!edge.getSource().toString().equals("default")) {
					if (edge.getType().toString().equals("TRUE")) {
						cfg.addEdge(newBranchEdge(
								(CfgDecisionNode) edge.getSource(),
								cfg.getExit(), true));
						// System.out.println(edge);
					} else {
						cfg.addEdge(newBranchEdge(
								(CfgDecisionNode) edge.getSource(),
								cfg.getExit(), false));
						// System.out.println(edge);
					}
				} else {
					cfg.addEdge(edge.getSource(), cfg.getExit());
				}
			}
			temporaryReturnEdgeList.removeAll(temporaryReturnEdgeList);
		}

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
		hasReturnStmt = true;
		return convertProcessStmt(n);
	}

	@Override
	protected CFG convert(BreakStmt n) {
		// System.out.println(n.getParentNode().getParentNode().getParentNode().getParentNode().getParentNode().getParentNode());
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
					hasBreakNotToExitStmt = true;
					break;
				}
			}

		}
		// System.out.println(breakStmtToParentStmt);
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
	protected CFG convert(MethodDeclaration method) {
		CFG cfg = newInstance(method);
		cfg.addProperty(CfgProperty.PARAMETER, method.getParameters());
		cfg.addEdge(cfg.getEntry(), cfg.getExit());
		CFG body = toCFG(method.getBody());
		cfg.append(body);
		
		//set beginLine to entryNode
		if(!method.getBody().getChildrenNodes().isEmpty()){
			cfg.getEntry().setBeginLine(method.getBody().getChildrenNodes().get(0).getBeginLine());
		}
		else{
			cfg.getEntry().setBeginLine(method.getBody().getBeginLine());
		}
		
		return cfg;
	}

	@Override
	protected CFG convert(AssertStmt n) {
		return convertProcessStmt(n);
	}

	@Override
	protected CFG convert(LabeledStmt n) {
		return convertProcessStmt(n);
	}

	@Override
	protected CFG convert(SynchronizedStmt n) {
		return convertProcessStmt(n);
	}

	@Override
	protected CFG convert(TryStmt n) {
		return convertProcessStmt(n);
	}

	@Override
	protected CFG convert(ExplicitConstructorInvocationStmt n) {
		return convertProcessStmt(n);
	}

	@Override
	protected CFG convert(ThrowStmt n) {
		return convertProcessStmt(n);
	}

	protected void dealParentLoopLine(Object n, CfgDecisionNode decision) {
		Node node = null;
		if (((Node) n).getParentNode() != null) {
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
					decision.setParentBeginLine(temporaryDecisionNodeList
								.get( decisionNodeMap.get(node.toString())).getBeginLine());
				//	System.out.println(decision.getBeginLine() + " " + decision.getParentBeginLine());
					break;
				}
			}
		}
	}
	
	protected void setTrueBeginLine(Statement n ,  CfgDecisionNode decision){
		if(n instanceof japa.parser.ast.stmt.WhileStmt){
			if(!((WhileStmt) n).getBody().getChildrenNodes().isEmpty()){
			decision.setTrueBeginLine( ((WhileStmt) n).getBody().getChildrenNodes().get(0).getBeginLine());
			}
			else{
				decision.setTrueBeginLine( ((WhileStmt) n).getBody().getBeginLine());
			}
		}
		else if(n instanceof japa.parser.ast.stmt.ForStmt){
			if(!((ForStmt) n).getBody().getChildrenNodes().isEmpty()){
			decision.setTrueBeginLine( ((ForStmt) n).getBody().getChildrenNodes().get(0).getBeginLine());
			}
			else{
				decision.setTrueBeginLine( ((ForStmt) n).getBody().getBeginLine());
			}
		}
		else if(n instanceof japa.parser.ast.stmt.ForeachStmt){
			if(!((ForeachStmt) n).getBody().getChildrenNodes().isEmpty()){
			decision.setTrueBeginLine( ((ForeachStmt) n).getBody().getChildrenNodes().get(0).getBeginLine());
			}
			else{
				decision.setTrueBeginLine( ((ForeachStmt) n).getBody().getBeginLine());
			}
		}
		else if(n instanceof japa.parser.ast.stmt.DoStmt){
			if(!((DoStmt) n).getBody().getChildrenNodes().isEmpty()){
			decision.setTrueBeginLine( ((DoStmt) n).getBody().getChildrenNodes().get(0).getBeginLine());
			}
			else{
				decision.setTrueBeginLine( ((DoStmt) n).getBody().getBeginLine());
			}
		}
		else if(n instanceof japa.parser.ast.stmt.IfStmt){
			if(!((IfStmt) n).getThenStmt().getChildrenNodes().isEmpty()){
				decision.setTrueBeginLine( ((IfStmt) n).getThenStmt().getChildrenNodes().get(0).getBeginLine());
				}
				else{
					decision.setTrueBeginLine( ((IfStmt) n).getThenStmt().getBeginLine());
				}
		}
		else if(n instanceof japa.parser.ast.stmt.SwitchEntryStmt){
			//System.out.println(n.getBeginLine());
			if(!n.getChildrenNodes().isEmpty()){
				decision.setTrueBeginLine( n.getChildrenNodes().get(0).getBeginLine());
			}
			else{
				decision.setTrueBeginLine( n.getBeginLine());
			}
		}
	}

}
