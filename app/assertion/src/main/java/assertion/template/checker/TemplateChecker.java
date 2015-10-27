package assertion.template.checker;

import java.util.ArrayList;
import java.util.List;

import invariant.templates.CompositeTemplate;
import invariant.templates.SingleTemplate;
import invariant.templates.Template;
import sav.strategies.dto.execute.value.ExecValue;

public class TemplateChecker {

	private List<List<ExecValue>> origPassExecValuesList;
	
	private List<List<ExecValue>> origFailExecValuesList;
	
	private SingleTemplateChecker stc;
	
	private CompositeTemplateChecker ctc;
	
	public TemplateChecker(List<List<ExecValue>> origPassExecValuesList,
			List<List<ExecValue>> origFailExecValuesList) {
		this.origPassExecValuesList = origPassExecValuesList;
		this.origFailExecValuesList = origFailExecValuesList;
	}
	
	public void addExecValuesList(List<List<ExecValue>> newPassExecValuesList,
			List<List<ExecValue>> newFailExecValuesList) {
		for (List<ExecValue> evl : newPassExecValuesList) {
			if (!duplicate(origPassExecValuesList, evl)) {
				origPassExecValuesList.add(evl);
			}
		}
		
		for (List<ExecValue> evl : newFailExecValuesList) {
			if (!duplicate(origFailExecValuesList, evl)) {
				origFailExecValuesList.add(evl);
			}
		}
	}
	
	private boolean duplicate(List<List<ExecValue>> currEvlList, List<ExecValue> newEvl) {
		String s1 = newEvl.toString();
		
		for (List<ExecValue> evl : currEvlList) {
			String s2 = evl.toString();
			if (s1.equals(s2)) return true;
		}
		
		return false;
	}
	
	public void checkSingleTemplates() {
		System.out.println("pass values = " + origPassExecValuesList);
		System.out.println("fail values = " + origFailExecValuesList);
		
		stc = new SingleTemplateChecker(
				origPassExecValuesList,
				origFailExecValuesList);
		stc.checkSingleTemplates();
	}
	
	public void checkCompositeTemplates() {
		ctc = new CompositeTemplateChecker(
				stc.getSatifiedPassTemplates(),
				stc.getSatifiedFailTemplates());
		ctc.checkCompositeTemplates();
	}
	
	public List<Template> getSingleTemplates() {
		if (stc != null) return stc.getSingleTemplates();
		else return new ArrayList<Template>();
	}
	
	public List<Template> getCompositeTemplates() {
		if (ctc != null) return ctc.getCompositeTemplates();
		else return new ArrayList<Template>();
	}
	
}
