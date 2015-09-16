package invariant.templates.onefeature;

import java.util.List;

import sav.strategies.dto.execute.value.ExecValue;

public class OnePrimGeZeroTemplate extends OneFeatureTemplate {

	public OnePrimGeZeroTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}
	
	@Override
	public boolean check() {
		for (List<ExecValue> evl : passExecValuesList) {
			if (evl.get(0).getDoubleVal() < 0.0) {
				return false;
			}
		}
		
		for (List<ExecValue> evl : failExecValuesList) {
			if (evl.get(0).getDoubleVal() >= 0.0) {
				return false;
			}
		}
				
		return true;
	}

}
