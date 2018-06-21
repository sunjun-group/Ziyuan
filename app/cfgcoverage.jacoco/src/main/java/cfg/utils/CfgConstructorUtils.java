/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfg.utils;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfg.CFG;
import cfg.CfgNode;
import cfg.DecisionBranchType;
import sav.common.core.utils.Assert;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class CfgConstructorUtils {
	private static final Logger log = LoggerFactory.getLogger(CfgConstructorUtils.class);
	private CfgConstructorUtils() {}
	
	public static void updateNodesInLoop(CFG cfg) {
		int size = cfg.getNodeList().size();
		int i = size - 1;
		for (i = size - 1; i > 0; i--) {
			CfgNode node = cfg.getNodeList().get(i);
			int firstIdxOfLoopBlk = node.getBackwardJumpIdx();
			/* if firstIdx is not valid meaning node is not a loop header, we move to another node */
			if (firstIdxOfLoopBlk != CfgNode.INVALID_IDX) {
				CfgNode loopHeader = getLoopHeader(cfg.getNodeList(), node);
				CfgNode firstNested = getAnotherCondNodeOfNestedLoopCond(cfg, loopHeader.getIdx(), firstIdxOfLoopBlk);
				if (firstNested != null) {
					CfgNode anotherLoopHeaderCandidate = findLoopHeaderOfNestedCond(cfg, firstIdxOfLoopBlk, firstNested, loopHeader);
					if (anotherLoopHeaderCandidate != null) {
						loopHeader = anotherLoopHeaderCandidate;
					}
				}
				/* this decision node is loop header */
				for (int j = firstIdxOfLoopBlk; j <= node.getIdx(); j++) {
					cfg.getNode(j).addLoopHeader(loopHeader);
				}
			}
		}
	}
	
	private static CfgNode findLoopHeaderOfNestedCond(CFG cfg, int firstIdxOfLoopBlk, CfgNode firstNested, CfgNode loopHeader) {
		for (int i = firstIdxOfLoopBlk; i < firstNested.getIdx(); i++) {
			CfgNode node = cfg.getNode(i);
			if (node.isDecisionNode()) {
				for (CfgNode branch : node.getBranches()) {
					if (branch.getIdx() < loopHeader.getIdx() && branch.getIdx() > firstNested.getIdx()) {
						return node;
					}
				}
			}
		}
		return null;
	}

	private static CfgNode getAnotherCondNodeOfNestedLoopCond(CFG cfg, int lastIdx, int backwardJumpIdx) {
		CfgNode firstNestedCondNode = null;
		for (int i = lastIdx - 1; i > 0; i--) {
			CfgNode node = cfg.getNode(i);
			if (node.getBackwardJumpIdx() == backwardJumpIdx) {
				firstNestedCondNode = node;
			}
		}
		return firstNestedCondNode;
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
	 * using labeling.
	 * @param cfg
	 */
	public static void computeControlDependency(CFG cfg) {
		List<CfgNode> decisionNodes = cfg.getDecisionNodes();
		if (decisionNodes.isEmpty()) {
			return;
		}
		/* update dominatte for nodes between each decision node and next decision node/leaf node (included) */
		setDecisionNodeDependenteesAndDirectLevelRelationship(decisionNodes);
		CfgNode root = cfg.getFirstDecisionNode();
		Assert.assertNotNull(root, "fail to look up the first decision node!");
		
		calculateControlDominators(root, cfg);
	}

	/**
	 * @param decisionNodes
	 */
	private static void setDecisionNodeDependenteesAndDirectLevelRelationship(List<CfgNode> decisionNodes) {
		for (CfgNode node : decisionNodes) {
			for (int branchIdx = 0; branchIdx < node.getBranchTotal(); branchIdx++) {
				CfgNode branch = node.getBranches().get(branchIdx);
				short relationshipToDecisionNode = ControlRelationship.getRelationshipToDecisionPrecessor(branchIdx);
				boolean completed = false;
				/* iterate when child is not a leaf node or at the end of the loop */
				while (branch != null && branch != node && !completed) {
					/* if the branch of node is actually its loop header, then add */
					boolean childIsLoopHeaderOfNode = branch.isLoopHeaderOf(node);
					node.setDecisionControlRelationship(branch, ControlRelationship
							.mergeBPD(node.getDecisionControlRelationship(branch), relationshipToDecisionNode));
					branch.addDominator(node, childIsLoopHeaderOfNode);
					if (branch.isDecisionNode()) {
						node.addDependentee(branch, childIsLoopHeaderOfNode);
						if (!childIsLoopHeaderOfNode) {
							break;
						}
						break;
					} else {
						CfgNode next = branch.getNext();
						if (next == null) {
							break;
						}
						branch = next;
					}
				}
			}
		}
	}
	
	private static void calculateControlDominators(CfgNode root, CFG cfg) {
		Set<CfgNode> visited = new HashSet<CfgNode>();
		Stack<CfgNode> visitStack = new Stack<CfgNode>();
		visitStack.push(root);
		while (!visitStack.isEmpty()) {
			CfgNode lastNode = visitStack.lastElement();
			if (visited.contains(lastNode)) {
				visitStack.pop();
				continue;
			}
			if (lastNode.getDependentees() == null) {
				visited.add(lastNode);
				visitStack.pop();
				continue;
			}
			boolean allDependenteesVisited = true;
			
			for (CfgNode dependentee : getDependenteeSortByLoopHeader(lastNode)) {
				if (!visited.contains(dependentee)) {
					allDependenteesVisited = false;
					if (visitStack.contains(dependentee)) {
						log.debug("warning: suspicious dependency!!: dependentee[{}], lastNode[{}]", dependentee,
								lastNode);
					}
					visitStack.push(dependentee);
				}
			}
			
			/* we update backward, calculate post-dominator of a node based on control dependency relationship of its sub decisionnode */
			if (allDependenteesVisited) {
				for (CfgNode directDependentee : lastNode.getDependentees()) {
					short relationshipOfDirectDependenteeOnLastNode = lastNode.getDecisionControlRelationship(directDependentee);
					if (ControlRelationship.isPostDominance(relationshipOfDirectDependenteeOnLastNode, lastNode.getBranchTotal())) {
						for (Entry<CfgNode, Short> entry : directDependentee.getDecisionControlRelationships().entrySet()) {
							CfgNode entryNode = entry.getKey();
							if (isLoopHeaderPrecessor(lastNode, entryNode)) {
								continue;
							}
							short relationship = 0;
							if (ControlRelationship.isPostDominance(entry.getValue(), directDependentee.getBranchTotal())) {
								relationship = ControlRelationship.postDominance(lastNode.getBranchTotal());
							} else {
								relationship = ControlRelationship.fullControlDependency(lastNode.getBranchTotal());
							}
							lastNode.setDecisionControlRelationship(entry.getKey(), relationship);
							entryNode.updateDominator(lastNode, relationship);
						}
					} else {
						for (Entry<CfgNode, Short> entry : directDependentee.getDecisionControlRelationships().entrySet()) {
							CfgNode entryNode = entry.getKey();
							if (isLoopHeaderPrecessor(lastNode, entryNode)) {
								continue;
							}
							short relationship = lastNode.getDecisionControlRelationship(entry.getKey());
							if (ControlRelationship.isPostDominance(entry.getValue(), directDependentee.getBranchTotal())) {
								relationship = ControlRelationship.weakMerge(relationship, relationshipOfDirectDependenteeOnLastNode);
							} else {
								relationship = ControlRelationship.weakMerge(relationship,
										ControlRelationship.toControlDependency(
												relationshipOfDirectDependenteeOnLastNode, lastNode.getBranchTotal()));
							}
							lastNode.setDecisionControlRelationship(entry.getKey(), relationship);
							entryNode.updateDominator(lastNode, relationship);
						}
					}
				}
				visited.add(lastNode);
				visitStack.pop();
			}
		}
	}
	
	private static boolean isLoopHeaderPrecessor(CfgNode node, CfgNode entryNode) {
		if (!node.isLoopHeader()) {
			return false;
		}
		/* check if there is a direct path from entryNode to loopHeader node */
		CfgNode nextNode = entryNode;
		while (nextNode != null && !nextNode.isDecisionNode()) {
			if (nextNode == node) {
				return true;
			}
			nextNode = nextNode.getNext();
		}
		
		return false;
	}

	private static Collection<CfgNode> getDependenteeSortByLoopHeader(CfgNode node) {
		if (!node.isLoopHeader()) {
			return node.getDependentees();
		}
		List<CfgNode> list = new ArrayList<CfgNode>(node.getDependentees());
		Collections.sort(list, new Comparator<CfgNode>() {

			@Override
			public int compare(CfgNode o1, CfgNode o2) {
				if (!node.isLoopHeaderOf(o1)) {
					return 1;
				}
				if (!node.isLoopHeaderOf(o2)) {
					return -1;
				}
 				return 0;
			}
		});
		return list;
	}
	
}
