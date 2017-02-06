package assertion.template.checker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import invariant.templates.Template;
import sav.strategies.dto.BreakPoint;

public class BreakpointTemplate {

	private BreakPoint bkp;
	
	private List<Template> singleTemplates;
	
	private List<Template> compositeTemplates;
	
	public BreakpointTemplate(BreakPoint bkp, List<Template> singleTemplates,
			List<Template> compositeTemplates) {
		this.bkp = bkp;
		this.singleTemplates = singleTemplates;
		this.compositeTemplates = compositeTemplates;
	}
	
	public BreakPoint getBreakPoint() {
		return bkp;
	}
	
	public List<Template> getSingleTemplates() {
		return singleTemplates;
	}
	
	public List<Template> getCompositeTemplates() {
		return compositeTemplates;
	}
	
	public List<Template> getTemplates() {
		List<Template> templates = new ArrayList<Template>();
		
		templates.addAll(singleTemplates);
		templates.addAll(compositeTemplates);
		
		return templates;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\nSingle templates: " + Arrays.toString(singleTemplates.toArray()));
		sb.append("\nComposite templates: " + Arrays.toString(compositeTemplates.toArray()));
		return sb.toString();
	}
	
}
