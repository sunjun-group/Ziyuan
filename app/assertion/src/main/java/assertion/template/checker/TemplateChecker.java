package assertion.template.checker;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import invariant.templates.CompositeTemplate;
import invariant.templates.SingleTemplate;
import sav.strategies.dto.execute.value.ExecValue;

public class TemplateChecker {

	public List<List<ExecValue>> passValues;
	
	public List<List<ExecValue>> failValues;
	
	public SingleTemplateChecker stc;
	
 	public CompositeTemplateChecker ctc;
 	
 	public PACChecker pac;
	
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
	
	public void checkSingleTemplates() {
		log.info("Pass values: {}\n", passValues);
		log.info("Fail values: {}\n", failValues);
		
		stc = new SingleTemplateChecker(passValues, failValues);
		stc.checkSingleTemplates();
	}
	
	public void checkCompositeTemplates() {
		ctc = new CompositeTemplateChecker(
				passValues, failValues,
				stc.getAllTemplates());
		ctc.checkCompositeTemplates();
	}
	
	public List<SingleTemplate> getSingleTemplates() {
		if (stc != null) return stc.getValidTemplates();
		else return new ArrayList<SingleTemplate>();
	}
	
	public List<CompositeTemplate> getCompositeTemplates() {
		if (ctc != null) return ctc.getValidTemplates();
		else return new ArrayList<CompositeTemplate>();
	}
	
}
