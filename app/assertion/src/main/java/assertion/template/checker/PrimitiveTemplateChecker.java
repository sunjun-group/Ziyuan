package assertion.template.checker;

import java.util.ArrayList;
import java.util.List;

import invariant.templates.SingleTemplate;
import invariant.templates.onefeature.OnePrimEqConstTemplate;
import invariant.templates.onefeature.OnePrimNeConstTemplate;
import invariant.templates.threefeatures.ThreePrimIlpTemplate;
import invariant.templates.twofeatures.TwoPrimIlpTemplate;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.execute.value.ExecValue;

public class PrimitiveTemplateChecker extends TypeTemplateChecker {

	@Override
	public void checkTemplates(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		if (!passExecValuesList.isEmpty() && !failExecValuesList.isEmpty()) {
			checkOneFeatureTemplates(passExecValuesList, failExecValuesList);
			// checkTwoFeaturesTemplates(passExecValuesList, failExecValuesList);
			// checkThreeFeaturesTemplates(passExecValuesList, failExecValuesList);
		}

	}

	private void checkOneFeatureTemplates(List<List<ExecValue>> passExecValuesList,
			List<List<ExecValue>> failExecValuesList) {
		SingleTemplate t = null;

		int n = passExecValuesList.get(0).size();
		for (int i = 0; i < n; i++) {
			List<List<ExecValue>> passEvl = new ArrayList<List<ExecValue>>();
			List<List<ExecValue>> failEvl = new ArrayList<List<ExecValue>>();

			for (List<ExecValue> evl : passExecValuesList) {
				passEvl.add(CollectionUtils.listOf(evl.get(i)));
			}

			for (List<ExecValue> evl : failExecValuesList) {
				failEvl.add(CollectionUtils.listOf(evl.get(i)));
			}

			t = new OnePrimEqConstTemplate(passEvl, failEvl);
			check(t);

			t = new OnePrimNeConstTemplate(passEvl, failEvl);
			check(t);

			// t = new OnePrimRangeTemplate(passEvl, failEvl);
			// if (t.check()) templates.add(t);

			// t = new OnePrimIlpTemplate(passEvl, failEvl);
			// if (t.check()) templates.add(t);
			// if (t.isSatisfiedAllPassValues())
			// satifiedAllPassValuesTemplates.add(t);
			// if (t.isSatisfiedAllFailValues())
			// satifiedAllFailValuesTemplates.add(t);
		}
	}

	private void checkTwoFeaturesTemplates(List<List<ExecValue>> passExecValuesList,
			List<List<ExecValue>> failExecValuesList) {
		SingleTemplate t = null;

		int n = passExecValuesList.get(0).size();
		if (n < 2)
			return;

		// check template with commutativity
		for (int i = 0; i < n - 1; i++) {
			for (int j = i + 1; j < n; j++) {
				List<List<ExecValue>> passEvl = new ArrayList<List<ExecValue>>();
				List<List<ExecValue>> failEvl = new ArrayList<List<ExecValue>>();

				for (List<ExecValue> evl : passExecValuesList) {
					passEvl.add(CollectionUtils.listOf(evl.get(i), evl.get(j)));
				}

				for (List<ExecValue> evl : failExecValuesList) {
					failEvl.add(CollectionUtils.listOf(evl.get(i), evl.get(j)));
				}

				t = new TwoPrimIlpTemplate(passEvl, failEvl);
				if (t.check())
					singleTemplates.add(t);
			}
		}

		// check template with commutativity
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i != j) {
					List<List<ExecValue>> passEvl = new ArrayList<List<ExecValue>>();
					List<List<ExecValue>> failEvl = new ArrayList<List<ExecValue>>();

					for (List<ExecValue> evl : passExecValuesList) {
						passEvl.add(CollectionUtils.listOf(evl.get(i), evl.get(j)));
					}

					for (List<ExecValue> evl : failExecValuesList) {
						failEvl.add(CollectionUtils.listOf(evl.get(i), evl.get(j)));
					}

					// t = new TwoPrimAbsTemplate(passEvl, failEvl);
					// if (t.check()) templates.add(t);
				}
			}
		}
	}

	private void checkThreeFeaturesTemplates(List<List<ExecValue>> passExecValuesList,
			List<List<ExecValue>> failExecValuesList) {
		SingleTemplate t = null;

		int n = passExecValuesList.get(0).size();
		if (n < 3)
			return;

		// check template with commutativity
		for (int i = 0; i < n - 2; i++) {
			for (int j = i + 1; j < n - 1; j++) {
				for (int k = j + 1; k < n; k++) {
					List<List<ExecValue>> passEvl = new ArrayList<List<ExecValue>>();
					List<List<ExecValue>> failEvl = new ArrayList<List<ExecValue>>();

					for (List<ExecValue> evl : passExecValuesList) {
						passEvl.add(CollectionUtils.listOf(evl.get(i), evl.get(j), evl.get(k)));
					}

					for (List<ExecValue> evl : failExecValuesList) {
						failEvl.add(CollectionUtils.listOf(evl.get(i), evl.get(j), evl.get(k)));
					}

					// t = new ThreePrimEqConstTemplate(passEvl, failEvl);
					// if (t.check()) templates.add(t);

					t = new ThreePrimIlpTemplate(passEvl, failEvl);
					if (t.check())
						singleTemplates.add(t);
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

						for (List<ExecValue> evl : passExecValuesList) {
							passEvl.add(CollectionUtils.listOf(evl.get(i), evl.get(j), evl.get(k)));
						}

						for (List<ExecValue> evl : failExecValuesList) {
							failEvl.add(CollectionUtils.listOf(evl.get(i), evl.get(j), evl.get(k)));
						}

						// t = new ThreePrimMulTemplate(passEvl, failEvl);
						// if (t.check()) templates.add(t);
					}
				}
			}
		}
	}

}
