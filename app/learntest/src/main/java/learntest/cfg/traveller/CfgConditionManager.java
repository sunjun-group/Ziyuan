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
import sav.common.core.formula.Formula;
import sav.strategies.dto.execute.value.ExecVar;

public class CfgConditionManager {
	
	private Map<Integer, CfgDecisionNode> nodeMap;
	
	private Map<CfgDecisionNode, CfgDecisionNode> trueNext;
	private Map<CfgDecisionNode, CfgDecisionNode> falseNext;
	
	private List<ExecVar> vars;
	
	public CfgConditionManager(CFG cfg) {
		nodeMap = new HashMap<Integer, CfgDecisionNode>();
		trueNext = new HashMap<CfgDecisionNode, CfgDecisionNode>();
		falseNext = new HashMap<CfgDecisionNode, CfgDecisionNode>();
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

}
