package sav.java.parser.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sav.common.core.utils.CollectionUtils;
import sav.java.parser.cfg.graph.Graph;

public class CFG extends Graph<CfgNode, CfgEdge>{
	private CfgEntryNode entry;
	private CfgExitNode exit;
	private Map<EdgeUnCompletedType, List<CfgEdge>> uncompletedEdges;
	
	public CFG(CfgEntryNode entry, CfgExitNode exit) {
		this.entry = entry;
		this.exit = exit;
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
			for (CfgEdge thisExitIn : getInEdges(this.exit)) {
				for (CfgEdge otherEntryOut : other.getOutEdges(other.entry)) {
					addEdge(thisExitIn.clone(otherEntryOut.getDest()));
				}
			}
			removeEdgesTo(getExit());
			for (CfgEdge otherExistIn : other.getInEdges(other.exit)) {
				addEdge(otherExistIn.clone(exit));
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
				if (!(edge.getSource().equals(other.getEntry()) || edge.getDest()
						.equals(other.getExit()))) {
					addEdge(edge);
				}
			}
		}
	}
	
	public void appendLast(CfgNode node, boolean attachExit) {
		addVertex(node);
		for (Iterator<CfgEdge> it = getInEdges(exit).iterator(); it.hasNext();) {
			CfgEdge exitIn = it.next();
			List<CfgEdge> edges = getInNeighbourhood().get(node);
			if (edges == null) {
				edges = new ArrayList<CfgEdge>();
				getInNeighbourhood().put(node, edges);
			}
			edges.add(exitIn);
			exitIn.setDest(node);
			it.remove();
		}
		if (attachExit) {
			addEdge(node, getExit());
		}
	}
	
	public void addEdge(CfgNode source, CfgNode dest) {
		addEdge(new CfgEdge(source, dest));
	}
	
	public void addUncompletedEdge(EdgeUnCompletedType type, CfgEdge edge) {
		CollectionUtils.getListInitIfEmpty(getUncompletedEdges(), type).add(
				edge);
	}
	
	public Map<EdgeUnCompletedType, List<CfgEdge>> getUncompletedEdges() {
		if (uncompletedEdges == null) {
			uncompletedEdges = new HashMap<EdgeUnCompletedType, List<CfgEdge>>();
		}
		return uncompletedEdges;
	}

	public boolean isEmpty() {
		return size() == 0;
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

	public static enum EdgeUnCompletedType {
		BREAK,
		CONTINUE,
		EXCEPTION
	}
}
