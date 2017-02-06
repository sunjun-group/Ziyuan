package learntest.testcase.data;

import icsetlv.common.dto.BreakpointValue;
import learntest.breakpoint.data.DecisionLocation;

public class BranchSelectionData extends BreakpointData{
	
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
	
}
