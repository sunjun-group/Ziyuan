package learntest.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import icsetlv.common.dto.BreakpointValue;
import sav.common.core.Pair;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.ClassLocation;

public class BranchSelectionDataBuilder {
	
	private List<Pair<ClassLocation, BreakPoint>> branches;
	private Map<ClassLocation, BranchSelectionData> bsDataMap;
	
	public BranchSelectionDataBuilder(List<Pair<ClassLocation, BreakPoint>> branches){
		this.branches = branches;
		bsDataMap = new HashMap<ClassLocation, BranchSelectionData>();
		for (Pair<ClassLocation, BreakPoint> branch : branches) {
			ClassLocation branchLocation = branch.first();
			bsDataMap.put(branchLocation, new BranchSelectionData(branchLocation));
		}
	}
	
	public void build(Set<BreakPoint> path, BreakpointValue inputValue) {
		for (Pair<ClassLocation, BreakPoint> branch : branches) {
			ClassLocation decision = branch.first();
			BreakPoint trueBranch = branch.second();
			BranchSelectionData bsData = bsDataMap.get(decision);
			if (path.contains(trueBranch)) {
				bsData.addTrueValue(inputValue);
			} else {
				bsData.addFalseValue(inputValue);
			}
		}
	}
	
	public List<BranchSelectionData> getResult() {
		return new ArrayList<BranchSelectionData>(bsDataMap.values());
	}

}
