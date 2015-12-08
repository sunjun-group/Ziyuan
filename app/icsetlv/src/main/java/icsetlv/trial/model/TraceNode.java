package icsetlv.trial.model;

import icsetlv.common.dto.BreakPointValue;
import sav.strategies.dto.BreakPoint;

public class TraceNode {
	
	private BreakPoint breakPoint;
	private BreakPointValue programState;
	private BreakPointValue afterState;
	
	/**
	 * the order of this node in the whole trace
	 */
	private int order;
	
	/**
	 * indicate whether this node has been marked correct/incorrect by user
	 */
	private Boolean markedCorrrect;

	public TraceNode(BreakPoint breakPoint, BreakPointValue programState, int order) {
		super();
		this.breakPoint = breakPoint;
		this.programState = programState;
		this.order = order;
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

	public void addAfterExectionValue(BreakPointValue bkpVal) {
		this.afterState = bkpVal;
	}

	public BreakPointValue getAfterState() {
		return afterState;
	}

	public void setAfterState(BreakPointValue afterState) {
		this.afterState = afterState;
	}

	
	
}
