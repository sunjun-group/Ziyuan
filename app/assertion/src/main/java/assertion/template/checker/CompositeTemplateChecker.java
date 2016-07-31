package assertion.template.checker;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import invariant.templates.CompositeTemplate;
import invariant.templates.SingleTemplate;
import invariant.templates.onefeature.OneNumIlpTemplate;
import invariant.templates.twofeatures.TwoNumIlpTemplate;
import sav.strategies.dto.execute.value.ExecValue;

public class CompositeTemplateChecker {
	
	private List<List<ExecValue>> passValues;
	
	private List<List<ExecValue>> failValues;

	private List<SingleTemplate> allTemplates;
	
	private List<SingleTemplate> ilpTemplates;
	
	private List<SingleTemplate> primTemplates;
	
	private List<CompositeTemplate> validTemplates;
	
	private static Logger log = LoggerFactory.getLogger(CompositeTemplateChecker.class);
	
	public CompositeTemplateChecker(List<List<ExecValue>> passValues,
			List<List<ExecValue>> failValues,
			List<SingleTemplate> allTemplates) {
		this.allTemplates = allTemplates;
		this.passValues = passValues;
		this.failValues = failValues;
		
		validTemplates = new ArrayList<CompositeTemplate>();
	}
	
	public List<CompositeTemplate> getValidTemplates() {
		return validTemplates;
	}
	
	public void checkCompositeTemplates() {
		catTemplates();
		
		IlpConjChecker ilp = new IlpConjChecker(ilpTemplates);
		ilp.checkIlpConjunction();
		
		validTemplates.addAll(ilp.getValidTemplates());
		if (!validTemplates.isEmpty()) return;
		
		PACChecker pac = new PACChecker(passValues, failValues, primTemplates);
		pac.checkPAC();
		
		validTemplates.addAll(pac.getValidTemplates());
	}
	
	public void catTemplates() {
		ilpTemplates = new ArrayList<SingleTemplate>();
		primTemplates = new ArrayList<SingleTemplate>();
		
		int size = allTemplates.size();
		for (int i = 0; i < size; i++) {
			SingleTemplate st = allTemplates.get(i);
			
			if (st instanceof OneNumIlpTemplate ||
					st instanceof TwoNumIlpTemplate) {
				ilpTemplates.add(st);
			} else {
				primTemplates.add(st);
			}
		}
	}
	
}
