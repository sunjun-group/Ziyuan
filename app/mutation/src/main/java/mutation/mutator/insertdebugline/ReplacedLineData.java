/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package mutation.mutator.insertdebugline;

import japa.parser.ast.Node;

import java.util.List;

import sav.strategies.dto.ClassLocation;

/**
 * @author LLT
 *
 */
public class ReplacedLineData extends DebugLineData {
	private Node orgNode;
	private List<Node> replaceNodes;

	public ReplacedLineData(ClassLocation loc, Node orgNode,
			List<Node> replaceNodes) {
		super(loc);
		this.orgNode = orgNode;
		this.replaceNodes = replaceNodes;
	}
	
	@Override
	public List<Node> getReplaceNodes() {
		return replaceNodes;
	}

	@Override
	public Node getOrgNode() {
		return orgNode;
	}
}
