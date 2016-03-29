package learntest.testcase.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import icsetlv.common.dto.BreakpointValue;
import learntest.breakpoint.data.DecisionLocation;
import sav.common.core.Pair;
import sav.strategies.dto.BreakPoint;

public class BreakpointDataBuilder {
	
	private List<Pair<DecisionLocation, BreakPoint>> decisionMap;
	private Map<DecisionLocation, BreakpointData> bkpDataMap;
	
	public BreakpointDataBuilder(List<Pair<DecisionLocation, BreakPoint>> decisionMap){
		this.decisionMap = decisionMap;
		bkpDataMap = new HashMap<DecisionLocation, BreakpointData>();
		for (Pair<DecisionLocation, BreakPoint> decisionPair : decisionMap) {
			DecisionLocation decision = decisionPair.first();
			if (decision.isLoop()) {
				bkpDataMap.put(decision, new LoopTimesData(decision));
			}else {
				bkpDataMap.put(decision, new BranchSelectionData(decision));
			}
		}
	}
	
	public void build(Collection<BreakPoint> path, BreakpointValue inputValue) {
		for (Pair<DecisionLocation, BreakPoint> decisionPair : decisionMap) {
			DecisionLocation decision = decisionPair.first();
			BreakPoint bkp = decisionPair.second();
			BreakpointData bkpData = bkpDataMap.get(decision);
			if (decision.isLoop()) {
				LoopTimesData loopData = (LoopTimesData) bkpData;
				//TODO handle loop times
			} else {
				BranchSelectionData branchData = (BranchSelectionData) bkpData;
				if (path.contains(bkp)) {
					branchData.addTrueValue(inputValue);
				} else {
					branchData.addFalseValue(inputValue);
				}
			}
		}
	}

}
