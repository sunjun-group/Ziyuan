package invariant.templates;

import java.util.ArrayList;
import java.util.List;

import invariant.templates.onefeature.OnePrimIlpTemplate;
import invariant.templates.threefeatures.ThreePrimIlpTemplate;
import invariant.templates.twofeatures.TwoPrimIlpTemplate;
import sav.strategies.dto.execute.value.ExecValue;

public class ConjunctionTemplate extends CompositeTemplate {
	
	public ConjunctionTemplate() {
		super();
	}
	
	@Override
	public boolean check() {
		List<Integer> notSatisfiedIndex = new ArrayList<Integer>();
		SingleTemplate t1 = (SingleTemplate) templates.get(0);
		SingleTemplate t2 = (SingleTemplate) templates.get(1);
		
		List<List<ExecValue>> failExecValuesList1 = t1.getFailExecValuesList();
		
		for (int k = 0; k < failExecValuesList1.size(); k++) {
			if (!t1.checkFailValue(failExecValuesList1.get(k))) {
				notSatisfiedIndex.add(k);
			}
		}
		
		List<List<ExecValue>> failExecValuesList2 = t2.getFailExecValuesList();
		
		if (t2 instanceof OnePrimIlpTemplate ||
				t2 instanceof TwoPrimIlpTemplate ||
				t2 instanceof ThreePrimIlpTemplate) {
			List<List<ExecValue>> newFailExecValuesList2 = new ArrayList<List<ExecValue>>();
			for (int i = 0; i < failExecValuesList2.size(); i++) {
				if (notSatisfiedIndex.contains(i)) {
					newFailExecValuesList2.add(failExecValuesList2.get(i));
				}
			}
			
			return t2.check(t2.getPassExecValuesList(), newFailExecValuesList2);
		} else {
			for (int k : notSatisfiedIndex) {
				if (!t2.checkFailValue(failExecValuesList2.get(k))) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		String s = "";
		
		s += templates.get(0);
		for (int i = 1; i < templates.size(); i++) {
			s += " && " + templates.get(i);
		}
		
		return s;
	}

}
