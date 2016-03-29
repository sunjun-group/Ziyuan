package learntest.breakpoint.data;

import java.util.List;

import learntest.cfg.CFG;
import sav.common.core.Pair;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;

public abstract class BreakpointBuilder {
	
	protected String className;
	protected String methodName;
	protected List<Variable> variables;
	protected CFG cfg;
	protected List<BreakPoint> breakPoints;

	public BreakpointBuilder(String className, String methodName, List<Variable> variables, CFG cfg) {
		this.className = className;
		this.methodName = methodName;
		this.variables = variables;
		this.cfg = cfg;
	}
	
	public abstract List<Pair<DecisionLocation, BreakPoint>> buildBreakpoints();
	
	public List<BreakPoint> getBreakPoints() {
		return breakPoints;
	}

}
