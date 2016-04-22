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

public class BreakpointBuilder {
	
	private String className;
	private String methodName;
	private List<Variable> variables;
	private CFG cfg;
	
	private BreakPoint entry;
	private List<DecisionLocation> locations;
	private List<BreakPoint> breakPoints;
	private Map<DecisionLocation, BreakPoint> decisionMap;
	private Map<DecisionLocation, DecisionLocation> parentMap;
	

	public BreakpointBuilder(String className, String methodName, List<Variable> variables, CFG cfg) {
		this.className = className;
		this.methodName = methodName;
		this.variables = variables;
		this.cfg = cfg;
	}
	
	public void buildBreakpoints() {
		locations = new ArrayList<DecisionLocation>();
		decisionMap = new HashMap<DecisionLocation, BreakPoint>();
		parentMap = new HashMap<DecisionLocation, DecisionLocation>();
		Set<BreakPoint> bkps = new HashSet<BreakPoint>();
		entry = new BreakPoint(className, methodName, cfg.getEntry().getBeginLine());
		entry.addVars(variables);
		
		List<CfgNode> nodes = cfg.getVertices();
		for (CfgNode node : nodes) {
			CfgDecisionNode decision = (CfgDecisionNode) node;
			DecisionLocation location = new DecisionLocation(className, methodName, decision.getBeginLine(), decision.isLoop());
			locations.add(location);
			BreakPoint breakPoint = new BreakPoint(className, methodName, decision.getTrueBeginLine());
			bkps.add(breakPoint);
			decisionMap.put(location, breakPoint);
			if (decision.getParentBeginLine() != -1) {
				DecisionLocation parent = new DecisionLocation(className, methodName, decision.getParentBeginLine(), false);
				parentMap.put(location, parent);
			}
		}
		
		bkps.remove(entry);
		bkps.add(entry);
		
		breakPoints = new ArrayList<BreakPoint>(bkps);
	}
	
	public List<DecisionLocation> getLocations() {
		return locations;
	}
	
	public List<BreakPoint> getBreakPoints() {
		return breakPoints;
	}
	
	public List<BreakPoint> buildBreakpoints(DecisionLocation location) {
		List<BreakPoint> bkps = new ArrayList<BreakPoint>();
		BreakPoint bkp = getBreakPoint(location);
		if (bkp != null) {
			bkps.add(bkp);
			BreakPoint parent = getParentBreakPoint(location);
			if (parent != null) {
				bkps.add(parent);
			}
			bkps.remove(entry);
			bkps.add(entry);
		}
		return bkps;
	}
	
	public BreakPoint getBreakPoint(DecisionLocation location) {
		return decisionMap.get(location);
	}
	
	public BreakPoint getParentBreakPoint(DecisionLocation location) {
		return decisionMap.get(parentMap.get(location));
	}

}
