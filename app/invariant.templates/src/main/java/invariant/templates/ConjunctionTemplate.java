package invariant.templates;

import java.util.ArrayList;
import java.util.List;

import sav.strategies.dto.execute.value.ExecValue;

public class ConjunctionTemplate extends CompositeTemplate {
	
	public ConjunctionTemplate() {
		super();
	}
	
	@Override
	public boolean check() {
		System.out.println("Check conjunction");
		
		List<Integer> notSatisfiedIndex = new ArrayList<Integer>();
		SingleTemplate t1 = templates.get(0);
		SingleTemplate t2 = templates.get(1);
		
		List<List<ExecValue>> failExecValuesList1 = t1.getFailExecValuesList();
		
		for (int k = 0; k < failExecValuesList1.size(); k++) {
			if (!t1.checkFailValue(failExecValuesList1.get(k))) {
				notSatisfiedIndex.add(k);
			}
		}
		
		List<List<ExecValue>> failExecValuesList2 = t2.getFailExecValuesList();
		
		for (int k : notSatisfiedIndex) {
			if (!t2.checkFailValue(failExecValuesList2.get(k))) {
				return false;
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
