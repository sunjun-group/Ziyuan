package learntest.cfg.graph;

import learntest.cfg.PropertiesContainer;
import sav.common.core.utils.StringUtils;

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
	
	public Edge<V> clone(V newDest) {
		Edge<V> newEdge = new Edge<V>(getSource(), newDest);
		newEdge.setProperties(getProperties());
		return newEdge;
	}
	
	@Override
	public String toString() {
		return String.format("%s  ---->  %s", source.toString(), dest);
	}
	
	public String getLabel() {
		return StringUtils.EMPTY;
	}
}
