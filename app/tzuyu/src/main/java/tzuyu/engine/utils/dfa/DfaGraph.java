/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.utils.dfa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;

import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 * 
 */
public class DfaGraph {
	public static final String INIT_NODE_NAME = "init";
	public static final String INIT_NODE_LABEL = "init<>";
	
	private LinkedList<Node> nodes;
	private List<Edge> edges;
	private Map<String, Integer> connNoMap; 
	private String firstNode;

	public DfaGraph() {
		nodes = new LinkedList<DfaGraph.Node>();
		nodes.add(getInitNode());
		edges = new ArrayList<DfaGraph.Edge>();
		connNoMap = new HashMap<String, Integer>();
	}
	
	public void setInit(String init) {
		edges.add(new Edge(INIT_NODE_NAME, init, INIT_NODE_LABEL));
		firstNode = init;
	}
	
	public String getFirstNode() {
		return firstNode;
	}
	
	public List<Node> getNodes() {
		return nodes;
	}
	
	public List<Edge> getEdges() {
		return edges;
	}
	
	public void addNode(String nodeLb, boolean accepted) {
		nodes.add(new Node(nodeLb, accepted));
	}

	public void addEdge(String source, String target, String label) {
		edges.add(new Edge(source, target, label));
	}
	
	private Node getInitNode() {
		return new Node(INIT_NODE_NAME, null);
	}
	
	public int getConnNum(int i) {
		String nodeLb = nodes.get(i).getName();
		int num = 0;
		for (Edge edge : edges) {
			if (edge.getTarget().equals(nodeLb)) {
				num ++;
			}
		}
		return num;
	}
	
	public void commit() {
		for (Edge edge : edges) {
			String edgeName = edge.name();
			Integer connNo = (Integer) ObjectUtils.defaultIfNull(
					connNoMap.get(edgeName), 0) + 1;
			edge.idx = connNo;
			connNoMap.put(edgeName, connNo);
		}
	}
	
	public int getConnNoOfEdge(String name) {
		return connNoMap.get(name);
	}

	
	public static class Node {
		private String name;
		private Boolean accepted;
		
		
		public Node(String name, Boolean accepted) {
			this.name = name;
			this.accepted = accepted;
		}
		
		public String getName() {
			return name;
		}

		public Boolean getAccepted() {
			return accepted;
		}
	}
	
	public static class Edge {
		private String source;
		private String target;
		private String label;
		private int idx;
		
		public Edge(String source, String target, String label) {
			this.source = source;
			this.target = target;
			this.label = label;
		}
		
		public boolean isRecursiveEdge() {
			return source.equals(target);
		}
		
		public String getSource() {
			return source;
		}
		
		public String getTarget() {
			return target;
		}
		
		public String getLabel() {
			return label;
		}
		
		public int getIdx() {
			return idx;
		}
		
		public String name() {
			return StringUtils.join("_", source, target);
		}
	}

}
