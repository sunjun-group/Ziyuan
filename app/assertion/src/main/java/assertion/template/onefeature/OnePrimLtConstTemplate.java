package assertion.template.onefeature;

import java.util.List;

import icsetlv.common.dto.ExecValue;

public class OnePrimLtConstTemplate extends OneFeatureTemplate {

	public OnePrimLtConstTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}
	
	@Override
	public boolean check() {
		// list of pass and fail exec value only has one feature
		// find min value of all fail values
		ExecValue ev = failExecValuesList.get(0).get(0);
		double minFail = ev.getDoubleVal();
				
		for (List<ExecValue> evl : failExecValuesList) {
			if (evl.get(0).getDoubleVal() < minFail) {
				minFail = evl.get(0).getDoubleVal();
			}
		}
		
		// all pass value must be less than minFail
		for (List<ExecValue> evl : passExecValuesList) {
			if (evl.get(0).getDoubleVal() >= minFail) {
				return false;
			}
		}
				
		return true;
	}

}
