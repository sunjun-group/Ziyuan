package assertion.template.checker;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import invariant.templates.SingleTemplate;
import invariant.templates.onefeature.OneNumEqTemplate;
import invariant.templates.onefeature.OneNumGt0Template;
import invariant.templates.onefeature.OneNumGte0Template;
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
import invariant.templates.twofeatures.TwoNumGtTemplate;
import invariant.templates.twofeatures.TwoNumGteTemplate;
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
	public void checkTemplates(List<List<ExecValue>> passValues, List<List<ExecValue>> failValues) {
		log.info("Number pass values: {}\n", passValues);
		log.info("Number fail values: {}\n", failValues);
		
		if (passValues.isEmpty() || failValues.isEmpty()) return;
		
		checkOneFeatureTemplates(passValues, failValues);
		checkTwoFeaturesTemplates(passValues, failValues);
		checkThreeFeaturesTemplates(passValues, failValues);
	}

	private void checkOneFeatureTemplates(List<List<ExecValue>> passValues,
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
			check(t);
			
			t = new OneNumNeTemplate(passEvl, failEvl);
			check(t);
			
//			t = new OneNumGte0Template(passEvl, failEvl);
//			check(t);
//			
//			t = new OneNumGt0Template(passEvl, failEvl);
//			check(t);
			
			t = new OneNumIlpTemplate(passEvl, failEvl);
			check(t);
		}
	}

	private void checkTwoFeaturesTemplates(List<List<ExecValue>> passValues,
			List<List<ExecValue>> failValues) {
		SingleTemplate t = null;

		int n = passValues.get(0).size();
		if (n < 2) return;

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
				check(t);
				
				t = new TwoNumAddOverflowTemplate(passEvl, failEvl);
				check(t);
				
				t = new TwoNumMulOverflowTemplate(passEvl, failEvl);
				check(t);
				
				t = new TwoNumIlpTemplate(passEvl, failEvl);
				check(t);
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
					check(t);
					
					t = new TwoNumCubeTemplate(passEvl, failEvl);
					check(t);
					
					t = new TwoNumSqrtTemplate(passEvl, failEvl);
					check(t);
					
					t = new TwoNumSquareTemplate(passEvl, failEvl);
					check(t);
					
//					t = new TwoNumGteTemplate(passEvl, failEvl);
//					check(t);
//					
//					t = new TwoNumGtTemplate(passEvl, failEvl);
//					check(t);
					
					t = new TwoNumSubOverflowTemplate(passEvl, failEvl);
					check(t);
				}
			}
		}
	}

	private void checkThreeFeaturesTemplates(List<List<ExecValue>> passValues,
			List<List<ExecValue>> failValues) {
		SingleTemplate t = null;

		int n = passValues.get(0).size();
		if (n < 3) return;

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
					check(t);
					
//					t = new ThreePrimIlpTemplate(passEvl, failEvl);
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
						check(t);
						
						t = new ThreeNumDivTemplate(passEvl, failEvl);
						check(t);
						
						t = new ThreeNumGcdTemplate(passEvl, failEvl);
						check(t);
						
						t = new ThreeNumModTemplate(passEvl, failEvl);
						check(t);
						
						t = new ThreeNumMulTemplate(passEvl, failEvl);
						check(t);
						
						t = new ThreeNumPowerTemplate(passEvl, failEvl);
						check(t);
						
						t = new ThreeNumSubTemplate(passEvl, failEvl);
						check(t);
					}
				}
			}
		}
	}

}
