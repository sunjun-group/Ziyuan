package assertion.template.onefeature;

import java.util.List;

import icsetlv.common.dto.ExecValue;

public class OnePrimGeConstTemplate extends OneFeatureTemplate {

	public OnePrimGeConstTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}
	
	@Override
	public boolean check() {
		// list of pass and fail exec value only has one feature
		// find min value of all pass values
		ExecValue ev = passExecValuesList.get(0).get(0);
		double minPass = ev.getDoubleVal();
				
		for (List<ExecValue> evl : passExecValuesList) {
			if (evl.get(0).getDoubleVal() < minPass) {
				minPass = evl.get(0).getDoubleVal();
			}
		}
		
		// all fail value must be less than maxFail
		for (List<ExecValue> evl : failExecValuesList) {
			if (evl.get(0).getDoubleVal() >= minPass) {
				return false;
			}
		}
				
		return true;
	}

}
