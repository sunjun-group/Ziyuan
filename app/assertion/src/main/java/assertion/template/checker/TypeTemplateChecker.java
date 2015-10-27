package assertion.template.checker;

import java.util.ArrayList;
import java.util.List;

import invariant.templates.SingleTemplate;
import invariant.templates.Template;
import sav.strategies.dto.execute.value.ExecValue;

public class TypeTemplateChecker {

	protected List<Template> singleTemplates = new ArrayList<Template>();
	
	protected List<Template> satifiedPassTemplates = new ArrayList<Template>();
	
	protected List<Template> satifiedFailTemplates = new ArrayList<Template>();
	
	public void checkTemplates(List<List<ExecValue>> passExecValuesList,
			List<List<ExecValue>> failExecValuesList) {
	}
	
	public List<Template> getSingleTemplates() {
		return singleTemplates;
	}
	
	public List<Template> getSatifiedPassTemplates() {
		return satifiedPassTemplates;
	}
	
	public List<Template> getSatifiedFailTemplates() {
		return satifiedFailTemplates;
	}
	
	public void check(SingleTemplate t) {
		boolean valid = t.check();
		
		if (valid) singleTemplates.add(t);
		if (!valid && t.isSatisfiedAllPassValues()) satifiedPassTemplates.add(t);
		if (!valid && t.isSatisfiedAllFailValues()) satifiedFailTemplates.add(t);
	}
	
}
