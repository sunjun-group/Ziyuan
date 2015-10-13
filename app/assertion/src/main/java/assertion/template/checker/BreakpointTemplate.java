package assertion.template.checker;

import java.util.Arrays;
import java.util.List;

import invariant.templates.CompositeTemplate;
import invariant.templates.SingleTemplate;
import sav.strategies.dto.BreakPoint;

public class BreakpointTemplate {

	private BreakPoint bkp;
	
	private List<SingleTemplate> singleTemplates;
	
	private List<CompositeTemplate> compositeTemplates;
	
	public BreakpointTemplate(BreakPoint bkp, List<SingleTemplate> singleTemplates,
			List<CompositeTemplate> compositeTemplates) {
		this.bkp = bkp;
		this.singleTemplates = singleTemplates;
		this.compositeTemplates = compositeTemplates;
	}
	
	public BreakPoint getBreakPoint() {
		return bkp;
	}
	
	public List<SingleTemplate> getSingleTemplates() {
		return singleTemplates;
	}
	
	public List<CompositeTemplate> getCompositeTemplates() {
		return compositeTemplates;
	}
	
	@Override
	public String toString() {
//		return bkp + Arrays.toString(singleTemplates.toArray()) +
//				Arrays.toString(compositeTemplates.toArray());
		return Arrays.toString(singleTemplates.toArray()) +
				Arrays.toString(compositeTemplates.toArray());
	}
	
}
