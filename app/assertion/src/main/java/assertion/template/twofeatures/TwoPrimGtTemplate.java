package assertion.template.twofeatures;

import java.util.List;

import icsetlv.common.dto.ExecValue;

public class TwoPrimGtTemplate extends TwoFeaturesTemplate {

	public TwoPrimGtTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}
	
	@Override
	public boolean check() {
		// list of pass and fail exec value only has two features
		// first feature must be greater than second feature
		for (List<ExecValue> evl : passExecValuesList) {
			if (evl.get(0).getDoubleVal() <= evl.get(1).getDoubleVal()) {
				return false;
			}
		}
				
		// first feature must be less than or equals to second feature
		for (List<ExecValue> evl : failExecValuesList) {
			if (evl.get(0).getDoubleVal() > evl.get(1).getDoubleVal()) {
				return false;
			}
		}
				
		return true;
	}

}
