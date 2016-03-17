package assertion.template.checker;

import java.util.ArrayList;
import java.util.List;

import invariant.templates.SingleTemplate;
import invariant.templates.Template;
import sav.strategies.dto.execute.value.ExecValue;

public class TypeTemplateChecker {

	protected List<Template> singleTemplates = new ArrayList<Template>();
	
 	protected List<Template> satPassTemplates = new ArrayList<Template>();
	
	protected List<Template> satFailTemplates = new ArrayList<Template>();
	
	public void checkTemplates(List<List<ExecValue>> passExecValuesList,
			List<List<ExecValue>> failExecValuesList) {
	}
	
	public List<Template> getSingleTemplates() {
		return singleTemplates;
	}
	
	public List<Template> getSatPassTemplates() {
		return satPassTemplates;
	}
	
	public List<Template> getSatFailTemplates() {
		return satFailTemplates;
	}
	
	public void check(SingleTemplate t) {
		boolean valid = t.check();
		
		if (valid) singleTemplates.add(t);
		if (!valid && t.isSatPass()) satPassTemplates.add(t);
		if (!valid && t.isSatFail()) satFailTemplates.add(t);
	}
	
}
