package invariant.templates.threefeatures;

import java.util.List;

import sav.strategies.dto.execute.value.ExecValue;

public class ThreePrimSubTemplate extends ThreeFeaturesTemplate {

	public ThreePrimSubTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}
	
	@Override
	public boolean check() {
		// list of pass and fail exec value only has two features
		// first feature must be equals to mod between second and third feature
		for (List<ExecValue> evl : passExecValuesList) {
			double d0 = evl.get(0).getDoubleVal();
			double d1 = evl.get(1).getDoubleVal();
			double d2 = evl.get(2).getDoubleVal();
			
			if (d0 != (d1 - d2)) {
				return false;
			}
		}
						
		// first feature must not be equals to mod between second and third feature
		for (List<ExecValue> evl : failExecValuesList) {
			double d0 = evl.get(0).getDoubleVal();
			double d1 = evl.get(1).getDoubleVal();
			double d2 = evl.get(2).getDoubleVal();
			
			if (d0 == (d1 - d2)) {
				return false;
			}
		}
						
		return true;
	}

}
