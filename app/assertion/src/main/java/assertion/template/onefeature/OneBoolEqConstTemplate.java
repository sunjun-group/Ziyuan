package assertion.template.onefeature;

import java.util.List;

import icsetlv.common.dto.ExecValue;

public class OneBoolEqConstTemplate extends OnePrimEqConstTemplate {

	public OneBoolEqConstTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}

}
