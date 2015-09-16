package invariant.templates.threefeatures;

import java.util.List;

import invariant.templates.Template;
import sav.strategies.dto.execute.value.ExecValue;

public class ThreeFeaturesTemplate extends Template {

	public ThreeFeaturesTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}

}
