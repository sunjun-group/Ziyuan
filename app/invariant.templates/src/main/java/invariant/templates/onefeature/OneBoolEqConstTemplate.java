package invariant.templates.onefeature;

import java.util.List;

import sav.strategies.dto.execute.value.ExecValue;

public class OneBoolEqConstTemplate extends OnePrimEqConstTemplate {

	public OneBoolEqConstTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}

}
