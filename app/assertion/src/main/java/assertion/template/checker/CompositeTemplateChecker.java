package assertion.template.checker;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import invariant.templates.CompositeTemplate;
import invariant.templates.SingleTemplate;
import invariant.templates.onefeature.OneBoolEq0Template;
import invariant.templates.onefeature.OneBoolEq1Template;
import invariant.templates.onefeature.OneNumGt0Template;
import invariant.templates.onefeature.OneNumGte0Template;
import invariant.templates.onefeature.OneNumIlpTemplate;
import invariant.templates.onefeature.OneNumNeMinTemplate;
import invariant.templates.threefeatures.ThreeNumAddTemplate;
import invariant.templates.threefeatures.ThreeNumDivTemplate;
import invariant.templates.threefeatures.ThreeNumGcdTemplate;
import invariant.templates.threefeatures.ThreeNumModTemplate;
import invariant.templates.threefeatures.ThreeNumMulTemplate;
import invariant.templates.threefeatures.ThreeNumPowerTemplate;
import invariant.templates.threefeatures.ThreeNumSubTemplate;
import invariant.templates.twofeatures.TwoNumAbsTemplate;
import invariant.templates.twofeatures.TwoNumAddOverflowTemplate;
import invariant.templates.twofeatures.TwoNumCubeTemplate;
import invariant.templates.twofeatures.TwoNumEqualTemplate;
import invariant.templates.twofeatures.TwoNumGtTemplate;
import invariant.templates.twofeatures.TwoNumGteTemplate;
import invariant.templates.twofeatures.TwoNumIlpTemplate;
import invariant.templates.twofeatures.TwoNumMulOverflowTemplate;
import invariant.templates.twofeatures.TwoNumSqrtTemplate;
import invariant.templates.twofeatures.TwoNumSquareTemplate;
import invariant.templates.twofeatures.TwoNumSubOverflowTemplate;
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
			} else if (st instanceof OneBoolEq0Template ||
					st instanceof OneBoolEq1Template ||
					st instanceof OneNumGt0Template ||
					st instanceof OneNumGte0Template ||
					st instanceof OneNumNeMinTemplate ||
					st instanceof TwoNumEqualTemplate ||
					st instanceof TwoNumAbsTemplate ||
					st instanceof TwoNumCubeTemplate ||
					st instanceof TwoNumSqrtTemplate ||
					st instanceof TwoNumSquareTemplate ||
					st instanceof TwoNumGteTemplate ||
					st instanceof TwoNumGtTemplate ||
					st instanceof TwoNumAddOverflowTemplate ||
					st instanceof TwoNumSubOverflowTemplate ||
					st instanceof TwoNumMulOverflowTemplate ||
					st instanceof ThreeNumAddTemplate ||
					st instanceof ThreeNumDivTemplate ||
					st instanceof ThreeNumGcdTemplate ||
					st instanceof ThreeNumModTemplate ||
					st instanceof ThreeNumMulTemplate ||
					st instanceof ThreeNumPowerTemplate ||
					st instanceof ThreeNumSubTemplate
					) {
				primTemplates.add(st);
			}
		}
	}
	
}
