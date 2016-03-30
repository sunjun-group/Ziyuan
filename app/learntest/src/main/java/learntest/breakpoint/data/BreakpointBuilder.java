package learntest.breakpoint.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import learntest.cfg.CFG;
import learntest.cfg.CfgDecisionNode;
import learntest.cfg.CfgNode;
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
	
	public Map<BreakPoint, List<DecisionLocation>> buildBreakpoints() {
		Set<BreakPoint> bkps = new HashSet<BreakPoint>();
		Map<BreakPoint, List<DecisionLocation>> decisionMap = new HashMap<BreakPoint, List<DecisionLocation>>();
		
		List<CfgNode> nodes = cfg.getVertices();
		for (CfgNode node : nodes) {
			CfgDecisionNode decision = (CfgDecisionNode) node;
			DecisionLocation location = buildLocation(decision);
			BreakPoint breakPoint = buildBreakPoint(decision);
			bkps.add(breakPoint);
			List<DecisionLocation> locations = decisionMap.get(breakPoint);
			if (locations == null) {
				locations = new ArrayList<DecisionLocation>();
				decisionMap.put(breakPoint, locations);
			}
			locations.add(location);
		}
		
		BreakPoint entry = new BreakPoint(className, methodName, cfg.getEntry().getBeginLine());
		bkps.remove(entry);
		entry.addVars(variables);
		bkps.add(entry);
		
		breakPoints = new ArrayList<BreakPoint>(bkps);
		return decisionMap;
	}
	
	protected abstract DecisionLocation buildLocation(CfgDecisionNode node);
	
	protected abstract BreakPoint buildBreakPoint(CfgDecisionNode node);
	
	public List<BreakPoint> getBreakPoints() {
		return breakPoints;
	}

}
