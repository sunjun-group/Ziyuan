package assertion.template.checker;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import invariant.templates.SingleTemplate;
import invariant.templates.onefeature.OneBoolEq0Template;
import invariant.templates.onefeature.OneBoolEq1Template;
import sav.common.core.utils.CollectionUtils;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.dto.execute.value.ExecValue;

public class BooleanTemplateChecker extends TypeTemplateChecker {
	
	private static Logger log = LoggerFactory.getLogger(BooleanTemplateChecker.class);

	@Override
	public boolean checkTemplates(List<List<ExecValue>> passValues, List<List<ExecValue>> failValues) throws SAVExecutionTimeOutException {
//		log.info("Boolean pass values: {}\n", passValues);
//		log.info("Boolean fail values: {}\n", failValues);
		
		if (passValues.isEmpty() || failValues.isEmpty()) return false;
		
		if (checkOneFeatureTemplates(passValues, failValues)) return true;
		
		return false;
	}
	
	private boolean checkOneFeatureTemplates(List<List<ExecValue>> passValues,
			List<List<ExecValue>> failValues) throws SAVExecutionTimeOutException {
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
	
}
