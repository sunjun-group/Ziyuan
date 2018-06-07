/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfg;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.tree.MethodNode;

/**
 * @author LLT
 *
 */
public class CFG {
	private String id;
	private List<CfgNode> nodeList;
	private CfgNode startNode;
	private List<CfgNode> exitList;
	
	private MethodNode methodNode;
	private boolean isCDG = false;
	
	public CFG(String methodId) {
		this.id = methodId;
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
	
	public CfgNode getStartNode() {
		return startNode;
	}
	
	public List<CfgNode> getNodeList() {
		return nodeList;
	}
	
	public void addExitNode(CfgNode node) {
		exitList.add(node);
	}

	@Override
	public String toString () {
		return "Cfg[nodeList=" + nodeList + ", \nstartNode=" + startNode + ",\n exitList=" + exitList
				+ ", \nmethodNode=" + methodNode + "]";
	}

	private List<CfgNode> decisionNodes;
	public List<CfgNode> getDecisionNodes() {
		if (decisionNodes == null) {
			decisionNodes = new ArrayList<CfgNode>();
			for (CfgNode node : nodeList) {
				if (node.isDecisionNode()) {
					decisionNodes.add(node);
				}
			}
		}
		return decisionNodes;
	}

	public String getId() {
		return id;
	}
	
	public boolean isCDG() {
		return isCDG;
	}
	
	public void setCDG(boolean isCDG) {
		this.isCDG = isCDG;
	}
}
