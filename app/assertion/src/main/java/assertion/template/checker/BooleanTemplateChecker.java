package assertion.template.checker;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import invariant.templates.SingleTemplate;
import invariant.templates.onefeature.OneBoolEq0Template;
import invariant.templates.onefeature.OneBoolEq1Template;
import invariant.templates.twofeatures.TwoBoolAndTemplate;
import invariant.templates.twofeatures.TwoBoolOrTemplate;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.execute.value.ExecValue;

public class BooleanTemplateChecker extends TypeTemplateChecker {
	
	private static Logger log = LoggerFactory.getLogger(BooleanTemplateChecker.class);

	@Override
	public boolean checkTemplates(List<List<ExecValue>> passValues, List<List<ExecValue>> failValues) {
//		log.info("Boolean pass values: {}\n", passValues);
//		log.info("Boolean fail values: {}\n", failValues);
		
		if (passValues.isEmpty() || failValues.isEmpty()) return false;
		
		if (checkOneFeatureTemplates(passValues, failValues)) return true;
		// checkTwoFeaturesTemplates(passValues, failValues);
		
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

			t = new OneBoolEq1Template(passEvl, failEvl);
			if (check(t)) return true;
			
			t = new OneBoolEq0Template(passEvl, failEvl);
			if (check(t)) return true;
		}
		
		return false;
	}
	
	private void checkTwoFeaturesTemplates(List<List<ExecValue>> passExecValuesList,
			List<List<ExecValue>> failExecValuesList) {
		SingleTemplate t = null;

		int n = passExecValuesList.get(0).size();
		if (n < 2) return;

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

				t = new TwoBoolAndTemplate(passEvl, failEvl);
				check(t);
				
				t = new TwoBoolOrTemplate(passEvl, failEvl);
				check(t);
			}
		}
	}
	
}
