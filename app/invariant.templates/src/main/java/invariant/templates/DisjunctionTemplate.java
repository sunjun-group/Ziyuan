package invariant.templates;

import java.util.ArrayList;
import java.util.List;

import sav.strategies.dto.execute.value.ExecValue;

public class DisjunctionTemplate extends CompositeTemplate {
	
	public DisjunctionTemplate() {
		super();
	}
	
	@Override
	public boolean check() {
		List<Integer> notSatisfiedIndex = new ArrayList<Integer>();
		SingleTemplate t1 = templates.get(0);
		SingleTemplate t2 = templates.get(1);
		
		List<List<ExecValue>> passExecValuesList1 = t1.getPassExecValuesList();
		
		for (int k = 0; k < passExecValuesList1.size(); k++) {
			if (!t1.checkPassValue(passExecValuesList1.get(k))) {
				notSatisfiedIndex.add(k);
			}
		}
		
		List<List<ExecValue>> passExecValuesList2 = t2.getPassExecValuesList();
		
		for (int k : notSatisfiedIndex) {
			if (!t2.checkPassValue(passExecValuesList2.get(k))) {
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
			s += " || " + templates.get(i);
		}
		
		return s;
	}

}
