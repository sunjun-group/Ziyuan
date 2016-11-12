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
	private Map<DecisionLocation, BreakPoint> decisionMap;
	private Map<DecisionLocation, DecisionLocation> parentMap;
	//to change false branch logic
	private Map<DecisionLocation, BreakPoint> selfBkps;
	private List<BreakPoint> breakPoints;
	
	//recursion handling
	private Set<Integer> returns;
	private List<BreakPoint> returnBkps;
	private Map<DecisionLocation, List<BreakPoint>> bkpListMap;
	

	public BreakpointBuilder(String className, String methodName, List<Variable> variables, CFG cfg, Set<Integer> returns) {
		this.className = className;
		this.methodName = methodName;
		this.variables = variables;
		this.cfg = cfg;
		this.returns = returns;
	}
	
	public void buildBreakpoints() {
		locations = new ArrayList<DecisionLocation>();
		decisionMap = new HashMap<DecisionLocation, BreakPoint>();
		parentMap = new HashMap<DecisionLocation, DecisionLocation>();
		selfBkps = new HashMap<DecisionLocation, BreakPoint>();
		Set<BreakPoint> bkps = new HashSet<BreakPoint>();
		entry = new BreakPoint(className, methodName, cfg.getEntry().getBeginLine());
		//entry = new BreakPoint(className, methodName, 41);
		entry.addVars(variables);
		bkps.add(entry);
		
		List<CfgNode> nodes = cfg.getVertices();
		for (CfgNode node : nodes) {
			CfgDecisionNode decision = (CfgDecisionNode) node;
			DecisionLocation location = new DecisionLocation(className, methodName, decision.getBeginLine(), decision.isLoop());
			locations.add(location);
			BreakPoint breakPoint = new BreakPoint(className, methodName, decision.getTrueBeginLine());
			bkps.add(breakPoint);
			decisionMap.put(location, breakPoint);
			BreakPoint selfBkp = new BreakPoint(className, methodName, decision.getBeginLine());
			bkps.add(selfBkp);
			selfBkps.put(location, selfBkp);
			if (decision.getParentBeginLine() != -1) {
				DecisionLocation parent = new DecisionLocation(className, methodName, decision.getParentBeginLine(), false);
				parentMap.put(location, parent);
			}
		}

		returnBkps = new ArrayList<BreakPoint>();
		for(int returnLine : returns) {
			BreakPoint bkp = new BreakPoint(className, methodName, returnLine);
			returnBkps.add(bkp);
			bkps.add(bkp);
		}
				
		breakPoints = new ArrayList<BreakPoint>(bkps);
		bkpListMap = new HashMap<DecisionLocation, List<BreakPoint>>();
		buildBreakpointsForAllLocations();
	}
	
	private void buildBreakpointsForAllLocations() {
		for (DecisionLocation location : locations) {
			Set<BreakPoint> set = new HashSet<BreakPoint>();
			set.add(entry);
			set.addAll(returnBkps);
			set.add(selfBkps.get(location));
			set.add(getTrueBreakPoint(location));
			BreakPoint parent = getParentBreakPoint(location);
			if (parent != null) {
				set.add(parent);
			}
			bkpListMap.put(location, new ArrayList<BreakPoint>(set));
		}
	}

	public List<BreakPoint> getBreakpoints(DecisionLocation location) {
		return bkpListMap.get(location);
	}
	
	public List<DecisionLocation> getLocations() {
		return locations;
	}

	public List<BreakPoint> getBreakPoints() {
		return breakPoints;
	}

	public BreakPoint getTrueBreakPoint(DecisionLocation location) {
		return decisionMap.get(location);
	}
	
	public BreakPoint getSelfBreakPoint(DecisionLocation location) {
		return selfBkps.get(location);
	}
	
	public BreakPoint getParentBreakPoint(DecisionLocation location) {
		return decisionMap.get(parentMap.get(location));
	}
	
	public boolean isReturnNode(int line) {
		return returns.contains(line);
	}
	
	public boolean isEntryNode(BreakPoint bkp) {
		return entry.equals(bkp);
	}

}
