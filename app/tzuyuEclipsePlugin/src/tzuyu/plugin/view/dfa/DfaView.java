/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.view.dfa;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import tzuyu.plugin.view.TzViewPart;

/**
 * @author LLT
 * 
 */
public class DfaView extends TzViewPart {
	private Label label;

	@Override
	public void createPartControl(Composite parent) {
		label = new Label(parent, 0);
		label.setText("Hello World");
		drawGraph(parent);
	}

	private void drawGraph(Composite parent) {
//		String dot = "digraph automaton {\n	center=true;\n	size=\"9,11\";\n	init -> 1 [label=\"init<>\"];\n	1 -> 0 [label=\"[true]StatelessBoundedStack.pop()\"];\n	1 -> 1 [label=\"[true]StatelessBoundedStack.push(Object)\"];\n	0 -> 0 [label=\"[true]StatelessBoundedStack.pop()\"];\n	0 -> 0 [label=\"[true]StatelessBoundedStack.push(Object)\"];\n	init [shape=point];\n	1 [label=\"1\", shape=doublecircle];\n	0 [label=\"0\", shape=circle, style=filled, color=red];\n}\n";
//		File dotFile = new File("D:/_1_Projects/Tzuyu/workspace/spencerxiao-tzuyu/spencerxiao-tzuyu-08d6e1ac1754/StatelessBoundedStack.dot");
//		DotImport dotimport = new DotImport(dotFile);
//		Graph graph = new Graph();
//		dotimport.into(graph);
//		ZestGraph zgraph = new ZestGraph(parent, SWT.NONE, graph);
	}

	@Override
	public void setFocus() {
		label.setFocus();
	}

}
