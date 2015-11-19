package icsetlv.trial.model;

import icsetlv.common.dto.BreakPointValue;
import sav.strategies.dto.BreakPoint;

public class TraceNode {
	private BreakPoint breakPoint;
	private BreakPointValue programState;

	public TraceNode(BreakPoint breakPoint, BreakPointValue programState) {
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

	public BreakPointValue getProgramState() {
		return programState;
	}

	public void setProgramState(BreakPointValue programState) {
		this.programState = programState;
	}

}
