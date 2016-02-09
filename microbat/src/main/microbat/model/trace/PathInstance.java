package microbat.model.trace;

import java.util.ArrayList;
import java.util.List;

public class PathInstance {
	private TraceNode startNode;
	private TraceNode endNode;
	private List<SourceLine> lineTrace;
	
	public PathInstance(TraceNode startNode, TraceNode endNode) {
		super();
		this.startNode = startNode;
		this.endNode = endNode;
		
		this.setLineTrace(generateSourceLineTrace());
	}
	
	private List<SourceLine> generateSourceLineTrace(){
		TraceNode node = startNode;
		List<SourceLine> lineTrace = new ArrayList<>();
		while(node.getOrder() < endNode.getOrder()){
			SourceLine sourceLine = new SourceLine(node.getClassName(), node.getLineNumber());
			lineTrace.add(sourceLine);
		}
		
		return lineTrace;
	}
	
	public TraceNode getStartNode() {
		return startNode;
	}
	public void setStartNode(TraceNode startNode) {
		this.startNode = startNode;
	}
	public TraceNode getEndNode() {
		return endNode;
	}
	public void setEndNode(TraceNode endNode) {
		this.endNode = endNode;
	}
	
	public List<SourceLine> getLineTrace() {
		return lineTrace;
	}

	public void setLineTrace(List<SourceLine> lineTrace) {
		this.lineTrace = lineTrace;
	}

	public class SourceLine{
		private String className;
		private int lineNumber;
		public SourceLine(String className, int lineNumber) {
			super();
			this.className = className;
			this.lineNumber = lineNumber;
		}
		@Override
		public String toString() {
			return "SourceLine [className=" + className + ", lineNumber="
					+ lineNumber + "]";
		}
		public String getClassName() {
			return className;
		}
		public void setClassName(String className) {
			this.className = className;
		}
		public int getLineNumber() {
			return lineNumber;
		}
		public void setLineNumber(int lineNumber) {
			this.lineNumber = lineNumber;
		}
		
	}
}
