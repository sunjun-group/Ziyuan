package icsetlv.trial.model;

import icsetlv.common.dto.BreakpointValue;
import sav.strategies.dto.BreakPoint;

public class TraceNode {
	private BreakPoint breakPoint;
	private BreakpointValue programState;

	public TraceNode(BreakPoint breakPoint, BreakpointValue programState) {
		super();
		this.breakPoint = breakPoint;
		this.programState = programState;
	}

	public BreakPoint getBreakPoint() {
		return breakPoint;
	}

	public void setBreakPoint(BreakPoint breakPoint) {
		this.breakPoint = breakPoint;
	}

	public BreakpointValue getProgramState() {
		return programState;
	}

	public void setProgramState(BreakpointValue programState) {
		this.programState = programState;
	}

}
