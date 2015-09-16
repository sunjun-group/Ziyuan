package assertion.template.onefeature;

import java.util.List;

import icsetlv.common.dto.ExecValue;

public class OneBoolNeConstTemplate extends OnePrimNeConstTemplate {

	public OneBoolNeConstTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}

}
