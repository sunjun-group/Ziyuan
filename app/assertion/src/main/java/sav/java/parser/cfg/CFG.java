package sav.java.parser.cfg;

import java.util.HashMap;
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
		addVertex(entry);
		addVertex(exit);
	}
	
	public void append(CFG that) {
		addVerties(that.getVertices());
		if (!that.isEmpty()) {
			for (CfgEdge thisExitIn : getInEdges(this.exit)) {
				for (CfgEdge thatEntryOut : that.getOutEdges(that.entry)) {
					addEdge(thisExitIn.clone(thatEntryOut.getDest()));
				}
			}
			removeEdgesTo(getExit());
			for (CfgEdge thatExistIn : that.getInEdges(that.exit)) {
				addEdge(thatExistIn.clone(exit));
			}
		}
	}
	
	public void append(CfgNode node) {
		for (CfgEdge exitIn : getInEdges(exit)) {
			moveEdgeTo(exitIn, node);
		}
	}
	
	public void moveEdgeTo(CfgEdge edge, CfgNode newTarget) {
		
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
		return size() == 2;
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
