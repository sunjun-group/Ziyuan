package icsetlv.trial.model;

import icsetlv.common.dto.BreakPointValue;
import sav.strategies.dto.BreakPoint;

public class TraceNode {
	
	private BreakPoint breakPoint;
	private BreakPointValue programState;
	
	private int order;

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

	
	
}
