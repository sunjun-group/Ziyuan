package invariant.templates.twofeatures;

import java.util.List;

import sav.strategies.dto.execute.value.ExecValue;

public class TwoBoolEqTemplate extends TwoPrimEqTemplate {

	public TwoBoolEqTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}

}
