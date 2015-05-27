/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.common.utils;

import java.util.List;

import sav.strategies.dto.BreakPoint.Variable;
import sav.strategies.dto.BreakPoint.Variable.VarScope;

import com.sun.jdi.Field;
import com.sun.jdi.LocalVariable;

/**
 * @author LLT
 *
 */
public class VariableUtils {
	private static boolean filterBreakpointVar = true;

	public static Variable lookupVarInBreakpoint(LocalVariable var,
			List<Variable> bkpVars) {
		for (Variable bkpVar : bkpVars) {
			if (bkpVar.getScope() == VarScope.UNDEFINED
					&& var.name().equals(bkpVar.getName())) {
				return bkpVar;
			}
		}
		
		if (!filterBreakpointVar) {
			return new Variable(var.name());
		}
		
		return null;
	}

	public static Variable lookupVarInBreakpoint(Field var, List<Variable> bkpVars) {
		for (Variable bkpVar : bkpVars) {
			if (var.name().equals(bkpVar.getName())){
				return bkpVar;
			}
		}
		
		if (!filterBreakpointVar) {
			return new Variable(var.name());
		}
		return null;
	}
}
