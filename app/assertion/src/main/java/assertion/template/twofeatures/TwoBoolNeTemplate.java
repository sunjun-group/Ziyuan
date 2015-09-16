package assertion.template.twofeatures;

import java.util.List;

import icsetlv.common.dto.ExecValue;

public class TwoBoolNeTemplate extends TwoPrimNeTemplate {

	public TwoBoolNeTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}

}
