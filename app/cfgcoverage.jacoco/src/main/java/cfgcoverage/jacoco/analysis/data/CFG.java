/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.analysis.data;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.tree.MethodNode;

/**
 * @author LLT
 *
 */
public class CFG {
	private List<CfgNode> nodeList;
	private CfgNode startNode;
	private List<CfgNode> exitList;
	
	private MethodNode methodNode;
	
	public CFG() {
		nodeList = new ArrayList<CfgNode>();
		exitList = new ArrayList<CfgNode>();
	}
	
	public void setStartNode(CfgNode startNode) {
		this.startNode = startNode;
	}

	public void addNode(CfgNode node) {
		if (startNode == null) {
			setStartNode(node);
		}
		// set node idx as its index in nodelist
		node.setIdx(nodeList.size());
		nodeList.add(node);
	}
	
	public void setMethodNode(MethodNode methodNode) {
		this.methodNode = methodNode;
	}
	
	public MethodNode getMethodNode() {
		return methodNode;
	}

	public CfgNode getNode(int nodeIdx) {
		if (nodeIdx >= nodeList.size()) {
			return null;
		}
		return nodeList.get(nodeIdx);
	}
	
	@Override
	public String toString() {
		return "CfgCoverage [nodeList=" + nodeList + ", \nstartNode=" + startNode + ",\n exitList=" + exitList
				+ ", \nmethodNode=" + methodNode + "]";
	}

	public List<CfgNode> getDecisionNodes() {
		List<CfgNode> nodes = new ArrayList<CfgNode>();
		for (CfgNode node : nodeList) {
			if (node.isDecisionNode()) {
				nodes.add(node);
			}
		}
		return nodes;
	}
	
	public static void updateExitNodes(CFG cfg) {
		for (CfgNode node : cfg.nodeList) {
			if (node.isLeaf()) {
				cfg.exitList.add(node);
			}
		}
	}
	
	public static void updateNodesInLoop(CFG cfg) {
		int size = cfg.nodeList.size();
		int i = size - 1;
		while (i > 0) {
			CfgNode node = cfg.nodeList.get(i);
			int firstIdxOfLoopBlk = node.getFistBlkIdxIfLoopHeader();
			/* if firstIdx is not valid meaning node is not a loop header, we move to another node */
			if (firstIdxOfLoopBlk == CfgNode.INVALID_IDX) {
				i--;
				continue;
			} else {
				for (int j = firstIdxOfLoopBlk; j <= node.getIdx(); j++) {
					cfg.getNode(j).setInLoop(true);
				}
				i = firstIdxOfLoopBlk - 1;
			}
		}
	}
	
	public static void updateDecisionNodes(CFG cfg) {
		for (CfgNode node : cfg.nodeList) {
			if (node.getBranches() != null &&
					node.getBranches().size() > 1) {
					node.setDecisionNode(true);
			}
		}
	}

}
