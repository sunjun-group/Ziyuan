package assertion.template.onefeature;

import java.util.List;

import icsetlv.common.dto.ExecValue;

public class OnePrimLeConstTemplate extends OneFeatureTemplate {

	public OnePrimLeConstTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}
	
	@Override
	public boolean check() {
		// list of pass and fail exec value only has one feature
		// find max value of all pass values
		ExecValue ev = passExecValuesList.get(0).get(0);
		double maxPass = ev.getDoubleVal();
		
		for (List<ExecValue> evl : passExecValuesList) {
			if (evl.get(0).getDoubleVal() > maxPass) {
				maxPass = evl.get(0).getDoubleVal();
			}
		}
		
		// all fail value must be greater than maxPass
		for (List<ExecValue> evl : failExecValuesList) {
			if (evl.get(0).getDoubleVal() <= maxPass) {
				return false;
			}
		}
		
		return true;
	}

}
