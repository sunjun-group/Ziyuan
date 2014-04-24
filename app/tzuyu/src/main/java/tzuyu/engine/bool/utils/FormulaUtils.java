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
import tzuyu.engine.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class FormulaUtils {
	private FormulaUtils() {
	}
	
	public static Formula notOf(Formula cond) {
		return FormulaNegation.notOf(cond);
	}
	
	public static Formula andOf(Formula curCond, Formula newCond) {
		return FormulaConjunction.andOf(curCond, newCond);
	}
	
	public static Formula orOf(Formula curCond, Formula newCond) {
		return FormulaConjunction.orOf(curCond, newCond);
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
	
	public static String toString(Formula formula) {
		ConditionBuilder condBuilder = new ConditionBuilder();
		formula.accept(condBuilder);
		String str = condBuilder.getResult();
		if (!StringUtils.isEmpty(str)) {
			return "[" + str + "]";
		}
		return StringUtils.EMPTY;
	}
}
