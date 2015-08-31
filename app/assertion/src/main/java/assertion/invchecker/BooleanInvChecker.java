package assertion.invchecker;

import java.util.ArrayList;
import java.util.List;

import icsetlv.common.dto.ExecValue;
import sav.common.core.formula.Formula;
import sav.common.core.formula.Operator;

public class BooleanInvChecker extends TypeInvChecker {

	@Override
	public List<Formula> check(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		List<Formula> invs = new ArrayList<Formula>();
		
		if (passExecValuesList.isEmpty() || failExecValuesList.isEmpty()) {
			// System.out.println("Nothing to check with boolean!");
			return invs;
		} else {
			// check each group of boolean variables
			for (int i = 0; i < passExecValuesList.get(0).size(); i++) {
				List<Double> passValues = new ArrayList<Double>();
				List<Double> failValues = new ArrayList<Double>();
				
				for (List<ExecValue> evl : passExecValuesList) {
					passValues.add(evl.get(i).getDoubleVal());
				}
				
				for (List<ExecValue> evl : failExecValuesList) {
					failValues.add(evl.get(i).getDoubleVal());
				}
				
				if (checkFormula(passValues, failValues, Operator.EQ, 1.0)) {
					invs.add(createFormula(passExecValuesList.get(0).get(i), true));
				}
				
				if (checkFormula(passValues, failValues, Operator.EQ, 0.0)) {
					invs.add(createFormula(passExecValuesList.get(0).get(i), false));
				}
			}
			
			return invs;
		}
 	}
	
}
