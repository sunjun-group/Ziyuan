/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import tzuyu.engine.model.exception.TzRuntimeException;
import tzuyu.engine.utils.dfa.DfaGraph;
import tzuyu.engine.utils.dfa.DfaGraph.Edge;
import tzuyu.engine.utils.dfa.DfaGraph.Node;


/**
 * @author LLT
 * 
 */
public class SimpleDotUtils {
	
	public static DfaGraph readDot(InputStream in) {
		
		try {
//			IOUtils.readLines(input)
			in.read();
		} catch (IOException e) {
			// TODO nice to have
			e.printStackTrace();
		}
		return new DfaGraph();
	}
	
	public static void writeDot(DfaGraph dfaGraph, OutputStream out) {
		StringBuffer result = new StringBuffer();
		result.append("digraph automaton {\n\tcenter=true;\n\tsize=\"9,11\";\n");
		// Draw the initial state transition
		for (Edge edge : dfaGraph.getEdges()) {
			result.append("\t" + edge.getSource() + " -> " + edge.getTarget());
			result.append(" [label=\"");
			result.append(edge.getLabel());
			result.append("\"];\n");
		}
		for (Node node : dfaGraph.getNodes()) {
			result.append("\t" + node.getName());
			if (node.getAccepted() == null) {
				result.append(" [shape=point];");
			} else if (node.getAccepted()) {
				result.append(" [label=\"" + node.getName() + "\""
						+ ", shape=doublecircle];");
			} else {
				result.append(" [label=\"" + node.getName()
						+ "\", shape=circle, style=filled, color=red];\n");
			}
			result.append("\n");
		}

		result.append("}\n");
		try {
			out.write(result.toString().getBytes());
		} catch (IOException e) {
			throw new TzRuntimeException("Can not write .dot file");
		}
	}
	
}
