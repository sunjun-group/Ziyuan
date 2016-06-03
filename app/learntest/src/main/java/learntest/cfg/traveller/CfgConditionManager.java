package learntest.cfg.traveller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import learntest.cfg.CFG;
import learntest.cfg.CfgDecisionNode;
import learntest.cfg.CfgEdge;
import learntest.cfg.CfgFalseEdge;
import learntest.cfg.CfgNode;
import learntest.cfg.CfgTrueEdge;
import sav.common.core.formula.AndFormula;
import sav.common.core.formula.Formula;
import sav.common.core.formula.NotFormula;

public class CfgConditionManager {
	
	private Map<CfgDecisionNode, CfgDecisionNode> trueNext;
	private Map<CfgDecisionNode, CfgDecisionNode> falseNext;
	
	public CfgConditionManager(CFG cfg) {
		trueNext = new HashMap<CfgDecisionNode, CfgDecisionNode>();
		falseNext = new HashMap<CfgDecisionNode, CfgDecisionNode>();
		List<CfgNode> vertices = cfg.getVertices();
		for (CfgNode node : vertices) {
			if (node instanceof CfgDecisionNode) {
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
	
	public void setCondition(CfgDecisionNode node, Formula condition) {
		node.setCondition(condition);
		CfgDecisionNode trueNode = trueNext.get(node);
		CfgDecisionNode falseNode = falseNext.get(node);
		if (trueNode != null || falseNode != null) {
			List<Formula> preconditions = node.getPreconditions();
			if (trueNode != null) {
				for (Formula formula : preconditions) {
					trueNode.addPrecondition(new AndFormula(formula, condition));
				}
			}
			if (falseNode != null) {
				for (Formula formula : preconditions) {
					falseNode.addPrecondition(new AndFormula(formula, new NotFormula(condition)));
				}
			}
		}
	}

}
