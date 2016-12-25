package invariant.templates.twofeatures;

import java.util.List;

import invariant.templates.SingleTemplate;
import sav.strategies.dto.execute.value.ExecValue;

public class TwoFeaturesTemplate extends SingleTemplate {

	public TwoFeaturesTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}
	
}
