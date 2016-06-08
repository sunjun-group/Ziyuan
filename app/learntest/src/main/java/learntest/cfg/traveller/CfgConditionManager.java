package learntest.cfg.traveller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import learntest.calculator.MultiNotDividerBasedCategoryCalculator;
import learntest.calculator.OrCategoryCalculator;
import learntest.cfg.CFG;
import learntest.cfg.CfgDecisionNode;
import learntest.cfg.CfgEdge;
import learntest.cfg.CfgFalseEdge;
import learntest.cfg.CfgNode;
import learntest.cfg.CfgTrueEdge;
import libsvm.core.CategoryCalculator;
import libsvm.core.Divider;
import libsvm.extension.MultiDividerBasedCategoryCalculator;
import sav.common.core.Pair;
import sav.common.core.formula.AndFormula;
import sav.common.core.formula.Formula;
import sav.common.core.formula.NotFormula;
import sav.strategies.dto.execute.value.ExecVar;

public class CfgConditionManager {
	
	private CfgDecisionNode begin;
	
	private Map<Integer, CfgDecisionNode> nodeMap;	
	private Map<CfgDecisionNode, CfgDecisionNode> trueNext;
	private Map<CfgDecisionNode, CfgDecisionNode> falseNext;
	private Map<CfgDecisionNode, CfgDecisionNode> next;
	
	private List<ExecVar> vars;
	
	public CfgConditionManager(CFG cfg) {
		List<CfgEdge> entryOutEdges = cfg.getEntryOutEdges();
		if (!entryOutEdges.isEmpty()) {
			CfgNode dest = entryOutEdges.get(0).getDest();
			if (dest instanceof CfgDecisionNode) {
				begin = (CfgDecisionNode) dest;
			}
		}
		nodeMap = new HashMap<Integer, CfgDecisionNode>();
		trueNext = new HashMap<CfgDecisionNode, CfgDecisionNode>();
		falseNext = new HashMap<CfgDecisionNode, CfgDecisionNode>();
		next = new HashMap<CfgDecisionNode, CfgDecisionNode>();
		List<CfgNode> vertices = cfg.getVertices();
		for (CfgNode node : vertices) {
			if (node instanceof CfgDecisionNode) {
				nodeMap.put(node.getBeginLine(), (CfgDecisionNode) node);
				List<CfgEdge> outEdges = cfg.getOutEdges(node);
				CfgDecisionNode trueNode = null;
				CfgDecisionNode falseNode = null;
				for (CfgEdge edge : outEdges) {
					CfgNode dest = edge.getDest();
					if (dest instanceof CfgDecisionNode) {
						if (edge instanceof CfgTrueEdge) {
							trueNode = (CfgDecisionNode) dest;
						} else if (edge instanceof CfgFalseEdge) {
							falseNode  = (CfgDecisionNode) dest;
						}
					}
				}
				if (trueNode != falseNode) {
					if (trueNode != null && trueNode.getBeginLine() > node.getBeginLine()) {
						trueNext.put((CfgDecisionNode) node, trueNode);
					}
					if (falseNode != null && falseNode.getBeginLine() > node.getBeginLine()) {
						falseNext.put((CfgDecisionNode) node, falseNode);	
					}
				} else if (trueNode != null && trueNode.getBeginLine() > node.getBeginLine()) {
					next.put((CfgDecisionNode) node, trueNode);
				}
			}
		}
	}
	
	public void setVars(List<ExecVar> vars) {
		this.vars = vars;
	}
	
	public void setCondition(int lineNo, Pair<Formula, Formula> formulas, List<Divider> dividers) {
		CfgDecisionNode node = nodeMap.get(lineNo);
		node.setTrueFalse(formulas.first());
		if (node.isLoop()) {
			node.setOneMore(formulas.second());
		}
		node.setDividers(dividers);
		
		if (dividers == null) {
			CfgDecisionNode trueNode = trueNext.get(node);
			CfgDecisionNode falseNode = falseNext.get(node);
			if (trueNode != null || falseNode != null) {
				List<List<CategoryCalculator>> preconditions = node.getPreconditions();
				if (trueNode != null) {
					for (List<CategoryCalculator> list : preconditions) {
						List<CategoryCalculator> cur = new ArrayList<CategoryCalculator>(list);
						trueNode.addPrecondition(cur);
					}
				}
				if (falseNode != null && node.isLoop()) {
					for (List<CategoryCalculator> list : preconditions) {
						List<CategoryCalculator> cur = new ArrayList<CategoryCalculator>(list);
						falseNode.addPrecondition(cur);
					}
				}
			}
			return;
		}
		
		CfgDecisionNode trueNode = trueNext.get(node);
		CfgDecisionNode falseNode = falseNext.get(node);
		if (trueNode != null || falseNode != null) {
			List<List<CategoryCalculator>> preconditions = node.getPreconditions();
			if (trueNode != null) {
				CategoryCalculator condition = new MultiDividerBasedCategoryCalculator(dividers);
				if (preconditions.isEmpty()) {
					List<CategoryCalculator> cur = new ArrayList<CategoryCalculator>();
					cur.add(condition);
					trueNode.addPrecondition(cur);
				} else {
					for (List<CategoryCalculator> list : preconditions) {
						List<CategoryCalculator> cur = new ArrayList<CategoryCalculator>(list);
						cur.add(condition);
						trueNode.addPrecondition(cur);
					}
				}				
			}
			if (falseNode != null) {
				CategoryCalculator condition = new MultiNotDividerBasedCategoryCalculator(dividers);
				if (preconditions.isEmpty()) {
					List<CategoryCalculator> cur = new ArrayList<CategoryCalculator>();
					cur.add(condition);
					falseNode.addPrecondition(cur);
				} else {
					for (List<CategoryCalculator> list : preconditions) {
						List<CategoryCalculator> cur = new ArrayList<CategoryCalculator>(list);
						cur.add(condition);
						falseNode.addPrecondition(cur);
					}
				}				
			}
		}
	}
	
	public OrCategoryCalculator getPreConditions(int lineNo) {
		CfgDecisionNode node = nodeMap.get(lineNo);
		return new OrCategoryCalculator(node.getPreconditions(), vars);
	}

	public List<List<Formula>> buildPaths() {
		List<List<Formula>> res = new ArrayList<List<Formula>>();
		if (begin == null) {
			return res;
		}
		buildPaths(begin, new ArrayList<Formula>(), res);
		return res;
	}
	
	private void buildPaths(CfgDecisionNode node, List<Formula> prefix, List<List<Formula>> res) {
		CfgDecisionNode trueNode = trueNext.get(node);
		CfgDecisionNode falseNode = falseNext.get(node);
		if (trueNode == null && falseNode == null) {
			trueNode = next.get(node);
			falseNode = next.get(node);
		}
		Formula trueBranch = node.getTrueFalse();
		Formula falseBranch = null;
		Formula moreBranch = null;
		if (trueBranch != null) {
			falseBranch = new NotFormula(trueBranch);
		}
		if (node.isLoop()) {
			moreBranch = node.getOneMore();
			Formula tmp = moreBranch;
			if (trueBranch != null) {
				if (moreBranch == null) {
					tmp = trueBranch;
				} else {
					tmp = new AndFormula(trueBranch, moreBranch);
				}
			}
			if (moreBranch != null) {
				if (trueBranch == null) {
					trueBranch = new NotFormula(moreBranch);
				} else {
					trueBranch = new AndFormula(trueBranch, new NotFormula(moreBranch));
				}
			}
			moreBranch = tmp;
		}
		if (falseNode != null) {
			if (falseBranch != null) {
				prefix.add(falseBranch);
			}
			buildPaths(falseNode, prefix, res);
			if (falseBranch != null) {
				prefix.remove(prefix.size() - 1);
			}
		}  else if (falseBranch != null) {
			List<Formula> path = new ArrayList<Formula>(prefix);
			path.add(falseBranch);
			res.add(path);
		}
		if (trueNode != null) {
			if (trueBranch != null) {
				prefix.add(trueBranch);
			}
			buildPaths(trueNode, prefix, res);
			if (trueBranch != null) {
				prefix.remove(prefix.size() - 1);
			}
		} else {
			List<Formula> path = new ArrayList<Formula>(prefix);
			if (trueBranch != null) {
				path.add(trueBranch);
			}
			res.add(path);
		}
		if (moreBranch != null) {
			List<Formula> path = new ArrayList<Formula>(prefix);
			path.add(moreBranch);
			res.add(path);
		}
	}

}
