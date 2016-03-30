package learntest.breakpoint.data;

import java.util.List;

import learntest.cfg.CFG;
import learntest.cfg.CfgDecisionNode;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;

public class LoopTimesBkpBuilder extends BreakpointBuilder {

	public LoopTimesBkpBuilder(String className, String methodName, List<Variable> variables, CFG cfg) {
		super(className, methodName, variables, cfg);
	}

	@Override
	protected DecisionLocation buildLocation(CfgDecisionNode node) {
		return new DecisionLocation(className, methodName, node.getBeginLine(), node.isLoop());
	}

	@Override
	protected BreakPoint buildBreakPoint(CfgDecisionNode node) {
		return new BreakPoint(className, methodName, 
				cfg.isLoop(node.getTrueBeginLine()) ? node.getBeginLine() : node.getTrueBeginLine());
	}

}
