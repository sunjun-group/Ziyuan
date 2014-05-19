/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.bool.utils;

import java.util.List;

import tzuyu.engine.bool.formula.Atom;
import tzuyu.engine.model.Formula;
import tzuyu.engine.runtime.RMethod;
import tzuyu.engine.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class FormulaUtils {
	private FormulaUtils() {
	}
	
	public static Formula not(Formula cond) {
		return FormulaNegation.not(cond);
	}
	
	public static Formula and(Formula curCond, Formula newCond) {
		return FormulaConjunction.and(curCond, newCond);
	}
	
	public static Formula or(Formula curCond, Formula newCond) {
		return FormulaConjunction.or(curCond, newCond);
	}

	public static Formula restrict(Formula formula, List<Atom> vars,
			List<Integer> vals) {
		return FormulaRestriction.restrict(formula, vars, vals);
	}

	public static Formula simplify(Formula formula) {
		/* LLT: we already handle the simplifier for conjunction formula
		 * when creating, so consider to remove this one.
		 */
		return formula;
	}
	
	public static String toString(Formula formula, RMethod method) {
		ConditionBuilder condBuilder = new ConditionBuilder(method);
		formula.accept(condBuilder);
		String str = condBuilder.getResult();
		if (!StringUtils.isEmpty(str)) {
			return "[" + str + "]";
		}
		return StringUtils.EMPTY;
	}
}
