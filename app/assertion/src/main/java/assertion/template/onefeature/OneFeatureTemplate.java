package assertion.template.onefeature;

import java.util.List;

import assertion.template.Template;
import icsetlv.common.dto.ExecValue;

public class OneFeatureTemplate extends Template {

	public OneFeatureTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}
	
}
