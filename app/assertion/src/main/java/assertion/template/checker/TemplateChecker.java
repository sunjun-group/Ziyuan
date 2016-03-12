package assertion.template.checker;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import invariant.templates.Template;
import sav.strategies.dto.execute.value.ExecValue;

public class TemplateChecker {

	public List<List<ExecValue>> passValues;
	
	public List<List<ExecValue>> failValues;
	
	public SingleTemplateChecker stc;
	
 	public CompositeTemplateChecker ctc;
	
	public boolean hasNewData;
	
	private static Logger log = LoggerFactory.getLogger(TemplateChecker.class);
	
	public TemplateChecker(List<List<ExecValue>> passValues,
			List<List<ExecValue>> failValues) {
		this.passValues = passValues;
		this.failValues = failValues;
	}
	
	public void addExecValuesList(List<List<ExecValue>> newPassValues,
			List<List<ExecValue>> newFailValues) {
		for (List<ExecValue> evl : newPassValues) {
			if (!duplicate(passValues, evl)) {
				this.passValues.add(evl);
				hasNewData = true;
			}
		}
		
		for (List<ExecValue> evl : newFailValues) {
			if (!duplicate(failValues, evl)) {
				this.failValues.add(evl);
				hasNewData = true;
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
	
//	public void recheckSingleTemplates() {
//		log.info("pass values = " + newPassExecValuesList);
//		log.info("fail values = " + newFailExecValuesList);
//		
//		moreTemplates = stc.recheckSingleTemplates(newPassExecValuesList, newFailExecValuesList);
//		
//		newPassExecValuesList.clear();
//		newFailExecValuesList.clear();
//	}
	
	public void checkSingleTemplates() {
		log.info("Pass values: {}\n", passValues);
		log.info("Fail values: {}\n", failValues);
		
		stc = new SingleTemplateChecker(passValues, failValues);
		stc.checkSingleTemplates();
	}
	
//	public boolean isAllIlp(List<Template> templates) {
//		for (Template t : templates) {
//			if (!(t instanceof OnePrimIlpTemplate || t instanceof TwoPrimIlpTemplate ||
//					t instanceof ThreePrimIlpTemplate)) {
//				return false;
//			}
//		}
//		
//		return true;
//	}
	
//	public void recheckCompositeTemplates() {
//		if (isAllIlp(stc.getSatifiedPassTemplates()) &&
//				isAllIlp(stc.getSatifiedFailTemplates()) &&
//				ctc.getCompositeTemplates().isEmpty()) return;
//		else checkCompositeTemplates();
//	}
	
	public void checkCompositeTemplates() {
		ctc = new CompositeTemplateChecker(
				stc.getSatPassTemplates(),
				stc.getSatFailTemplates());
		ctc.checkCompositeTemplates();
	}
	
	public List<Template> getSingleTemplates() {
		if (stc != null) return stc.getSingleTemplates();
		else return new ArrayList<Template>();
	}
	
	public List<Template> getSatPassTemplates() {
		if (stc != null) return stc.getSatPassTemplates();
		else return new ArrayList<Template>();
	}
	
	public List<Template> getSatFailTemplates() {
		if (stc != null) return stc.getSatFailTemplates();
		else return new ArrayList<Template>();
	}
	
	public List<Template> getCompositeTemplates() {
		if (ctc != null) return ctc.getCompositeTemplates();
		else return new ArrayList<Template>();
	}
	
}
