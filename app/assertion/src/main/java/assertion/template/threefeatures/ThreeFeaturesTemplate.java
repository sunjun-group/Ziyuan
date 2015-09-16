package assertion.template.threefeatures;

import java.util.List;

import assertion.template.Template;
import icsetlv.common.dto.ExecValue;

public class ThreeFeaturesTemplate extends Template {

	public ThreeFeaturesTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}

}
