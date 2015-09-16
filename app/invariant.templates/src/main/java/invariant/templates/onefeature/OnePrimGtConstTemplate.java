package invariant.templates.onefeature;

import java.util.List;

import sav.strategies.dto.execute.value.ExecValue;

public class OnePrimGtConstTemplate extends OneFeatureTemplate {

	public OnePrimGtConstTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}
	
	@Override
	public boolean check() {
		// list of pass and fail exec value only has one feature
		// find max value of all fail values
		ExecValue ev = failExecValuesList.get(0).get(0);
		double maxFail = ev.getDoubleVal();
				
		for (List<ExecValue> evl : failExecValuesList) {
			if (evl.get(0).getDoubleVal() > maxFail) {
				maxFail = evl.get(0).getDoubleVal();
			}
		}
		
		// all pass value must be greater than maxFail
		for (List<ExecValue> evl : passExecValuesList) {
			if (evl.get(0).getDoubleVal() <= maxFail) {
				return false;
			}
		}
				
		return true;
	}

}
