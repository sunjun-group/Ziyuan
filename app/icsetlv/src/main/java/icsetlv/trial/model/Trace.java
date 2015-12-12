package icsetlv.trial.model;

import java.util.ArrayList;
import java.util.List;

import sav.strategies.dto.BreakPoint;

/**
 * This class stands for a trace for an execution
 * @author "linyun"
 *
 */
public class Trace {
	private int observingIndex = -1;
	
	private List<TraceNode> exectionList = new ArrayList<>();

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
}
