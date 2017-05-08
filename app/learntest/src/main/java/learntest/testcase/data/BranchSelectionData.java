package learntest.testcase.data;

import java.util.List;

import icsetlv.common.dto.BreakpointValue;
import learntest.breakpoint.data.DecisionLocation;

public class BranchSelectionData extends BreakpointData {
	
	public BranchSelectionData(DecisionLocation location){
		super(location);
	}
	
	public void addTrueValue(BreakpointValue bkpValue) {
		trueValues.add(bkpValue);
	}
	
	@Override
	public String toString() {
		return "BranchSelectionData (" + location + "), \ntrueValues=" + trueValues
				+ ", \nfalseValues=" + falseValues + "]\n";
	}

	@Override
	public List<BreakpointValue> getMoreTimesValues() {
		return null;
	}

	@Override
	public List<BreakpointValue> getOneTimeValues() {
		return null;
	}
	
}
