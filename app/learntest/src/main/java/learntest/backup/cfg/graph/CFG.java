package learntest.backup.cfg.graph;


import java.util.HashSet;
import java.util.List;

import japa.parser.ast.stmt.AssertStmt;
import learntest.backup.cfg.graph.CfgNode.Type;
import learntest.backup.cfg.graph.graph.Graph;

public class CFG extends Graph<CfgNode, CfgEdge> {

	// TODO: please finish this class
	private CfgEntryNode entry;
	private CfgExitNode exit;
	
	public CFG(CfgEntryNode entry, CfgExitNode exit) {
		this.entry = entry;
		this.exit = exit;
	}
	
	public boolean isEmpty() {
		return size() == 0;
	}
	
	public boolean isLoop(int lineNo) {
		//TODO please fill in this method to determine whether the node at line lineNo is a loop node
		return false;
	}

	public CfgEntryNode getEntry() {
		return entry;
	}

	public void setEntry(CfgEntryNode entry) {
		this.entry = entry;
	}

	public CfgExitNode getExit() {
		return exit;
	}

	public void setExit(CfgExitNode exit) {
		this.exit = exit;
	}
	
	public void addNode(CfgNode node) {
		super.addVertex(node);
	}
	
	public void addEdge(CfgNode source, CfgNode dest) {
		addEdge(new CfgEdge(source, dest));
	}
	

	public List<CfgEdge> getExitInEdges() {
		return getInEdges(getExit());
	}
	
	public List<CfgEdge> getEntryOutEdges() {
		return getOutEdges(getEntry());
	}
	
	/**
	 * join another cfg into this.
	 * this exit will be merged with that entry.out.
	 * and that exit will become this exit.
	 * */
	public void append(CFG other) {
		/* append vertices */
		addCFG(other);
		if (!other.isEmpty()) {
			merge(other);
			removeEdgesTo(getExit());
			for (CfgEdge otherExistIn : other.getExitInEdges()) {
				addEdge(otherExistIn.clone(exit));
			}
		}
	}

	public void merge(CFG other) {
		for (CfgEdge thisExitIn : getExitInEdges()) {
			for (CfgEdge otherEntryOut : other.getEntryOutEdges()) {
				addEdge(thisExitIn.clone(otherEntryOut.getDest()));
			}
		}
	}
	
	/**
	 * add all vertices and their edges from other,
	 * excludes entry end exit.
	 * */
	public void addCFG(CFG other) {
		addVerties(other.getVertices());
		addEdges(other);
	}
	
	public void addEdges(CFG other) {
		for (CfgNode vertex : other.getVertices()) {
			for (CfgEdge edge : other.getOutEdges(vertex)) {
				if (!(other.getEntry().equals(edge.getSource()) || 
						other.getExit().equals(edge.getDest()))) {
					addEdge(edge);
				}
			}
		}
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		appendStr(entry, sb, new HashSet<CfgNode>());
		return sb.toString();
	}
	
	private void appendStr(CfgNode node, StringBuilder sb, HashSet<CfgNode> visitedNodes) {
		if (visitedNodes.contains(node)) {
			return;
		}
		visitedNodes.add(node);
		for (CfgEdge edge : getOutEdges(node)) {
			sb.append(edge).append("\n");
			appendStr(edge.getDest(), sb , visitedNodes);
		}
	}
	
	public void getDecisionNode(CfgNode node, List<CfgNode> nodeList, HashSet<CfgNode> visitedNodes) {
		if (node == null || visitedNodes.contains(node)) {
			return;
		}
		visitedNodes.add(node);
		
		if (node.getType() == Type.DECISIONS && !nodeList.contains(node) &&
				!(node.getAstNode() instanceof AssertStmt)) {
			nodeList.add(node);
		}
		
		for (CfgEdge edge : getOutEdges(node)) {
			getDecisionNode(edge.getDest(), nodeList, visitedNodes);
		}
	}
	
	
}
