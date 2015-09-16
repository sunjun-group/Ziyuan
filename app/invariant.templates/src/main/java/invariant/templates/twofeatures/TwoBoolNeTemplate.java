package invariant.templates.twofeatures;

import java.util.List;

import sav.strategies.dto.execute.value.ExecValue;

public class TwoBoolNeTemplate extends TwoPrimNeTemplate {

	public TwoBoolNeTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}

}
