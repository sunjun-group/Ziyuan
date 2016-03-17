package learntest.data;

import java.util.List;
import java.util.Map;

import icsetlv.common.dto.BreakpointValue;

public class BreakPointDataBuilder {
	
	private Map<String, String> branches;
	
	public BreakPointDataBuilder(Map<String, String> branches){
		this.branches = branches;
	}
	
	public void build(Map<String, BreakpointData> bkpDataMap, List<BreakpointValue> testResult) {
		int size = testResult.size();
		for (int i = 0; i < size; i++) {
			BreakpointValue bkpValue = testResult.get(i);
			String trueBkpId = branches.get(bkpValue.getBkpId());
			if (trueBkpId != null) {
				BreakpointData bkpData = bkpDataMap.get(bkpValue.getBkpId());
				if (bkpData == null) {
					bkpData = new BreakpointData();
					bkpDataMap.put(bkpValue.getBkpId(), bkpData);
				}
				BreakpointValue selection = testResult.get(i + 1);
				if (selection.getBkpId().equals(trueBkpId)) {
					bkpData.addTrueValue(bkpValue);
				}else {
					bkpData.addFalseValue(bkpValue);
				}
			}
		}
	}

}
