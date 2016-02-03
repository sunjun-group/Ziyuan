package microbat.model.trace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import microbat.model.BreakPoint;
import microbat.model.UserInterestedVariables;
import microbat.model.value.VarValue;
import microbat.model.variable.Variable;

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
			String className = breakPoint.getDeclaringCompilationUnitName();
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
			String className = breakPoint.getDeclaringCompilationUnitName();
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
			
			if(producers.isEmpty()){
				System.err.println("there is no producer for variable " + entry.getAliasVariables());
			}
			
			Collections.sort(producers, new TraceNodeComparator());
			Collections.sort(consumers, new TraceNodeComparator());
			
			
			int readingCursor = 0;
			System.currentTimeMillis();
			
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
				
				while(readingOrder <= prevWritingNode.getOrder()){
					System.out.println("WARNING in Trace.constructDominanceRelation(): the consumer's order appears "
							+ "to be smaller than producer's order for variable " + entry.getVarID() + ": " + entry.getAliasVariables());
					
					readingCursor++;
					if(readingCursor >= consumers.size()){
						break;
					}
					readingNode = consumers.get(readingCursor);
					readingOrder = readingNode.getOrder();
				}
				
				
				if(postWritingNode != null){
					int preOrder = prevWritingNode.getOrder();
					int postOrder = postWritingNode.getOrder();
					
					if(preOrder == 158){
						System.currentTimeMillis();
					}
					
					if(readingCursor<consumers.size()){
						
						
						while(preOrder<readingOrder && readingOrder<=postOrder){
							
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

	public TraceNode findLastestExceptionNode() {
		for(int i=0; i<exectionList.size(); i++){
			TraceNode lastestNode = exectionList.get(exectionList.size()-1-i);
			if(lastestNode.isException()){
				return lastestNode;
			}
		}
		
		return null;
	}

	/**
	 * TODO unfinished
	 * @param varID
	 * @param startNodeOrder
	 * @return
	 */
	public TraceNode findBackwardSupiciousNode(
			UserInterestedVariables interestedVariables, int startNodeOrder) {
		if(startNodeOrder < 1){
			return null;
		}
		
		for(int i=startNodeOrder-1; i>=1; i--){
			/**	order start from 1 */
			TraceNode node = this.exectionList.get(i-1);
			for(String varID: interestedVariables.getVarIDs()){
				for(VarValue varValue: node.getWrittenVariables()){
					if(varValue.getVarID().equals(varID)){
						return node;
					}
				}
			}
		}
		
		return null;
	}

	public TraceNode findLastestNodeDefiningPrimitiveVariable(String varID) {
		for(int i=exectionList.size()-2; i>=0; i--){
			TraceNode node = exectionList.get(i);
			for(VarValue var: node.getWrittenVariables()){
				String writtenVarID = var.getVarID();
				if(writtenVarID.contains(":")){
					String simpleVarID = writtenVarID.substring(0, writtenVarID.indexOf(":"));
					if(simpleVarID.equals(varID)){
						return node;
					}
				}
			}
		}
		
		return null;
	}

	/**
	 * Note that, if a variable is a primitive type, I cannot retrieve its head address, therefore, I use the static approach
	 * to uniquely identify a variable, i.e., variable ID. Please refer to {@link microbat.model.variable.Variable#varID} for details.
	 * <br>
	 * <br>
	 * However, in order to save the parsing efficiency, the ID of variables of primitive types does not have the suffix of ":order".
	 * That's why I need to do the mapping from state variables to read/written variables.
	 * 
	 * @param varID
	 * @param order
	 * @return
	 */
	public String findTrueIDFromStateVariable(String varID, int order) {
		if(Variable.isPrimitiveVariable(varID)){
			for(int i=order; i>=1; i--){
				TraceNode node = this.exectionList.get(i);
				String trueID = findTrueID(node.getReadVariables(), varID); 
				
				if(trueID != null){
					return trueID;
				}
				else{
					if(i != order){
						trueID = findTrueID(node.getReadVariables(), varID);
						if(trueID != null){
							return trueID;
						}
					}					
				}
			}
			return null;
		}
		else{
			return varID;
		}
	}
	
	private String findTrueID(List<VarValue> readOrWriteVars, String varID){
		for(VarValue var: readOrWriteVars){
			String ID = var.getVarID();
			if(Variable.isPrimitiveVariable(ID)){
				String concanateID = ID.substring(0, ID.indexOf(":"));
				if(concanateID.equals(varID)){
					return ID;
				}
			}
		}
		
		return null;
	}
}
