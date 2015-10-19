/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.java.parser.cfg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author LLT
 *
 */
public class CfgPrinter {
	private CFG cfg;
	private Set<CfgNode> visitedNodes;
	
	public CfgPrinter(CFG cfg) {
		this.cfg = cfg;
	}
	
	public void print() {
		visitedNodes = new HashSet<CfgNode>();
		print(cfg.getEntry());
	}

	private void print(CfgNode node) {
		if (visitedNodes.contains(node)) {
			return;
		}
		List<CfgNode> nextList = new ArrayList<CfgNode>();
		for (CfgEdge edge : cfg.getOutEdges(node)) {
			System.out.println(edge);
			String edgeLabel = edge.getLabel();
			if (!edgeLabel.isEmpty()){
				System.out.println("Edge: " + edgeLabel);
			}
			nextList.add(edge.getDest());
		}
		visitedNodes.add(node);
		for (CfgNode nextNode : nextList) {
			print(nextNode);
		}
	}
	
	public static void print(CFG cfg) {
		CfgPrinter printer = new CfgPrinter(cfg);
		printer.print();
	}
}
