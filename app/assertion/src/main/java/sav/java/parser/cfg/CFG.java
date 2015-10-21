package sav.java.parser.cfg;

import japa.parser.ast.Node;
import japa.parser.ast.stmt.BreakStmt;
import japa.parser.ast.stmt.ContinueStmt;
import japa.parser.ast.stmt.DoStmt;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.stmt.ForeachStmt;
import japa.parser.ast.stmt.SwitchStmt;
import japa.parser.ast.stmt.WhileStmt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StringUtils;
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
			merge(other);
			removeEdgesTo(getExit());
			for (CfgEdge otherExistIn : other.getInEdges(other.exit)) {
				addEdge(otherExistIn.clone(exit));
			}
		}
	}

	public void merge(CFG other) {
		for (CfgEdge thisExitIn : getInEdges(this.exit)) {
			for (CfgEdge otherEntryOut : other.getOutEdges(other.entry)) {
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
		if (other.uncompletedEdges != null) {
			uncompletedEdges = getUncompletedEdges();
			for (EdgeUnCompletedType key : other.uncompletedEdges.keySet()) {
				CollectionUtils.getListInitIfEmpty(
						getUncompletedEdges(), key).addAll(other.uncompletedEdges.get(key));
			}
		}
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
	
	public void appendLast(CfgNode node, boolean attachExit) {
		addNode(node);
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
		CollectionUtils.getListInitIfEmpty(getUncompletedEdges(), type).add(edge);
		addOutgoingEdge(edge);
	}
	
	private Map<EdgeUnCompletedType, List<CfgEdge>> getUncompletedEdges() {
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
	
	public List<CfgEdge> getUnCompleteEdge(EdgeUnCompletedType type) {
		if (uncompletedEdges == null) {
			return Collections.emptyList();
		}
		return CollectionUtils.nullToEmpty(uncompletedEdges.get(type));
	}
	
	public void solveBreak(String label) {
		if (!this.presentsLoop()) {
			return;
		}
		List<CfgEdge> breakEdges = getUnCompleteEdge(EdgeUnCompletedType.BREAK);
		for (Iterator<CfgEdge> it = breakEdges.iterator(); it.hasNext(); ) {
			CfgEdge edge = it.next();
			BreakStmt stmt = (BreakStmt) edge.getSource().getAstNode();
			if (StringUtils.equals(label, stmt.getId())) {
				// solve Break stmt
				edge.setDest(exit);
				addInCommingEdge(edge);
				it.remove();
			}
		}
	}

	public void solveContinue(String label) {
		if (! this.presentsLoop()) {
			return;
		}
		List<CfgEdge> continueEdges = getUnCompleteEdge(EdgeUnCompletedType.CONTINUE);
		if (continueEdges.isEmpty()) {
			return;
		}
		CfgNode decisionNode = getProperty(CfgProperty.LOOP_DECISION_NODE);
		for (Iterator<CfgEdge> it = continueEdges.iterator(); it.hasNext(); ) {
			CfgEdge edge = it.next();
			ContinueStmt stmt = (ContinueStmt) edge.getSource().getAstNode();
			if (StringUtils.equals(label, stmt.getId())) {
				// solve continue stmt
				edge.setDest(decisionNode);
				addInCommingEdge(edge);
				it.remove();
			}
		}
	}
	
	public void solveError(List<String> type, CFG catchBlk) {
		for (Iterator<CfgEdge> it = getUnCompleteEdge(EdgeUnCompletedType.EXCEPTION).iterator(); it.hasNext();) {
			CfgErrorEdge errorEdge = (CfgErrorEdge) it.next(); 
			if (type.contains(errorEdge.getErrorType())) {
				/* solve exception */
				for (CfgEdge catchEntryOut : catchBlk.getOutEdges(catchBlk.getEntry())) {
					addEdge(errorEdge.clone(catchEntryOut.getDest()));
				}
				removeEdge(errorEdge);
				it.remove();
			}
		}
	}
	
	public void addNode(CfgNode node) {
		super.addVertex(node);
	}
	
	private boolean presentsLoop() {
		Node astNode = getProperty(CfgProperty.AST_NODE);
		return astNode instanceof ForStmt ||
				astNode instanceof WhileStmt ||
				astNode instanceof ForeachStmt ||
				astNode instanceof DoStmt ||
				astNode instanceof SwitchStmt;
	}

	public static enum EdgeUnCompletedType {
		BREAK,
		CONTINUE,
		EXCEPTION
	}

}
