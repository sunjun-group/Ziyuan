package invariant.templates.twofeatures;

import java.util.List;

import sav.strategies.dto.execute.value.ExecValue;

public class TwoPrimEqTemplate extends TwoFeaturesTemplate {

	public TwoPrimEqTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}
	
	@Override
	public boolean checkPassValue(List<ExecValue> evl) {
		// list of pass and fail exec value only has two features
		// two features in pass values must be equals
		double v1 = evl.get(0).getDoubleVal();
		double v2 = evl.get(1).getDoubleVal();
		return v1 == v2;
	}
	
	@Override
	public boolean checkFailValue(List<ExecValue> evl) {
		// list of pass and fail exec value only has two features
		// two features in pass values must be different
		double v1 = evl.get(0).getDoubleVal();
		double v2 = evl.get(1).getDoubleVal();
		return v1 != v2;
	}

}
