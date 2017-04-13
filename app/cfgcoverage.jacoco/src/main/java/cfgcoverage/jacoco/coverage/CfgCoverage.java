/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.coverage;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.tree.MethodNode;


/**
 * @author LLT
 *
 */
public class CfgCoverage {
	private List<CfgNode> nodeList;
	private CfgNode startNode;
	private List<CfgNode> exitList;
	
	private MethodNode methodNode;
	
	public CfgCoverage() {
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
		nodeList.add(node);
	}
	
	public void updateExitNodes() {
		for (CfgNode node : nodeList) {
			if (!node.hasBranch()) {
				exitList.add(node);
			}
		}
	}

	public void setMethodNode(MethodNode methodNode) {
		this.methodNode = methodNode;
	}
	
	public MethodNode getMethodNode() {
		return methodNode;
	}

	public CfgNode getNode(int nodeIdx) {
		if (nodeIdx >= nodeList.size() - 1) {
			return null;
		}
		return nodeList.get(nodeIdx);
	}

}
