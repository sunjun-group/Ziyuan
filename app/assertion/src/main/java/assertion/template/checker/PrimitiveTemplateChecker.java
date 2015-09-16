package assertion.template.checker;

import java.util.ArrayList;
import java.util.List;

import assertion.template.Template;
import assertion.template.onefeature.OnePrimEqConstTemplate;
import assertion.template.onefeature.OnePrimIlpTemplate;
import assertion.template.onefeature.OnePrimNeConstTemplate;
import assertion.template.threefeatures.ThreePrimEqConstTemplate;
import assertion.template.threefeatures.ThreePrimIlpTemplate;
import assertion.template.threefeatures.ThreePrimMulTemplate;
import assertion.template.twofeatures.TwoPrimAbsTemplate;
import assertion.template.twofeatures.TwoPrimIlpTemplate;
import icsetlv.common.dto.ExecValue;
import sav.common.core.utils.CollectionUtils;

public class PrimitiveTemplateChecker extends TypeTemplateChecker {

	// private double[] sampleCoefs = {-1.0, 0, 1.0};

	public List<Template> checkThreeFeaturesTemplates(List<List<ExecValue>> passExecValuesList,
			List<List<ExecValue>> failExecValuesList) {
		List<Template> templates = new ArrayList<Template>();
		Template t = null;
		
		int n = passExecValuesList.get(0).size();
		if (n < 3) return templates;
		
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
			
					t = new ThreePrimEqConstTemplate(passEvl, failEvl);
					if (t.check()) templates.add(t);
					
					t = new ThreePrimIlpTemplate(passEvl, failEvl);
					if (t.check()) templates.add(t);
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
					
						t = new ThreePrimMulTemplate(passEvl, failEvl);
						if (t.check()) templates.add(t);
					}
				}
			}
		}
		
		return templates;
	}

	public List<Template> checkTwoFeaturesTemplates(List<List<ExecValue>> passExecValuesList,
			List<List<ExecValue>> failExecValuesList) {
		List<Template> templates = new ArrayList<Template>();
		Template t = null;
		
		int n = passExecValuesList.get(0).size();
		if (n < 2) return templates;
		
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
				if (t.check()) templates.add(t);
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
					
					t = new TwoPrimAbsTemplate(passEvl, failEvl);
					if (t.check()) templates.add(t);
				}
			}
		}
		
		return templates;
	}
	
	public List<Template> checkOneFeatureTemplates(List<List<ExecValue>> passExecValuesList,
			List<List<ExecValue>> failExecValuesList) {
		List<Template> templates = new ArrayList<Template>();
		Template t = null;
		
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
			if (t.check()) templates.add(t);
			
			t = new OnePrimNeConstTemplate(passEvl, failEvl);
			if (t.check()) templates.add(t);
			
//			t = new OnePrimRangeTemplate(passEvl, failEvl);
//			if (t.check()) templates.add(t);
			
			t = new OnePrimIlpTemplate(passEvl, failEvl);
			if (t.check()) templates.add(t);
		}
		
		return templates;
	}
	
	@Override
	public List<Template> checkTemplates(List<List<ExecValue>> passExecValuesList,
			List<List<ExecValue>> failExecValuesList) {
		List<Template> templates = new ArrayList<Template>();
		
		if (passExecValuesList.isEmpty() || failExecValuesList.isEmpty()) {
			return templates;
		}
		
		templates.addAll(checkOneFeatureTemplates(passExecValuesList, failExecValuesList));
		templates.addAll(checkTwoFeaturesTemplates(passExecValuesList, failExecValuesList));
		
		/*
		List<List<Double>> coefsList = new ArrayList<List<Double>>();
		getAllPossibleCoefs(coefsList, new ArrayList<Double>(), passExecValuesList.get(0).size());
		
		Template t = null;
		
		for (List<Double> coefs : coefsList) {
			t = new IlpTemplate(coefs, passExecValuesList, failExecValuesList,
					Operator.GE, 0.0);
			if (t.check()) templates.add(t);
			
			t = new IlpTemplate(coefs, passExecValuesList, failExecValuesList,
					Operator.GT, 0.0);
			if (t.check()) templates.add(t);
			
			t = new IlpTemplate(coefs, passExecValuesList, failExecValuesList,
					Operator.EQ, 0.0);
			if (t.check()) templates.add(t);
			
			t = new IlpTemplate(coefs, passExecValuesList, failExecValuesList,
					Operator.NE, 0.0);
			if (t.check()) templates.add(t);
		}
		
		if (passExecValuesList.get(0).size() == 2) {
			t = new ModConstTemplate(passExecValuesList, failExecValuesList, 2);
			if (t.check()) templates.add(t);
		}
		*/
		
		return templates;
	}
	
	/*
	private void getAllPossibleCoefs(List<List<Double>> coefsList, List<Double> coefs, int index) {
		if (index == 0) {
			List<Double> temp = new ArrayList<Double>(coefs);
			coefsList.add(temp);
		} else {
			for (int i = 0; i < sampleCoefs.length; i++) {
				coefs.add(0, sampleCoefs[i]);
				getAllPossibleCoefs(coefsList, coefs, index - 1);
				coefs.remove(0);
			}
		}
	}
	*/
	
}
