/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package libsvm.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sav.common.core.formula.Formula;
import sav.common.core.formula.LIAAtom;
import sav.common.core.formula.LIATerm;
import sav.common.core.formula.Operator;
import sav.common.core.formula.Var;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 * 
 */
public class FormulaProcessor<T extends Var> implements IDividerProcessor<Formula> {
	private Map<String, T> vars;
	
	protected FormulaProcessor() {
		
	}

	public FormulaProcessor(List<T> allVars) {
		vars = new HashMap<String, T>();
		for (T var : allVars) {
			vars.put(var.getLabel(), var);
		}
	}

	@Override
	public Formula process(Divider divider, List<String> labels, boolean round) {
		if (divider == null) {
			return null;
		}
		// a1*x1 + a2*x2 + ... + an*xn >= b
		double[] thetas = divider.getLinearExpr();
		if (round) {
			thetas = new CoefficientProcessing().process(divider.getLinearExpr());
		}
		List<LIATerm> liaTerms = new ArrayList<LIATerm>();
		for (int i = 0; i < thetas.length - 1; i++) {
			if (Double.compare(thetas[i], 0) == 0) {
				continue;
			}
			String label = labels.get(i);
			Var var = null;
			if (StringUtils.isEmpty(label) || ((var = getVar(label)) == null)) {
				continue;
			}
			liaTerms.add(LIATerm.of(var, thetas[i]));
		}
		LIAAtom atom = new LIAAtom(liaTerms, Operator.GE, thetas[thetas.length - 1]);
		return atom;
	}

	protected T getVar(String label) {
		return vars.get(label);
	}
}
