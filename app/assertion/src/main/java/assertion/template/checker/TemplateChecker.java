package assertion.template.checker;

import java.util.ArrayList;
import java.util.List;

import invariant.templates.CompositeTemplate;
import invariant.templates.SingleTemplate;
import sav.strategies.dto.execute.value.ExecValue;

public class TemplateChecker {

	private List<List<ExecValue>> origPassExecValuesList;
	
	private List<List<ExecValue>> origFailExecValuesList;
	
	private List<SingleTemplate> singleTemplates;
	
	private List<CompositeTemplate> compositeTemplates;
	
	public TemplateChecker(List<List<ExecValue>> origPassExecValuesList,
			List<List<ExecValue>> origFailExecValuesList) {
		this.origPassExecValuesList = origPassExecValuesList;
		this.origFailExecValuesList = origFailExecValuesList;
		
		singleTemplates = new ArrayList<SingleTemplate>();
		compositeTemplates = new ArrayList<CompositeTemplate>();
	}
	
	public void checkTemplates() {
		SingleTemplateChecker stc = new SingleTemplateChecker(
				origPassExecValuesList,
				origFailExecValuesList);
		stc.checkSingleTemplates();
		singleTemplates.addAll(stc.getSingleTemplates());
		
		CompositeTemplateChecker ctc = new CompositeTemplateChecker(
				stc.getSatifiedPassTemplates(),
				stc.getSatifiedFailTemplates());
		ctc.checkCompositeTemplates();
		compositeTemplates.addAll(ctc.getCompositeTemplates());
	}
	
	public List<SingleTemplate> getSingleTemplates() {
		return singleTemplates;
	}
	
	public List<CompositeTemplate> getCompositeTemplates() {
		return compositeTemplates;
	}
	
}
