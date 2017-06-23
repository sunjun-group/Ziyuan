/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv;

import icsetlv.common.dto.BreakpointData;
import icsetlv.common.dto.BreakpointValue;

import java.util.List;

import sav.common.core.Pair;
import sav.common.core.formula.Eq;
import sav.common.core.formula.Formula;
import sav.common.core.formula.utils.FormulaUtils;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT
 *
 */
public class BooleanDivider {
	
	public Formula divide(List<ExecVar> boolVars, BreakpointData bkpData) {
		Formula formula = null;
		for (ExecVar var : boolVars) {
			formula = FormulaUtils.or(formula,
								divide(var, bkpData.getPassValues(),
										bkpData.getFailValues()));
		}
		return formula;
	}
	
	private Formula divide(ExecVar var,
			List<BreakpointValue> passValues, List<BreakpointValue> failValues) {
		Pair<Boolean, Boolean> allTrueFalseInPass = checkAllTrueOrAllFalse(passValues, var.getVarId());
		Pair<Boolean, Boolean> allTrueFalseInFail = checkAllTrueOrAllFalse(failValues, var.getVarId());
		if(allTrueFalseInPass.a && allTrueFalseInFail.b){
			return new Eq<Boolean>(var, true);
		}
		if(allTrueFalseInPass.b && allTrueFalseInFail.a){
			return new Eq<Boolean>(var, false);
		}
		return null;
	}

	private Pair<Boolean,Boolean> checkAllTrueOrAllFalse(final List<BreakpointValue> values,
			String varLabel) {
		boolean allTrue = true;
		boolean allFalse = true;
		boolean found = false;
		for(BreakpointValue breakPoint: values){
			Double varVal = breakPoint.getValue(varLabel, null);
			if (varVal == null) {
				continue;
			}
			found = true;
			boolean val = varVal > 0;
			allTrue &= val;
			allFalse &= !val;
			
			if(!allTrue && !allFalse){
				break;
			}
		}
		if (!found) {
			return Pair.of(false, false);
		}
		return new Pair<Boolean, Boolean>(allTrue, allFalse);
	}
	
}
