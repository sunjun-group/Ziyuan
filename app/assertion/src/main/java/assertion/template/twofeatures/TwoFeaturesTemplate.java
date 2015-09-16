package assertion.template.twofeatures;

import java.util.List;

import assertion.template.Template;
import icsetlv.common.dto.ExecValue;

public class TwoFeaturesTemplate extends Template {

	public TwoFeaturesTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}
	
}
