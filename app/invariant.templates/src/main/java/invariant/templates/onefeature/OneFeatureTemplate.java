package invariant.templates.onefeature;

import java.util.List;

import invariant.templates.SingleTemplate;
import sav.strategies.dto.execute.value.ExecValue;

public class OneFeatureTemplate extends SingleTemplate {

	public OneFeatureTemplate(List<List<ExecValue>> passValues, List<List<ExecValue>> failValues) {
		super(passValues, failValues);
	}
	
}
