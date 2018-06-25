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

import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.SignatureUtils;
import sav.common.core.utils.TextFormatUtils;
import sav.strategies.dto.BreakPoint;

/**
 * @author LLT
 *
 */
public class CFG {
	private String id;
	private List<CfgNode> nodeList;
	private CfgNode startNode;
	private List<CfgNode> exitList;
	
	private CfgNode firstDeisionNode;
	
	private MethodNode methodNode;
	private String className;
	private BreakPoint entryPoint;
	
	public CFG(String className, MethodNode methodNode) {
		this.methodNode = methodNode;
		this.className = className;
		String fullMethodName = ClassUtils.toClassMethodStr(className, methodNode.name);
		String methodId = SignatureUtils.createMethodNameSign(fullMethodName, methodNode.desc);
		this.id = methodId;
		nodeList = new ArrayList<CfgNode>();
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
	
	@Override
	public String toString () {
		return "Cfg[nodeList=" + TextFormatUtils.printCol(nodeList, "\n") + ", \nstartNode=" + startNode + ",\n exitList=" + exitList
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
	
	public int size() {
		return nodeList.size();
	}
	
	public List<CfgNode> getExitList() {
		if (exitList == null) {
			exitList = new ArrayList<>();
			for (CfgNode node : getNodeList()) {
				if (node.isLeaf()) {
					exitList.add(node);
				}
			}
		}
		return exitList;
	}
	
	public CfgNode getFirstDecisionNode() {
		if (!getDecisionNodes().isEmpty() && firstDeisionNode == null) {
			CfgNode node = startNode;
			while (node != null) {
				if (node.isDecisionNode()) {
					firstDeisionNode = node;
					break;
				}
				node = node.getNext();
			}
		}
		return firstDeisionNode;
	}
	
	public BreakPoint getEntryPoint() {
		if (entryPoint == null) {
			entryPoint = new BreakPoint(className, startNode.getLine());
		}
		return entryPoint;
	}
}
