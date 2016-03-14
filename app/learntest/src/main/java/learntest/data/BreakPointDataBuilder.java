package learntest.data;

import java.util.List;
import java.util.Map;

import icsetlv.common.dto.BreakpointValue;
import sav.common.core.Pair;

public class BreakPointDataBuilder {
	
	private List<Pair<Integer, Integer>> branches;
	
	public BreakPointDataBuilder(List<Pair<Integer, Integer>> branches){
		this.branches = branches;
	}
	
	public void build(Map<String, BreakpointData> bkpDataMap, List<BreakpointValue> testResult) {
		
	}

}
