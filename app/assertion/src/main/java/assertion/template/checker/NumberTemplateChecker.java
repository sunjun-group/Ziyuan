package assertion.template.checker;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import invariant.templates.SingleTemplate;
import invariant.templates.onefeature.OneNumEqTemplate;
import invariant.templates.onefeature.OneNumIlpTemplate;
import invariant.templates.onefeature.OneNumNeTemplate;
import invariant.templates.threefeatures.ThreeNumAddTemplate;
import invariant.templates.threefeatures.ThreeNumDivTemplate;
import invariant.templates.threefeatures.ThreeNumEqTemplate;
import invariant.templates.threefeatures.ThreeNumGcdTemplate;
import invariant.templates.threefeatures.ThreeNumModTemplate;
import invariant.templates.threefeatures.ThreeNumMulTemplate;
import invariant.templates.threefeatures.ThreeNumPowerTemplate;
import invariant.templates.threefeatures.ThreeNumSubTemplate;
import invariant.templates.twofeatures.TwoNumAbsTemplate;
import invariant.templates.twofeatures.TwoNumAddOverflowTemplate;
import invariant.templates.twofeatures.TwoNumCubeTemplate;
import invariant.templates.twofeatures.TwoNumEqTemplate;
import invariant.templates.twofeatures.TwoNumIlpTemplate;
import invariant.templates.twofeatures.TwoNumMulOverflowTemplate;
import invariant.templates.twofeatures.TwoNumSqrtTemplate;
import invariant.templates.twofeatures.TwoNumSquareTemplate;
import invariant.templates.twofeatures.TwoNumSubOverflowTemplate;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.execute.value.ExecValue;

public class NumberTemplateChecker extends TypeTemplateChecker {
	
	private static Logger log = LoggerFactory.getLogger(NumberTemplateChecker.class);

	@Override
	public boolean checkTemplates(List<List<ExecValue>> passValues, List<List<ExecValue>> failValues) {
		log.info("Number pass values: {}\n", passValues);
		log.info("Number fail values: {}\n", failValues);
		
		if (passValues.isEmpty() || failValues.isEmpty()) return false;
		
		if (checkOneFeatureTemplates(passValues, failValues)) return true;
		if (checkTwoFeaturesTemplates(passValues, failValues)) return true;
		if (checkThreeFeaturesTemplates(passValues, failValues)) return true;
		
		if (checkIlpTemplates(passValues, failValues)) return true;
		
		return false;
	}

	private boolean checkIlpTemplates(List<List<ExecValue>> passValues,
			List<List<ExecValue>> failValues) {
		SingleTemplate t = null;
		
		int n = passValues.get(0).size();
		for (int i = 0; i < n; i++) {
			List<List<ExecValue>> passEvl = new ArrayList<List<ExecValue>>();
			List<List<ExecValue>> failEvl = new ArrayList<List<ExecValue>>();

			for (List<ExecValue> evl : passValues) {
				passEvl.add(CollectionUtils.listOf(evl.get(i)));
			}

			for (List<ExecValue> evl : failValues) {
				failEvl.add(CollectionUtils.listOf(evl.get(i)));
			}
			
			t = new OneNumIlpTemplate(passEvl, failEvl);
			check(t);
		}
		
		if (n < 2) return false;
		
		for (int i = 0; i < n - 1; i++) {
			for (int j = i + 1; j < n; j++) {
				List<List<ExecValue>> passEvl = new ArrayList<List<ExecValue>>();
				List<List<ExecValue>> failEvl = new ArrayList<List<ExecValue>>();

				for (List<ExecValue> evl : passValues) {
					passEvl.add(CollectionUtils.listOf(evl.get(i), evl.get(j)));
				}

				for (List<ExecValue> evl : failValues) {
					failEvl.add(CollectionUtils.listOf(evl.get(i), evl.get(j)));
				}
				
				t = new TwoNumIlpTemplate(passEvl, failEvl);
				check(t);
			}
		}
		
		return false;
	}
	
	private boolean checkOneFeatureTemplates(List<List<ExecValue>> passValues,
			List<List<ExecValue>> failValues) {
		SingleTemplate t = null;

		int n = passValues.get(0).size();
		for (int i = 0; i < n; i++) {
			List<List<ExecValue>> passEvl = new ArrayList<List<ExecValue>>();
			List<List<ExecValue>> failEvl = new ArrayList<List<ExecValue>>();

			for (List<ExecValue> evl : passValues) {
				passEvl.add(CollectionUtils.listOf(evl.get(i)));
			}

			for (List<ExecValue> evl : failValues) {
				failEvl.add(CollectionUtils.listOf(evl.get(i)));
			}

			t = new OneNumEqTemplate(passEvl, failEvl);
			if (check(t)) return true;
			
			t = new OneNumNeTemplate(passEvl, failEvl);
			if (check(t)) return true;
			
//			t = new OneNumGte0Template(passEvl, failEvl);
//			check(t);
//			
//			t = new OneNumGt0Template(passEvl, failEvl);
//			check(t);	
		}
		
		return false;
	}

	private boolean checkTwoFeaturesTemplates(List<List<ExecValue>> passValues,
			List<List<ExecValue>> failValues) {
		SingleTemplate t = null;

		int n = passValues.get(0).size();
		if (n < 2) return false;

		// check template with commutativity
		for (int i = 0; i < n - 1; i++) {
			for (int j = i + 1; j < n; j++) {
				List<List<ExecValue>> passEvl = new ArrayList<List<ExecValue>>();
				List<List<ExecValue>> failEvl = new ArrayList<List<ExecValue>>();

				for (List<ExecValue> evl : passValues) {
					passEvl.add(CollectionUtils.listOf(evl.get(i), evl.get(j)));
				}

				for (List<ExecValue> evl : failValues) {
					failEvl.add(CollectionUtils.listOf(evl.get(i), evl.get(j)));
				}

				t = new TwoNumEqTemplate(passEvl, failEvl);
				if (check(t)) return true;
				
				t = new TwoNumAddOverflowTemplate(passEvl, failEvl);
				if (check(t)) return true;
				
				t = new TwoNumMulOverflowTemplate(passEvl, failEvl);
				if (check(t)) return true;
			}
		}

		// check template without commutativity
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i != j) {
					List<List<ExecValue>> passEvl = new ArrayList<List<ExecValue>>();
					List<List<ExecValue>> failEvl = new ArrayList<List<ExecValue>>();

					for (List<ExecValue> evl : passValues) {
						passEvl.add(CollectionUtils.listOf(evl.get(i), evl.get(j)));
					}

					for (List<ExecValue> evl : failValues) {
						failEvl.add(CollectionUtils.listOf(evl.get(i), evl.get(j)));
					}

					t = new TwoNumAbsTemplate(passEvl, failEvl);
					if (check(t)) return true;
					
					t = new TwoNumCubeTemplate(passEvl, failEvl);
					if (check(t)) return true;
					
					t = new TwoNumSqrtTemplate(passEvl, failEvl);
					if (check(t)) return true;
					
					t = new TwoNumSquareTemplate(passEvl, failEvl);
					if (check(t)) return true;
					
//					t = new TwoNumGteTemplate(passEvl, failEvl);
//					check(t);
//					
//					t = new TwoNumGtTemplate(passEvl, failEvl);
//					check(t);
					
					t = new TwoNumSubOverflowTemplate(passEvl, failEvl);
					if (check(t)) return true;
				}
			}
		}
		
		return false;
	}

	private boolean checkThreeFeaturesTemplates(List<List<ExecValue>> passValues,
			List<List<ExecValue>> failValues) {
		SingleTemplate t = null;

		int n = passValues.get(0).size();
		if (n < 3) return false;

		// check template with commutativity
		for (int i = 0; i < n - 2; i++) {
			for (int j = i + 1; j < n - 1; j++) {
				for (int k = j + 1; k < n; k++) {
					List<List<ExecValue>> passEvl = new ArrayList<List<ExecValue>>();
					List<List<ExecValue>> failEvl = new ArrayList<List<ExecValue>>();

					for (List<ExecValue> evl : passValues) {
						passEvl.add(CollectionUtils.listOf(evl.get(i), evl.get(j), evl.get(k)));
					}

					for (List<ExecValue> evl : failValues) {
						failEvl.add(CollectionUtils.listOf(evl.get(i), evl.get(j), evl.get(k)));
					}

					t = new ThreeNumEqTemplate(passEvl, failEvl);
					if (check(t)) return true;
					
//					t = new ThreeNumIlpTemplate(passEvl, failEvl);
//					check(t);
				}
			}
		}

		// check template without commutativity
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = j + 1; k < n; k++) {
					if (i != j && i != k) {
						List<List<ExecValue>> passEvl = new ArrayList<List<ExecValue>>();
						List<List<ExecValue>> failEvl = new ArrayList<List<ExecValue>>();

						for (List<ExecValue> evl : passValues) {
							passEvl.add(CollectionUtils.listOf(evl.get(i), evl.get(j), evl.get(k)));
						}

						for (List<ExecValue> evl : failValues) {
							failEvl.add(CollectionUtils.listOf(evl.get(i), evl.get(j), evl.get(k)));
						}

						t = new ThreeNumAddTemplate(passEvl, failEvl);
						if (check(t)) return true;
						
						t = new ThreeNumDivTemplate(passEvl, failEvl);
						if (check(t)) return true;
						
						t = new ThreeNumGcdTemplate(passEvl, failEvl);
						if (check(t)) return true;
						
						t = new ThreeNumModTemplate(passEvl, failEvl);
						if (check(t)) return true;
						
						t = new ThreeNumMulTemplate(passEvl, failEvl);
						if (check(t)) return true;
						
						t = new ThreeNumPowerTemplate(passEvl, failEvl);
						if (check(t)) return true;
						
						t = new ThreeNumSubTemplate(passEvl, failEvl);
						if (check(t)) return true;
					}
				}
			}
		}
		
		return false;
	}

}
