package invariant.templates.onefeature;

import java.util.List;

import invariant.templates.Template;
import sav.strategies.dto.execute.value.ExecValue;

public class OneFeatureTemplate extends Template {

	public OneFeatureTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}
	
}
