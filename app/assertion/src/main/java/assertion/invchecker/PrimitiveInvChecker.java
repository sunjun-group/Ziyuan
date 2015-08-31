package assertion.invchecker;

import java.util.ArrayList;
import java.util.List;

import icsetlv.common.dto.ExecValue;
import sav.common.core.formula.Formula;
import sav.common.core.formula.LIAAtom;
import sav.common.core.formula.LIATerm;
import sav.common.core.formula.Operator;
import sav.common.core.formula.StringVar;

public class PrimitiveInvChecker extends TypeInvChecker {

	public int[] sampleCoefs = {-1, 0, 1};
	
	public void getAllPossibleCoefs(List<List<Integer>> coefs, List<Integer> coef, int index) {
		if (index == 0) {
			List<Integer> temp = new ArrayList<Integer>(coef);
			coefs.add(temp);
		} else {
			for (int i = 0; i < sampleCoefs.length; i++) {
				coef.add(0, sampleCoefs[i]);
				getAllPossibleCoefs(coefs, coef, index - 1);
				coef.remove(0);
			}
		}
	}
	
	@Override
	public List<Formula> check(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		List<Formula> invs = new ArrayList<Formula>();
		
		if (passExecValuesList.isEmpty() || failExecValuesList.isEmpty()) {
			// System.out.println("Nothing to check with primitive!");
			return invs;
		} else {
			// for a list of primitive variables get all possible coefs for the list
			List<List<Integer>> coefs = new ArrayList<List<Integer>>();
			getAllPossibleCoefs(coefs, new ArrayList<Integer>(), passExecValuesList.get(0).size());
			
			// System.out.println("All coefs: " + coefs);
			
			for (List<Integer> coef : coefs) {
				// calculate pass and fail values according to each coef
				List<Double> passValues = new ArrayList<Double>();
				List<Double> failValues = new ArrayList<Double>();
				
				for (List<ExecValue> evl : passExecValuesList) {
					Double value = 0.0;
					for (int i = 0; i < evl.size(); i++) {
						value += coef.get(i) * evl.get(i).getDoubleVal();
					}
					passValues.add(value);
				}
				
				for (List<ExecValue> evl : failExecValuesList) {
					Double value = 0.0;
					for (int i = 0; i < evl.size(); i++) {
						value += coef.get(i) * evl.get(i).getDoubleVal();
					}
					failValues.add(value);
				}
				
				// check formula >= 0.0
				if (checkFormula(passValues, failValues, Operator.GE, 0.0)) {
					invs.add(createFormula(coef, passExecValuesList.get(0), Operator.GE, 0.0));
				}
				
				// check formula >= 1.0
				if (checkFormula(passValues, failValues, Operator.GT, 0.0)) {
					invs.add(createFormula(coef, passExecValuesList.get(0), Operator.GE, 1.0));
				}
			}
			
			return invs;
		}
 	}
	
}
