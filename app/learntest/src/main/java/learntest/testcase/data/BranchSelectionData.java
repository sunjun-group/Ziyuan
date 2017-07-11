package learntest.testcase.data;

import java.util.List;

import cfgcoverage.jacoco.analysis.data.NodeCoverage;
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

	/* (non-Javadoc)
	 * @see learntest.testcase.data.INodeCoveredData#update(cfgcoverage.jacoco.analysis.data.NodeCoverage, int, java.util.List)
	 */
	@Override
	public void update(NodeCoverage coverage, int samplesFirstIdx, List<BreakpointValue> sampleTestInputs) {
		// TODO Auto-generated method stub
		
	}
	
}
