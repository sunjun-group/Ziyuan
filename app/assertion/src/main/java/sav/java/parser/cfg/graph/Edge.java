package sav.java.parser.cfg.graph;

import sav.java.parser.cfg.PropertiesContainer;

public class Edge<V> extends PropertiesContainer {
	private V source;
	private V dest;
	
	public Edge(V source, V dest) {
		this.source = source;
		this.dest = dest;
	}
	
	public void setSource(V source) {
		this.source = source;
	}

	public void setDest(V dest) {
		this.dest = dest;
	}

	public V getSource() {
		return source;
	}

	public V getDest() {
		return dest;
	}
}
