/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfg.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfg.BranchRelationship;
import cfg.CFG;
import cfg.CfgNode;
import cfg.DecisionBranchType;
import sav.common.core.SavRtException;
import sav.common.core.utils.Assert;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class CfgConstructorUtils {
	private static final Logger log = LoggerFactory.getLogger(CfgConstructorUtils.class);
	private CfgConstructorUtils() {}
	
	public static void completeCfg(CFG cfg) {
		updateExitNodes(cfg);
		updateDecisionNodes(cfg);
		updateNodesInLoop(cfg);
		reconcileLoopHeaderBranches(cfg);
		computeControlDependency(cfg);
	}
	
	/**
	 * before this, if the decision node is a loop header,
	 * the false branch is outside the loop, but not be reached by a jump,
	 * so it is not distinguished with other normal next nodes which are treated as a TRUES branch of current node.
	 * So we have to check again and in the case that the branch is outside loop, we treat it as a TRUE_FALSE branch.
	 * @param cfg
	 */
	private static void reconcileLoopHeaderBranches(CFG cfg) {
		for (CfgNode node : cfg.getDecisionNodes()) {
			if (!node.isLoopHeader()) {
				continue;
			}
			for (CfgNode branch : node.getBranches()) {
				if (!node.isLoopHeaderOf(branch)) {
					node.addBranchRelationship(branch, BranchRelationship.FALSE);
				}
			}
		}
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
			int firstIdxOfLoopBlk = node.getBackwardJumpIdx();
			/* if firstIdx is not valid meaning node is not a loop header, we move to another node */
			if (firstIdxOfLoopBlk != CfgNode.INVALID_IDX) {
				CfgNode loopHeader = getLoopHeader(cfg.getNodeList(), node);
				/* this decision node is loop header */
				for (int j = firstIdxOfLoopBlk; j <= node.getIdx(); j++) {
					cfg.getNode(j).addLoopHeader(loopHeader);
				}
			}
		}
	}
	
	/**
	 * in a simple case which only contain one loop condition, the 
	 * the node which has backward jump is also a loop header,
	 * but in case there are more than one loop condition, we should consider the first condition
	 * as the loop header.
	 * @param list 
	 * */
	private static CfgNode getLoopHeader(List<CfgNode> nodeList, CfgNode hasBackwardJumpNode) {
		for (int j = hasBackwardJumpNode.getBackwardJumpIdx(); j < hasBackwardJumpNode.getIdx(); j++) {
			CfgNode inLoopNode = nodeList.get(j);
			/* if hasBackwardJumpNode is on the false branch of this inLoop decision node,
			 * this must be the correct loop header. 
			 * */
			if (inLoopNode.isDecisionNode()) {
				/* in case of a loop condition, the true branch will be out of the loop */
				CfgNode trueBranch = inLoopNode.getDecisionBranch(DecisionBranchType.TRUE);
				if (trueBranch != null && trueBranch.getIdx() > hasBackwardJumpNode.getIdx()) {
					return inLoopNode;
				}
			}
		}
		return hasBackwardJumpNode;
	}

	public static void updateDecisionNodes(CFG cfg) {
		for (CfgNode node : cfg.getNodeList()) {
			if (CollectionUtils.getSize(node.getBranches()) > 1) {
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
					child.addDominator(node, childIsLoopHeaderOfNode, branchType);
					if (child.isDecisionNode()) {
						node.addDependentee(child, childIsLoopHeaderOfNode, branchType);
						break;
					}
					CfgNode next = child.getNext();
					if (next == null || next.getIdx() < child.getIdx()) {
						break;
					}
					child = next;
				}
			}
		}
		
		/* copy dominatees of parent node to each node */
		CfgNode root = getVeryFirstDecisionNode(decisionNodes);
		Assert.assertNotNull(root, "fail to look up the first decision node!");
		updateDependentees(root);
		updateDominators(cfg);
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
					allDependenteesVisited = false;
					if (visitStack.contains(dependentee)) {
						log.debug("warning: suspicious dependency!!: dependentee[{}], lastNode[{}]", dependentee,
								lastNode);
					}
					visitStack.push(dependentee);
				}
			}
			if (allDependenteesVisited) {
				/* update dependentees from its dependentees and remove from stack */
				List<CfgNode> directDependentees = CollectionUtils.copy(lastNode.getDependentees());
				for (CfgNode dependentee : directDependentees) {
					lastNode.addChildsDependentees(dependentee);
				}
				visited.add(lastNode);
				visitStack.pop();
			}
		}
	}
	
	private static void updateDominators(CFG cfg) {
		for (CfgNode node : cfg.getDecisionNodes()) {
			if (node.getDependentees() == null) {
				continue;
			}
			for (CfgNode dependentee : node.getDependentees()) {
				dependentee.addDominator(node, false, node.getBranchRelationship(dependentee.getIdx()));
			}
		}
		/* update branch relationship with decision node for not decision nodes */
		for (CfgNode node : cfg.getNodeList()) {
			if (!node.isDecisionNode()) {
				for (CfgNode directDominator : CollectionUtils.nullToEmpty(node.getDominators())) {
					for (CfgNode indirectDominator : CollectionUtils.nullToEmpty(directDominator.getDominators())) {
						node.addBranchRelationship(indirectDominator, directDominator.getBranchRelationship(indirectDominator.getIdx()));
					}
				}
			}
		}
	}

	/**
	 * @param decisionNodes
	 * @return
	 */
	public static CfgNode getVeryFirstDecisionNode(List<CfgNode> decisionNodes) {
		Assert.assertTrue(CollectionUtils.isNotEmpty(decisionNodes), "cfg has no decisionNode!");
		CfgNode first = decisionNodes.get(0);
		if (first.getDominators() == null) {
			return first;
		}
		
		/* if first node is inside a loop, then lookup its loopheader */
		if (!first.isInLoop()) {
			throw new SavRtException("first decision node has dominatee but not inside a loop! " + first.toString());
		}
		for (CfgNode loopHeader : first.getLoopHeaders()) {
			if (loopHeader.getDominators() == null) {
				return loopHeader;
			}
		}
		throw new SavRtException("could not find the very root node of cfg!");
	}
	
	/**
	 * control dependency graph. 
	 * TODO LLT: this is just draft, so-called "branchRelationship" must be implemented correctly according to
	 * control dependency concept. 
	 */
	public static void toCDG(CFG cfg) {
		if (cfg.isCDG()) {
			return;
		}
		for (CfgNode node : cfg.getNodeList()) {
			/* dominator - dependentee */
			Iterator<CfgNode> it = node.getDominators().iterator();
			while (it.hasNext()) {
				CfgNode dominator = it.next();
				if (node.getBranchRelationship(dominator.getIdx()) == BranchRelationship.TRUE_FALSE) {
					it.remove();
					dominator.getDependentees().remove(node);
				}
			}
			/* loopDominator - loopDependentee */
			it = node.getLoopDominators().iterator();
			while (it.hasNext()) {
				CfgNode dominator = it.next();
				if (node.getBranchRelationship(dominator.getIdx()) == BranchRelationship.TRUE_FALSE) {
					it.remove();
					dominator.getLoopDependentees().remove(node);
				}
			}
		}
		cfg.setCDG(true);
	}
}
