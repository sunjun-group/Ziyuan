package assertion.template.checker;

import java.util.ArrayList;
import java.util.List;

import invariant.templates.CompositeTemplate;
import invariant.templates.ConjunctionTemplate;
import invariant.templates.DisjunctionTemplate;
import invariant.templates.SingleTemplate;

public class CompositeTemplateChecker {

	private List<SingleTemplate> satifiedPassTemplates;
	
	private List<SingleTemplate> satifiedFailTemplates;

	private List<CompositeTemplate> compositeTemplates;
	
	public CompositeTemplateChecker(List<SingleTemplate> satifiedPassTemplates,
			List<SingleTemplate> satifiedFailTemplates) {
		this.satifiedPassTemplates = satifiedPassTemplates;
		this.satifiedFailTemplates = satifiedFailTemplates;
		compositeTemplates = new ArrayList<CompositeTemplate>();
	}
	
	public List<CompositeTemplate> getCompositeTemplates() {
		return compositeTemplates;
	}
	
	public void checkCompositeTemplates() {
		checkConjunction();
		checkDisjunction();
	}
	
	private void checkConjunction() {
		int size = satifiedPassTemplates.size();
		
		if (size < 2) return;
		for (int i = 0; i < size - 1; i++) {
			for (int j = i + 1; j < size; j++) {
				SingleTemplate t1 = satifiedPassTemplates.get(i);
				SingleTemplate t2 = satifiedPassTemplates.get(j);
				
				CompositeTemplate ct = new ConjunctionTemplate();
				ct.addTemplates(t1, t2);
				if (ct.check()) compositeTemplates.add(ct);
			}
		}
	}
	
	private void checkDisjunction() {
		int size = satifiedFailTemplates.size();
		
		if (size < 2) return;
		for (int i = 0; i < size - 1; i++) {
			for (int j = i + 1; j < size; j++) {
				SingleTemplate t1 = satifiedFailTemplates.get(i);
				SingleTemplate t2 = satifiedFailTemplates.get(j);
				
				CompositeTemplate ct = new DisjunctionTemplate();
				ct.addTemplates(t1, t2);
				if (ct.check()) compositeTemplates.add(ct);
			}
		}
	}
	
}
