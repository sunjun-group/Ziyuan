package microbat.model.trace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import microbat.model.BreakPoint;

/**
 * This class stands for a trace for an execution
 * @author Yun Lin
 *
 */
public class Trace {
	private int observingIndex = -1;
	
	private List<TraceNode> exectionList = new ArrayList<>();
	/**
	 * tracking which steps read/write what variables, and what variables are read/written by which steps.
	 * key is the variable ID, and value is the entry containing all the steps reading/writing the corresponding
	 * variable.
	 */
	private Map<String, StepVariableRelationEntry> stepVariableTable = new HashMap<>();

	public List<TraceNode> getExectionList() {
		return exectionList;
	}

	public void setExectionList(List<TraceNode> exectionList) {
		this.exectionList = exectionList;
	}
	
	public void addTraceNode(TraceNode node){
		this.exectionList.add(node);
	}
	
	public int size(){
		return this.exectionList.size();
	}
	
	public List<TraceNode> getTopLevelNodes(){
		List<TraceNode> topList = new ArrayList<>();
		for(TraceNode node: this.exectionList){
			if(node.getInvocationParent() == null){
				topList.add(node);
			}
		}
		
		return topList;
	}
	
	public TraceNode getLastestNode(){
		int len = size();
		if(len > 0){
			return this.exectionList.get(len-1);
		}
		else{
			return null;
		}
	}
	
	public void resetObservingIndex(){
		this.observingIndex = -1;
	}
	
	public int getObservingIndex() {
		return observingIndex;
	}

	public void setObservingIndex(int observingIndex) {
		this.observingIndex = observingIndex;
	}
	
	public int searchBackwardTraceNode(String expression){
		int resultIndex = -1;
		
		for(int i=observingIndex-1; i>=0; i--){
			TraceNode node = exectionList.get(i);
			BreakPoint breakPoint = node.getBreakPoint();
			String className = breakPoint.getClassCanonicalName();
			int lineNumber = breakPoint.getLineNo();
			
			String exp = combineTraceNodeExpression(className, lineNumber);
			if(exp.equals(expression)){
				resultIndex = i;
				break;
			}
		}
		
		if(resultIndex != -1){
			this.observingIndex = resultIndex;
		}
		return resultIndex;
	}

	public int searchForwardTraceNode(String expression){
		int resultIndex = -1;
		
		for(int i=observingIndex+1; i<exectionList.size(); i++){
			TraceNode node = exectionList.get(i);
			BreakPoint breakPoint = node.getBreakPoint();
			String className = breakPoint.getClassCanonicalName();
			int lineNumber = breakPoint.getLineNo();
			
			String exp = combineTraceNodeExpression(className, lineNumber);
			if(exp.equals(expression)){
				resultIndex = i;
				break;
			}
		}
		
		if(resultIndex != -1){
			this.observingIndex = resultIndex;			
		}
		return resultIndex;
	}
	
	private String combineTraceNodeExpression(String className, int lineNumber){
		String exp = className + " line:" + lineNumber;
		return exp;
	}

	public void conductStateDiff() {
		for(int i=0; i<this.exectionList.size(); i++){
			TraceNode node = this.exectionList.get(i);
			node.conductStateDiff();
		}
		
	}

	public void constructDomianceRelation() {
		for(String varID: this.stepVariableTable.keySet()){
			StepVariableRelationEntry entry = this.stepVariableTable.get(varID);
			List<TraceNode> producers = entry.getProducers();
			List<TraceNode> consumers = entry.getConsumers();
			
			if(varID.contains("flag")){
				System.currentTimeMillis();
			}
			
			if(producers.isEmpty()){
				System.err.println("there is no producer for variable " + entry.getAliasVariables());
			}
			
			Collections.sort(producers, new TraceNodeComparator());
			Collections.sort(consumers, new TraceNodeComparator());
			
			int readingCursor = 0;
			
			for(int i=0; i<producers.size(); i++){
				TraceNode prevWritingNode = producers.get(i);
				TraceNode postWritingNode = null; 
				if(i+1 < producers.size()){
					postWritingNode = producers.get(i+1);					
				}
				
				if(readingCursor >= consumers.size()){
					break;
				}
				TraceNode readingNode = consumers.get(readingCursor);
				int readingOrder = readingNode.getOrder();
				
				if(postWritingNode != null){
					int preOrder = prevWritingNode.getOrder();
					int postOrder = postWritingNode.getOrder();
					
					if(readingCursor<consumers.size()){
						while(preOrder<=readingOrder && readingOrder<postOrder){
							List<String> varIDs = new ArrayList<>();
							varIDs.add(varID);
							
							prevWritingNode.addDominatee(readingNode, varIDs);
							readingNode.addDominator(prevWritingNode, varIDs);
							
							readingCursor++;
							if(readingCursor >= consumers.size()){
								break;
							}
							
							readingNode = consumers.get(readingCursor);
							readingOrder = readingNode.getOrder();
						}
					}
					else{
						break;
					}
				}
				else{
					while(readingCursor<consumers.size()){
						List<String> varIDs = new ArrayList<>();
						varIDs.add(varID);
						
						prevWritingNode.addDominatee(readingNode, varIDs);
						readingNode.addDominator(prevWritingNode, varIDs);
						
						readingCursor++;
						if(readingCursor >= consumers.size()){
							break;
						}
						
						readingNode = consumers.get(readingCursor);
						readingOrder = readingNode.getOrder();
					}
				}
			}
		}
		
	}

	public Map<String, StepVariableRelationEntry> getStepVariableTable() {
		return stepVariableTable;
	}
	
	
}
