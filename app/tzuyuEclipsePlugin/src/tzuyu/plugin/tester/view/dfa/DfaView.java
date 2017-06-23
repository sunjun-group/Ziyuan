/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.view.dfa;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;

import tzuyu.engine.model.dfa.DFA;
import tzuyu.engine.utils.dfa.DfaGraph;
import tzuyu.engine.utils.dfa.DfaGraph.Edge;
import tzuyu.engine.utils.dfa.DfaGraph.Node;
import tzuyu.plugin.tester.view.TzViewPart;

/**
 * @author LLT
 * 
 */
public class DfaView extends TzViewPart {
	public static final String ID = "tzuyu.plugin.tester.view.dfa";
	private Composite contentPanel;
	private Graph graph;
	
	@Override
	public void createPartControl(Composite parent) {
		initContentPanel(parent);
	}
	
	private void initContentPanel(Composite parent) {
		contentPanel = new Composite(parent, SWT.NO_FOCUS);
		GridLayout layout = new GridLayout();
        contentPanel.setLayout(layout);
	}
	
	public void displayDFA(DFA dfa) {
		DfaGraph dfaGraph = dfa.toDfaGraph();
		displayDfaGraph(dfaGraph);
	}

	public void displayDfaGraph(DfaGraph dfaGraph) {
		resetGraph();
		drawDfaGraph(dfaGraph, contentPanel);
		graph.applyLayout();
		contentPanel.layout();
	}

	private void resetGraph() {
		if (graph != null && !graph.isDisposed()) {
			graph.dispose();
		}
		graph = new Graph(contentPanel, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
		graph.setLayout(new GridLayout());
		graph.setLayoutData(gd);
		graph.setScrollBarVisibility(FigureCanvas.AUTOMATIC);
	}
	
	private void drawDfaGraph(DfaGraph dfaGraph, Composite parent) {
		graph.setLayoutAlgorithm(new DfaLayoutAlgorithm(dfaGraph), true);
		// nodes
		Map<String, GraphNode> nodesMap = new HashMap<String, GraphNode>();
		for (Node nodeId : dfaGraph.getNodes()) {
			GraphNode node = new GraphNode(graph, SWT.NONE, nodeId.getName());
			nodesMap.put(nodeId.getName(), node);
			// init node
			if (nodeId.getAccepted() == null) {
				node.setText(null);
				node.setNodeStyle(ZestStyles.NONE);
			} else if (nodeId.getAccepted()) {
				node.setBorderWidth(2);
				node.setBorderColor(parent.getDisplay().getSystemColor(SWT.COLOR_GRAY));
			} else {
				node.setBackgroundColor(parent.getDisplay().getSystemColor(SWT.COLOR_RED));
			}
		}

		//edges
		for (Edge edge : dfaGraph.getEdges()) {
			DirectedGraphConnection conn = new DirectedGraphConnection(graph,  
					nodesMap.get(edge.getSource()), nodesMap.get(edge.getTarget()));
			conn.setText(edge.getLabel());
			if (!DfaGraph.INIT_NODE_NAME.equals(edge.getLabel())) {
				if (edge.isRecursiveEdge()) {
					conn.setCurveDepth(edge.getIdx() * DfaLayoutAlgorithm.DEPTH_PER_CONN);
				} else {
					int connNo = dfaGraph.getConnNoOfEdge(edge.name());
					if ((connNo % 2 > 0 && (edge.getIdx() != 1)) || connNo % 2 == 0) {
						int edgeDrawIdx = connNo % 2 == 0 ? edge.getIdx() + 1 : edge.getIdx();
						if (edgeDrawIdx % 2 == 0) {
							conn.setCurveDepth(edgeDrawIdx * DfaLayoutAlgorithm.DEPTH_PER_RECUR_CONN);
						} else {
							conn.setCurveDepth(- edgeDrawIdx * DfaLayoutAlgorithm.DEPTH_PER_RECUR_CONN + 10);
						}
					}
				}
			}
		}
	}
	
    @Override
    public void dispose() {
        if (contentPanel != null && !contentPanel.isDisposed()) {
        	contentPanel.dispose();
        }
        if (graph != null && !graph.isDisposed()) {
        	graph.dispose();
        }
        super.dispose();
    }

	@Override
	public void setFocus() {
		if (graph != null && !graph.isDisposed()) {
			graph.setFocus();
		}
	}

	/**
	 * This class is created to work around the bug in zest version 1.x
	 * bug: connection label will be relocated whenever it is selected in case the scrollbar is visible.
	 * this bug is already fixed from zest version 2.0
	 */
	private static class DirectedGraphConnection extends GraphConnection {

		public DirectedGraphConnection(Graph graphModel,
				GraphNode source, GraphNode destination) {
			super(graphModel, ZestStyles.CONNECTIONS_SOLID, source, destination);
			decorateConnection();
		}

		private void decorateConnection() {
			PolygonDecoration decoration = new PolygonDecoration();
			if (getLineWidth() < 3) {
				decoration.setScale(9, 3);
			} else {
				double logLineWith = getLineWidth() / 2.0;
				decoration.setScale(7 * logLineWith, 3 * logLineWith);
			}
			((PolylineConnection) getConnectionFigure()).setTargetDecoration(decoration);
		}
		
		@Override
		public void setCurveDepth(int depth) {
			super.setCurveDepth(depth);
			decorateConnection();
			
		}
		
		@Override
		public int getConnectionStyle() {
			return ZestStyles.CONNECTIONS_DIRECTED;
		}

	}
}
