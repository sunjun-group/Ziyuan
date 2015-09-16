package assertion.template.twofeatures;

import java.util.List;

import icsetlv.common.dto.ExecValue;

public class TwoBoolEqTemplate extends TwoPrimEqTemplate {

	public TwoBoolEqTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}

}
