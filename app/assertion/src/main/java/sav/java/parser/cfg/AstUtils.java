/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.java.parser.cfg;

import japa.parser.ast.Node;

import java.util.List;

/**
 * @author LLT
 *
 */
public class AstUtils {
	private static int FAKE_NODE_POS = -1;
	
	public static void copyNodeProperties(Node from, Node to) {
		to.setBeginColumn(from.getBeginColumn());
		to.setEndColumn(from.getEndColumn());
		to.setBeginLine(from.getBeginLine());
		to.setEndLine(from.getEndLine());
	}
	
	public static boolean isFakeAstNode(Node node) {
		return node.getBeginLine() == FAKE_NODE_POS && node.getBeginColumn() == FAKE_NODE_POS;
	}
	
	public static void markNodeAsFake(Node node, Node refNode) {
		node.setBeginLine(FAKE_NODE_POS);
		node.setBeginColumn(FAKE_NODE_POS);
		node.setParentNode(refNode);
	}
	
	public static <T extends Node> void markNodesAsFake(List<T> nodes, Node refNode) {
		for (T node : nodes) {
			markNodeAsFake(node, refNode);
		}
	}
	
	public static Node getReferenceOfFakeNode(Node node) {
		return node.getParentNode();
	}

	public static String toString(Node node) {
		if (isFakeAstNode(node)) {
			return String.format("%s /* %s */ ", node.toString(),
							getReferenceOfFakeNode(node).toString());
		}
		return node.toString();
	}
}
