package assertion.invchecker;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import icsetlv.common.dto.ExecValue;
import sav.common.core.formula.Formula;
import sav.common.core.formula.Operator;

public class ReferenceInvChecker extends TypeInvChecker {

	public void flattenExecValue(List<ExecValue> evl, ExecValue ev) {
		for (ExecValue child : ev.getChildren()) {
			if (child.getChildren() != null) {
				flattenExecValue(evl, child);
			} else {
				evl.add(child);
			}
		}
	}
	
	public void flattenExecValues(List<List<ExecValue>> newExecValuesList, List<List<ExecValue>> execValuesList) {
		for (List<ExecValue> evl : execValuesList) {
			for (ExecValue ev : evl) {
				List<ExecValue> temp = new ArrayList<ExecValue>();
				flattenExecValue(temp, ev);
				newExecValuesList.add(temp);
			}
		}
	}
	
	public List<Formula> createFormulas(List<ExecValue> passExecValues, List<ExecValue> failExecValues) {
		List<Formula> invs = new ArrayList<Formula>();
		
		List<Double> passValues = new ArrayList<Double>();
		List<Double> failValues = new ArrayList<Double>();
		
		for (ExecValue ev : passExecValues) {
			passValues.add(ev.getDoubleVal());
		}
		
		for (ExecValue ev : failExecValues) {
			failValues.add(ev.getDoubleVal());
		}
		
		switch (passExecValues.get(0).getType()) {
		case BOOLEAN:
			// check formula = true
			if (checkFormula(passValues, failValues, Operator.EQ, 1.0)) {
				invs.add(createFormula(passExecValues.get(0), true));
			}
			
			// check formula = false
			if (checkFormula(passValues, failValues, Operator.EQ, 0.0)) {
				invs.add(createFormula(passExecValues.get(0), false));
			}
			
			break;
		case PRIMITIVE:
			// check formula >= 0.0
			if (checkFormula(passValues, failValues, Operator.GE, 0.0)) {
				invs.add(createFormula(sav.common.core.utils.CollectionUtils.listOf(1),
						sav.common.core.utils.CollectionUtils.listOf(passExecValues.get(0)),
						Operator.GE, 0.0));
			}
			
			// check formula > 0.0
			if (checkFormula(passValues, failValues, Operator.GE, 1.0)) {
				invs.add(createFormula(sav.common.core.utils.CollectionUtils.listOf(1),
						sav.common.core.utils.CollectionUtils.listOf(passExecValues.get(0)),
						Operator.GE, 1.0));
			}
			
			break;
		default:
			break;
		}
		
		return invs;
	}
	
	@Override
	public List<Formula> check(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		List<Formula> invs = new ArrayList<Formula>();
		
		if (passExecValuesList.isEmpty() || failExecValuesList.isEmpty()) {
			// System.out.println("Nothing to check with reference!");
			return invs;
		} else {
			// flatten all values to get children values
			List<List<ExecValue>> flattenPassExecValuesList = new ArrayList<List<ExecValue>>();
			List<List<ExecValue>> flattenFailExecValuesList = new ArrayList<List<ExecValue>>();
			
			flattenExecValues(flattenPassExecValuesList, passExecValuesList);
			flattenExecValues(flattenFailExecValuesList, failExecValuesList);
			
			// check each group of children values
			for (int i = 0; i < flattenPassExecValuesList.get(0).size(); i++) {
				List<ExecValue> passExecValues = new ArrayList<ExecValue>();
				List<ExecValue> failExecValues = new ArrayList<ExecValue>();
				
				for (List<ExecValue> evl : flattenPassExecValuesList) {
					passExecValues.add(evl.get(i));
				}
				
				for (List<ExecValue> evl : flattenFailExecValuesList) {
					failExecValues.add(evl.get(i));
				}
				
				invs.addAll(createFormulas(passExecValues, failExecValues));
			}
			
			return invs;
		}
	}
	
}
