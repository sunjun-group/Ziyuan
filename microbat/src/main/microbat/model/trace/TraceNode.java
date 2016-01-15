package microbat.model.trace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import microbat.algorithm.graphdiff.GraphDiff;
import microbat.algorithm.graphdiff.HierarchyGraphDiffer;
import microbat.model.BreakPoint;
import microbat.model.BreakPointValue;
import microbat.model.variable.Variable;

public class TraceNode {
	
	private BreakPoint breakPoint;
	private BreakPointValue programState;
	private BreakPointValue afterStepInState;
	private BreakPointValue afterStepOverState;
	
	private List<GraphDiff> consequences;
	
	private Map<TraceNode, List<Variable>> dominator = new HashMap<>();
	private Map<TraceNode, List<Variable>> dominatee = new HashMap<>();
	
	/**
	 * the order of this node in the whole trace
	 */
	private int order;
	
	/**
	 * indicate whether this node has been marked correct/incorrect by user
	 */
	private Boolean markedCorrrect;
	
	
	private TraceNode stepInNext;
	private TraceNode stepInPrevious;
	
	private TraceNode stepOverNext;
	private TraceNode stepOverPrevious;
	
	private List<TraceNode> invocationChildren = new ArrayList<>();
	private TraceNode invocationParent;

	public TraceNode(BreakPoint breakPoint, BreakPointValue programState, int order) {
		super();
		this.breakPoint = breakPoint;
		this.programState = programState;
		this.order = order;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + order;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TraceNode other = (TraceNode) obj;
		if (order != other.order)
			return false;
		return true;
	}

	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(getClassName());
		buffer.append(": line ");
		buffer.append(getLineNumber());
		
		String methodName = this.breakPoint.getMethodName();
		if(methodName != null){
			buffer.append(" in ");
			buffer.append(methodName);
			buffer.append("(...)");
		}
	
		return buffer.toString();
	}
	
	public String getClassName(){
		return this.breakPoint.getClassCanonicalName();
	}
	
	public int getLineNumber(){
		return this.breakPoint.getLineNo();
	}

	public BreakPoint getBreakPoint() {
		return breakPoint;
	}

	public void setBreakPoint(BreakPoint breakPoint) {
		this.breakPoint = breakPoint;
	}

	public BreakPointValue getProgramState() {
		return programState;
	}

	public void setProgramState(BreakPointValue programState) {
		this.programState = programState;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public Boolean getMarkedCorrrect() {
		return markedCorrrect;
	}

	public void setMarkedCorrrect(Boolean markedCorrrect) {
		this.markedCorrrect = markedCorrrect;
	}

	public BreakPointValue getAfterState() {
		if(this.afterStepOverState != null){
			return this.afterStepOverState;
		}
		else{
			return afterStepInState;			
		}
		
	}
//
//	public void setAfterState(BreakPointValue afterState) {
//		this.afterStepInState = afterState;
//	}

	public TraceNode getStepInNext() {
		return stepInNext;
	}

	public void setStepInNext(TraceNode stepInNext) {
		this.stepInNext = stepInNext;
	}

	public TraceNode getStepInPrevious() {
		return stepInPrevious;
	}

	public void setStepInPrevious(TraceNode stepInPrevious) {
		this.stepInPrevious = stepInPrevious;
	}

	public TraceNode getStepOverNext() {
		return stepOverNext;
	}

	public void setStepOverNext(TraceNode stepOverNext) {
		this.stepOverNext = stepOverNext;
	}

	public TraceNode getStepOverPrevious() {
		return stepOverPrevious;
	}

	public void setStepOverPrevious(TraceNode stepOverPrevious) {
		this.stepOverPrevious = stepOverPrevious;
	}

	public List<TraceNode> getInvocationChildren() {
		return invocationChildren;
	}

	public void setInvocationChildren(List<TraceNode> invocationChildren) {
		this.invocationChildren = invocationChildren;
	}

	public void addInvocationChild(TraceNode node){
		this.invocationChildren.add(node);
	}

	public TraceNode getInvocationParent() {
		return invocationParent;
	}

	public void setInvocationParent(TraceNode invocationParent) {
		this.invocationParent = invocationParent;
	}

	public BreakPointValue getAfterStepInState() {
		return afterStepInState;
	}

	public void setAfterStepInState(BreakPointValue afterStepInState) {
		this.afterStepInState = afterStepInState;
	}

	public BreakPointValue getAfterStepOverState() {
		return afterStepOverState;
	}

	public void setAfterStepOverState(BreakPointValue afterStepOverState) {
		this.afterStepOverState = afterStepOverState;
	}

	public List<GraphDiff> getConsequences() {
		return consequences;
	}

	public void setConsequences(List<GraphDiff> consequences) {
		this.consequences = consequences;
	}

	public void conductStateDiff() {
		BreakPointValue nodeBefore = getProgramState();
		BreakPointValue nodeAfter = getAfterState();
		
//		if(getOrder() == 6){
//			System.currentTimeMillis();
//		}
		
		HierarchyGraphDiffer differ = new HierarchyGraphDiffer();
		differ.diff(nodeBefore, nodeAfter);
		List<GraphDiff> diffs = differ.getDiffs();
		this.consequences = diffs;
	}

	public Map<TraceNode, List<Variable>> getDominator() {
		return dominator;
	}

	public void setDominator(Map<TraceNode, List<Variable>> dominator) {
		this.dominator = dominator;
	}

	public Map<TraceNode, List<Variable>> getDominatee() {
		return dominatee;
	}

	public void setDominatee(Map<TraceNode, List<Variable>> dominatee) {
		this.dominatee = dominatee;
	}
	
	public void addDominator(TraceNode node, List<Variable> variables){
		List<Variable> vars = this.dominator.get(node);
		if(vars == null){
			this.dominator.put(node, variables);
		}
		else{
			vars.addAll(variables);
		}
	}
	
	public void addDominatee(TraceNode node, List<Variable> variables){
		List<Variable> vars = this.dominatee.get(node);
		if(vars == null){
			this.dominatee.put(node, variables);
		}
		else{
			vars.addAll(variables);
		}
	}
}
