package assertion.invchecker;

import java.util.ArrayList;
import java.util.List;

import icsetlv.common.dto.BreakpointData;
import icsetlv.common.dto.BreakpointValue;
import icsetlv.common.dto.ExecValue;
import sav.common.core.Pair;
import sav.common.core.formula.Formula;
import sav.common.core.formula.LIAAtom;
import sav.common.core.formula.LIATerm;
import sav.common.core.formula.Operator;
import sav.common.core.formula.StringVar;

public class InvChecker {

	
	public void flattenValues(List<ExecValue> props, ExecValue ev) {
		for (ExecValue child : ev.getChildren()) {
			if (child.getChildren() != null) {
				flattenValues(props, child);
			} else {
				props.add(child);
			}
		}
	}
	
	
	public void classifyExecValues(List<List<ExecValue>> referenceExecValuesList,
			List<List<ExecValue>> booleanExecValuesList,
			List<List<ExecValue>> stringExecValuesList,
			List<List<ExecValue>> primitiveExecValuesList,
			List<List<ExecValue>> arrayExecValuesList,
			List<List<ExecValue>> execValuesList)
	{
		for (List<ExecValue> evl : execValuesList) {
			List<ExecValue> referenceExecValues = new ArrayList<ExecValue>();
			List<ExecValue> booleanExecValues = new ArrayList<ExecValue>();
			List<ExecValue> stringExecValues = new ArrayList<ExecValue>();
			List<ExecValue> primitiveExecValues = new ArrayList<ExecValue>();
			List<ExecValue> arrayExecValues = new ArrayList<ExecValue>();
			
			for (ExecValue ev : evl) {
				switch(ev.getType()) {
				case REFERENCE:
					referenceExecValues.add(ev);
					break;
				case BOOLEAN:
					booleanExecValues.add(ev);
					break;
				case STRING:
					stringExecValues.add(ev);
					break;
				case PRIMITIVE:
					primitiveExecValues.add(ev);
					break;
				case ARRAY:
					arrayExecValues.add(ev);
					break;
				default:
					break;
				}
			}
			
			if (!referenceExecValues.isEmpty()) referenceExecValuesList.add(referenceExecValues);
			if (!booleanExecValues.isEmpty()) booleanExecValuesList.add(booleanExecValues);
			if (!stringExecValues.isEmpty()) stringExecValuesList.add(stringExecValues);
			if (!primitiveExecValues.isEmpty()) primitiveExecValuesList.add(primitiveExecValues);
			if (!arrayExecValues.isEmpty()) arrayExecValuesList.add(arrayExecValues);
		}
	}
	
	public List<Formula> checkWithExecValues(List<List<ExecValue>> origPassExecValuesList,
			List<List<ExecValue>> origFailExecValuesList) {
		System.out.println("Pass values:");
		System.out.println(origPassExecValuesList + "\n");
		
		System.out.println("Fail values;");
		System.out.println(origFailExecValuesList + "\n");
		
		// List<List<ExecValue>> passExecValuesList = origPassExecValuesList;
		List<List<ExecValue>> passExecValuesList = new ArrayList<List<ExecValue>>();
		for (List<ExecValue> evl : origPassExecValuesList) {
			List<ExecValue> newEvl = new ArrayList<ExecValue>();
			
			for (ExecValue ev : evl) {
				switch (ev.getType()) {
				case REFERENCE:
					List<ExecValue> newValues = new ArrayList<ExecValue>();
					flattenValues(newValues, ev);
					newEvl.addAll(newValues);
					break;
				default:
					newEvl.add(ev);
					break;
				}
			}
			
			passExecValuesList.add(newEvl);
		}
		
		// List<List<ExecValue>> failExecValuesList = origFailExecValuesList;
		List<List<ExecValue>> failExecValuesList = new ArrayList<List<ExecValue>>();
		for (List<ExecValue> evl : origFailExecValuesList) {
			List<ExecValue> newEvl = new ArrayList<ExecValue>();
					
			for (ExecValue ev : evl) {
				switch (ev.getType()) {
				case REFERENCE:
					List<ExecValue> newValues = new ArrayList<ExecValue>();
					flattenValues(newValues, ev);
					newEvl.addAll(newValues);
					break;
				default:
					newEvl.add(ev);
					break;
				}
			}
					
			failExecValuesList.add(newEvl);
		}
		
		// classify exec values according to variables' types
		List<List<ExecValue>> passReferenceExecValuesList = new ArrayList<List<ExecValue>>();
		List<List<ExecValue>> failReferenceExecValuesList = new ArrayList<List<ExecValue>>();

		List<List<ExecValue>> passBooleanExecValuesList = new ArrayList<List<ExecValue>>();
		List<List<ExecValue>> failBooleanExecValuesList = new ArrayList<List<ExecValue>>();
		
		List<List<ExecValue>> passStringExecValuesList = new ArrayList<List<ExecValue>>();
		List<List<ExecValue>> failStringExecValuesList = new ArrayList<List<ExecValue>>();
		
		List<List<ExecValue>> passPrimitiveExecValuesList = new ArrayList<List<ExecValue>>();
		List<List<ExecValue>> failPrimitiveExecValuesList = new ArrayList<List<ExecValue>>();

		List<List<ExecValue>> passArrayExecValuesList = new ArrayList<List<ExecValue>>();
		List<List<ExecValue>> failArrayExecValuesList = new ArrayList<List<ExecValue>>();
		
		classifyExecValues(passReferenceExecValuesList, passBooleanExecValuesList, passStringExecValuesList,
				passPrimitiveExecValuesList, passArrayExecValuesList, passExecValuesList);
		classifyExecValues(failReferenceExecValuesList, failBooleanExecValuesList, failStringExecValuesList,
				failPrimitiveExecValuesList, failArrayExecValuesList, failExecValuesList);
		
		// check pass and fail values with template inv
		TypeInvChecker checker = null;

		checker = new ReferenceInvChecker();
		List<Formula> referenceInvs = checker.check(passReferenceExecValuesList, failReferenceExecValuesList);
		
		checker = new BooleanInvChecker();
		List<Formula> booleanInvs = checker.check(passBooleanExecValuesList, failBooleanExecValuesList);
		
		checker = new PrimitiveInvChecker();
		List<Formula> primitiveInvs = checker.check(passPrimitiveExecValuesList, failPrimitiveExecValuesList);
				
		List<Formula> invs = new ArrayList<Formula>();
		invs.addAll(referenceInvs);
		invs.addAll(booleanInvs);
		invs.addAll(primitiveInvs);
		
		// System.out.println(invs);
		
		return invs;
	}
	
	public Pair<BreakpointData, List<Formula>> check(BreakpointData bkpData) {
		List<List<ExecValue>> passExecValuesList = new ArrayList<List<ExecValue>>();
		List<List<ExecValue>> failExecValuesList = new ArrayList<List<ExecValue>>();
			
		// get pass values
		for (BreakpointValue bv : bkpData.getPassValues()) {
			passExecValuesList.add(bv.getChildren());
		}
			
		// get fail values
		for (BreakpointValue bv : bkpData.getFailValues()) {
			failExecValuesList.add(bv.getChildren());
		}
			
		List<Formula> newInvs = checkWithExecValues(passExecValuesList, failExecValuesList);
		
		return new Pair<BreakpointData, List<Formula>>(bkpData, newInvs);
	}
	
	public List<Pair<BreakpointData, List<Formula>>> check(List<BreakpointData> bkpsData) {
		
		List<Pair<BreakpointData, List<Formula>>> bkpsInvs = new ArrayList<Pair<BreakpointData, List<Formula>>>();
		
		// proceed each break points
		for (int i = 0; i < bkpsData.size(); i++) {
			BreakpointData bkpData = bkpsData.get(i);
				
			bkpsInvs.add(check(bkpData));
		}
		
		return bkpsInvs;
	}
	
}
