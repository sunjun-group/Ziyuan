/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import cfgcoverage.jacoco.analysis.data.BranchRelationship;
import cfgcoverage.jacoco.analysis.data.CFG;
import cfgcoverage.jacoco.analysis.data.CfgNode;
import sav.common.core.SavRtException;
import sav.common.core.utils.Assert;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class CfgConstructorUtils {
	private CfgConstructorUtils() {}
	
	public static void completeCfg(CFG cfg) {
		updateExitNodes(cfg);
		updateDecisionNodes(cfg);
		updateNodesInLoop(cfg);
		computeControlDependency(cfg);
	}
	
	public static void updateExitNodes(CFG cfg) {
		for (CfgNode node : cfg.getNodeList()) {
			if (node.isLeaf()) {
				cfg.addExitNode(node);
			}
		}
	}
	
	public static void updateNodesInLoop(CFG cfg) {
		int size = cfg.getNodeList().size();
		int i = size - 1;
		for (i = size - 1; i > 0; i--) {
			CfgNode node = cfg.getNodeList().get(i);
			int firstIdxOfLoopBlk = node.getFistBlkIdxIfLoopHeader();
			/* if firstIdx is not valid meaning node is not a loop header, we move to another node */
			if (firstIdxOfLoopBlk != CfgNode.INVALID_IDX) {
				/* this decision node is loop header */
				for (int j = firstIdxOfLoopBlk; j <= node.getIdx(); j++) {
					cfg.getNode(j).addLoopHeader(node);
				}
			}
		}
	}
	
	public static void updateDecisionNodes(CFG cfg) {
		for (CfgNode node : cfg.getNodeList()) {
			if (node.getBranches() != null &&
					node.getBranches().size() > 1) {
					node.setDecisionNode(true);
			}
		}
	}

	/**
	 * build up cfgNode dominatees and dependentees
	 * @param cfg
	 */
	public static void computeControlDependency(CFG cfg) {
		List<CfgNode> decisionNodes = cfg.getDecisionNodes();
		if (decisionNodes.isEmpty()) {
			return;
		}
		/* update dominatte for nodes between each decision node and next decision node/leaf node (included) */
		for (CfgNode node : decisionNodes) {
			for (CfgNode branch : node.getBranches()) {
				CfgNode child = branch;
				BranchRelationship branchType = node.getBranchRelationship(branch.getIdx());
				/* iterate when child is not a leaf node or at the end of the loop */
				while (child != null && child != node) {
					/* if the branch of node is actually its loop header, then add */
					boolean childIsLoopHeaderOfNode = child.isLoopHeaderOf(node);
					child.addDominatee(node, childIsLoopHeaderOfNode, branchType);
					if (child.isDecisionNode()) {
						node.addDependentee(child, childIsLoopHeaderOfNode, branchType);
						break;
					}
					child = child.getNext();
				}
			}
		}
		
		/* copy dominatees of parent node to each node */
		CfgNode root = getVeryFirstDecisionNode(decisionNodes);
		Assert.assertNotNull(root, "fail to loop up the first decision node!");
		updateDependentees(root);
		updateDominatees(decisionNodes);
	}

	/**
	 * @param decisionNodes
	 */
	private static void updateDominatees(List<CfgNode> decisionNodes) {
		for (CfgNode node : decisionNodes) {
			if (node.getDependentees() == null) {
				continue;
			}
			for (CfgNode dependentee : node.getDependentees()) {
				dependentee.addDominatee(node, false, node.getBranchRelationship(dependentee.getIdx()));
			}
		}
	}
	
	private static void updateDependentees(CfgNode root) {
		Set<CfgNode> visited = new HashSet<CfgNode>();
		Stack<CfgNode> visitStack = new Stack<CfgNode>();
		visitStack.push(root);
		while (!visitStack.isEmpty()) {
			CfgNode lastNode = visitStack.lastElement();
			/* if node already visit, remove from stack */
			if (visited.contains(lastNode)) {
				visitStack.pop();
				continue;
			}
			/* if node has no dependentee, mark visited and remove from stack */
			if (lastNode.getDependentees() == null) {
				visited.add(lastNode);
				visitStack.pop();
				continue;
			}
			boolean allDependenteesVisited = true;
			
			for (CfgNode dependentee : lastNode.getDependentees()) {
				if (!visited.contains(dependentee)) {
					visitStack.push(dependentee);
					allDependenteesVisited = false;
				}
			}
			if (allDependenteesVisited) {
				/* update dependentees from its dependentees and remove from stack */
				for (CfgNode dependentee : lastNode.getDependentees()) {
					lastNode.addChildsDependentees(dependentee);
				}
				visited.add(lastNode);
				visitStack.pop();
			}
		}
	}

	/**
	 * @param decisionNodes
	 * @return
	 */
	private static CfgNode getVeryFirstDecisionNode(List<CfgNode> decisionNodes) {
		Assert.assertTrue(CollectionUtils.isNotEmpty(decisionNodes), "");
		CfgNode first = decisionNodes.get(0);
		if (first.getDominatees() == null) {
			return first;
		}
		
		/* if first node is inside a loop, then lookup its loopheader */
		if (!first.isInLoop()) {
			throw new SavRtException("first decision node has dominatee but not inside a loop! " + first.toString());
		}
		for (CfgNode loopHeader : first.getLoopHeaders()) {
			if (loopHeader.getDominatees() == null) {
				return loopHeader;
			}
		}
		throw new SavRtException("could not find the very root node of cfg!");
	}
}
