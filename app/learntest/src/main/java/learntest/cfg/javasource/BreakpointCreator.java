package learntest.cfg.javasource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import common.cfg.CFG;
import common.cfg.CfgDecisionNode;
import common.cfg.CfgNode;
import learntest.breakpoint.data.DecisionBkpsData;
import learntest.breakpoint.data.DecisionLocation;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;

public class BreakpointCreator {
	
	private String className;
	private String methodName;
	private List<Variable> variables;
	
	private BreakPoint entry;
	
	//recursion handling
	private Set<Integer> returns;
	private List<BreakPoint> returnBkps;
	private DecisionBkpsData decisionBkpData;
	

	public BreakpointCreator(String className, String methodName, List<Variable> variables, Set<Integer> returns) {
		this.className = className;
		this.methodName = methodName;
		this.variables = variables;
		this.returns = returns;
	}
	
	public DecisionBkpsData createBkpsfromCfg(CFG cfg) {
		List<DecisionLocation> locations = new ArrayList<DecisionLocation>();
		Map<DecisionLocation, BreakPoint> decisionMap = new HashMap<DecisionLocation, BreakPoint>();
		Map<DecisionLocation, DecisionLocation> parentMap = new HashMap<DecisionLocation, DecisionLocation>();
		Map<DecisionLocation, BreakPoint> selfBkps = new HashMap<DecisionLocation, BreakPoint>();
		Set<BreakPoint> allBkps = new HashSet<BreakPoint>();
		entry = new BreakPoint(className, methodName, cfg.getEntry().getBeginLine());
		//entry = new BreakPoint(className, methodName, 41);
		entry.addVars(variables);
		allBkps.add(entry);
		
		List<CfgNode> nodes = cfg.getVertices();
		for (CfgNode node : nodes) {
			CfgDecisionNode decision = (CfgDecisionNode) node;
			DecisionLocation location = new DecisionLocation(className, methodName, decision.getBeginLine(), decision.isLoop());
			locations.add(location);
			BreakPoint breakPoint = new BreakPoint(className, methodName, decision.getTrueBeginLine());
//			breakPoint.addVars(variables);
			allBkps.add(breakPoint);
			decisionMap.put(location, breakPoint);
			BreakPoint selfBkp = new BreakPoint(className, methodName, decision.getBeginLine());
//			selfBkp.addVars(variables);
			allBkps.add(selfBkp);
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
			allBkps.add(bkp);
		}
		
		decisionBkpData = new DecisionBkpsData(locations, allBkps, decisionMap, parentMap);		
		decisionBkpData.setSelfBkps(selfBkps);
		buildBreakpointsForAllLocations(decisionBkpData);
		return decisionBkpData;
	}
	
	private void buildBreakpointsForAllLocations(DecisionBkpsData data) {
		for (DecisionLocation location : data.getLocations()) {
			Set<BreakPoint> bkps = new HashSet<BreakPoint>();
			bkps.add(entry);
			bkps.addAll(returnBkps);
			bkps.add(data.getSelfBkps().get(location));
			bkps.add(data.getTrueBreakPoint(location));
			BreakPoint parent = data.getParentBreakPoint(location);
			if (parent != null) {
				bkps.add(parent);
			}
			decisionBkpData.add(location, bkps);
		}
	}

	public boolean isReturnNode(int line) {
		return returns.contains(line);
	}
	
	public boolean isEntryNode(BreakPoint bkp) {
		return entry.equals(bkp);
	}

//	public DecisionBkpsData getDecisionBkpData() {
//		return decisionBkpData;
//	}
}
