package learntest.breakpoint.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import learntest.cfg.CFG;
import learntest.cfg.CfgDecisionNode;
import learntest.cfg.CfgNode;
import sav.common.core.Pair;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;

public class LoopTimesBkpBuilder extends BreakpointBuilder {

	public LoopTimesBkpBuilder(String className, String methodName, List<Variable> variables, CFG cfg) {
		super(className, methodName, variables, cfg);
	}

	@Override
	public List<Pair<DecisionLocation, BreakPoint>> buildBreakpoints() {
		Set<BreakPoint> bkps = new HashSet<BreakPoint>();
		List<Pair<DecisionLocation, BreakPoint>> decisionMap = new ArrayList<Pair<DecisionLocation,BreakPoint>>();
		
		List<CfgNode> nodes = cfg.getVertices();
		for (CfgNode node : nodes) {
			CfgDecisionNode decision = (CfgDecisionNode) node;
			DecisionLocation location = new DecisionLocation(className, methodName, 
					decision.getBeginLine(), decision.isLoop());
			BreakPoint breakPoint = new BreakPoint(className, methodName, 
					cfg.isLoop(decision.getTrueBeginLine()) ? decision.getBeginLine() : decision.getTrueBeginLine());
			bkps.add(breakPoint);
			decisionMap.add(new Pair<DecisionLocation, BreakPoint>(location, breakPoint));
		}
		
		BreakPoint entry = new BreakPoint(className, methodName, cfg.getEntry().getBeginLine());
		bkps.remove(entry);
		entry.addVars(variables);
		bkps.add(entry);
		
		breakPoints = new ArrayList<BreakPoint>(bkps);
		return decisionMap;
	}

}
