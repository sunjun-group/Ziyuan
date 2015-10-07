package assertion.template.checker;

import java.util.ArrayList;
import java.util.List;

import invariant.templates.SingleTemplate;
import sav.strategies.dto.execute.value.ExecValue;

public class TypeTemplateChecker {

	protected List<SingleTemplate> singleTemplates = new ArrayList<SingleTemplate>();
	
	protected List<SingleTemplate> satifiedPassTemplates = new ArrayList<SingleTemplate>();
	
	protected List<SingleTemplate> satifiedFailTemplates = new ArrayList<SingleTemplate>();
	
	public void checkTemplates(List<List<ExecValue>> passExecValuesList,
			List<List<ExecValue>> failExecValuesList) {
	}
	
	public List<SingleTemplate> getSingleTemplates() {
		return singleTemplates;
	}
	
	public List<SingleTemplate> getSatifiedPassTemplates() {
		return satifiedPassTemplates;
	}
	
	public List<SingleTemplate> getSatifiedFailTemplates() {
		return satifiedFailTemplates;
	}
	
	public void check(SingleTemplate t) {
		if (t.check()) singleTemplates.add(t);
		if (!t.check() && t.isSatisfiedAllPassValues()) satifiedPassTemplates.add(t);
		if (!t.check() && t.isSatisfiedAllFailValues()) satifiedFailTemplates.add(t);
	}
	
}
