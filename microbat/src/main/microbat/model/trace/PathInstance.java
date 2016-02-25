package microbat.model.trace;

import java.util.ArrayList;

import microbat.model.value.VarValue;
import microbat.model.variable.Variable;
import microbat.util.Settings;

public class PathInstance {
	private TraceNode startNode;
	private TraceNode endNode;
	private ArrayList<SourceLine> lineTrace;
	
	public PathInstance(){}
	
	public PathInstance(TraceNode node1, TraceNode node2) {
		
		if(node1.getOrder() < node2.getOrder()){
			this.startNode = node1;
			this.endNode = node2;			
		}
		else{
			this.startNode = node2;
			this.endNode = node1;
		}
		
		this.setLineTrace(generateSourceLineTrace());
	}
	
	public boolean isPotentialCorrect() {
		if(startNode.getReadVarCorrectness(Settings.interestedVariables, false)==TraceNode.READ_VARS_CORRECT && 
				endNode.getReadVarCorrectness(Settings.interestedVariables, false)==TraceNode.READ_VARS_CORRECT){
			return true;
		}
		else if(startNode.getReadVarCorrectness(Settings.interestedVariables, false)!=TraceNode.READ_VARS_CORRECT && 
				endNode.getReadVarCorrectness(Settings.interestedVariables, false)!=TraceNode.READ_VARS_CORRECT){
			return true;
		}
		
		return false;
	}
	
	public String getPathKey(){
		StringBuffer buffer = new StringBuffer();
		for(SourceLine line: lineTrace){
			buffer.append(line.toString());
			buffer.append(";");
		}
		
		return buffer.toString();
	}
	
	private ArrayList<SourceLine> generateSourceLineTrace(){
		TraceNode node = startNode;
		ArrayList<SourceLine> lineTrace = new ArrayList<>();
		while(node.getOrder() <= endNode.getOrder()){
			SourceLine sourceLine = new SourceLine(node.getClassName(), node.getLineNumber());
			lineTrace.add(sourceLine);
			
			node = node.getStepInNext();
			if(node == null){
				break;
			}
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
	
	public ArrayList<SourceLine> getLineTrace() {
		return lineTrace;
	}

	public void setLineTrace(ArrayList<SourceLine> lineTrace) {
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
			return className + ":" + lineNumber;
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
		
		@Override
		public boolean equals(Object obj){
			if(obj != null){
				if(obj instanceof SourceLine){
					SourceLine line = (SourceLine)obj;
					return line.getLineNumber()==lineNumber && line.getClassName().equals(className);
				}
			}
			
			return false;
		}
	}

	/**
	 * Find the variable which causes the jump of label path of the pattern.
	 */
	public Variable findCausingVar(){
		Variable causingVariable = null;
		TraceNode producer = getStartNode();
		for(VarValue readVar: getEndNode().getReadVariables()){
			for(VarValue writtenVar: producer.getWrittenVariables()){
				if(writtenVar.getVarID().equals(readVar.getVarID())){
					causingVariable = readVar.getVariable();
					break;
				}
			}
		}
		
		return causingVariable;
	}
}
