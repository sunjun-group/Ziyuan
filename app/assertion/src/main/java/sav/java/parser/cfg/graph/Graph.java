package sav.java.parser.cfg.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sav.common.core.utils.CollectionUtils;
import sav.java.parser.cfg.PropertiesContainer;

public class Graph<V, E extends Edge<V>> extends PropertiesContainer {
	private List<V> vertices;
	private Map<V, List<E>> inNeighbourhood;
	private Map<V, List<E>> outNeighbourhood;
	
	public Graph() {
		vertices = new ArrayList<V>();
		inNeighbourhood = new HashMap<V, List<E>>();
		outNeighbourhood = new HashMap<V, List<E>>();
	}

	public List<E> getOutEdges(V vertex) {
		return getEdges(outNeighbourhood, vertex);
	}
	
	public List<E> getInEdges(V vertex) {
		return getEdges(inNeighbourhood, vertex);
	}
	
	public void addVertex(V vertex) {
		vertices.add(vertex);
	}
	
	public void addVerties(List<V> newVertices) {
		vertices.addAll(newVertices);
	}
	
	public void addEdge(E edge) {
		CollectionUtils.getListInitIfEmpty(outNeighbourhood, edge.getSource())
				.add(edge);
		CollectionUtils.getListInitIfEmpty(inNeighbourhood, edge.getDest())
				.add(edge);
	}
	
	public void removeEdge(E edge) {
		outNeighbourhood.get(edge.getSource()).remove(edge);
		inNeighbourhood.get(edge.getDest()).remove(edge);
	}
	
	@SuppressWarnings("unchecked")
	public void remove(V vertex) {
		/* isolate node, and create path from its predecessors to its successors */
		for (E inEdge : getInEdges(vertex)) {
			for (E outEdge : getOutEdges(vertex)) {
				addEdge((E) inEdge.clone(outEdge.getDest()));
			}
		}
		/* remove node, and all its edges */
		Iterator<E> it;
		for (it = getInEdges(vertex).iterator(); it.hasNext();) {
			E edge = it.next();
			getOutEdges(edge.getSource()).remove(edge);
			it.remove();
		}
		for (it = getOutEdges(vertex).iterator(); it.hasNext();) {
			E edge = it.next();
			getInEdges(edge.getDest()).remove(edge);
			it.remove();
		}
	}
	
	public void remove(V... vertices) {
		for (V vertex : vertices) {
			remove(vertex);
		}
	}
	
	public void removeEdgesTo(V vertex) {
		for (E edge : CollectionUtils.nullToEmpty(inNeighbourhood.get(vertex))) {
			getOutEdges(edge.getSource()).remove(edge);
		}
		inNeighbourhood.remove(vertex);
	}
	
	public void removeEdgesFrom(V vertex) {
		for (E edge : CollectionUtils.nullToEmpty(outNeighbourhood.get(vertex))) {
			getInEdges(edge.getDest()).remove(edge);
		}
		outNeighbourhood.remove(vertex);
	}
	
	/**
	 * edge: source --> dest
	 * remove:   edge from [--> dest]
	 * add: edge to [--> newDest]
	 * modify edge 
	 */
	public void moveEdgeTo(E edge, V newDest) {
		V oldDest = edge.getDest();
		inNeighbourhood.get(oldDest).remove(edge);
		CollectionUtils.getListInitIfEmpty(inNeighbourhood, newDest).add(edge);
		edge.setDest(newDest);
	}
	
	private List<E> getEdges(Map<V, List<E>> neighbourhood, V vertex) {
		List<E> edges = null;
		if (neighbourhood != null) {
			edges = neighbourhood.get(vertex);
		}
		if (edges == null) {
			edges = Collections.emptyList();
		}
		return edges;
	}
	
	public List<V> getVertices() {
		return vertices;
	}

	public void setVertices(List<V> vertices) {
		this.vertices = vertices;
	}

	public Map<V, List<E>> getInNeighbourhood() {
		return inNeighbourhood;
	}

	public void setInNeighbourhood(Map<V, List<E>> inNeighbourhood) {
		this.inNeighbourhood = inNeighbourhood;
	}

	public Map<V, List<E>> getOutNeighbourhood() {
		return outNeighbourhood;
	}

	public void setOutNeighbourhood(Map<V, List<E>> outNeighbourhood) {
		this.outNeighbourhood = outNeighbourhood;
	}

	public int size() {
		return vertices.size();
	}
}
