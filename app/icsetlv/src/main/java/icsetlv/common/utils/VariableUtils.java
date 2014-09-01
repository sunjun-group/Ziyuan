/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.common.utils;

import sav.common.core.utils.CollectionUtils;
import icsetlv.common.dto.BreakPoint;
import icsetlv.common.dto.BreakPoint.VarScope;
import icsetlv.common.dto.BreakPoint.Variable;

import com.sun.jdi.Field;
import com.sun.jdi.LocalVariable;

import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.NameExpr;

/**
 * @author LLT
 *
 */
public class VariableUtils {
	private static boolean NOT_FILTER = false;
	private static boolean filterBreakpointVar = NOT_FILTER;
	/**
	 * currently, this method will ignore the case of method access.
	 */
	public static Variable toBreakpointVarName(FieldAccessExpr n) {
		Variable var = new Variable();
		if (n.getField().startsWith("this.")) {
			var.setName(n.getField().substring("this.".length()));
			var.setScope(VarScope.THIS);
		} else {
			var.setName(n.getField());
		}
		var.setCode(n.toString());
		return var;
	}

	public static Variable toBreakpointVarName(NameExpr n) {
		Variable var = new Variable();
		var.setName(n.getName());
		return var;
	}
	
	public static Variable toBreakpointVarName(VariableDeclaratorId n) {
		return new Variable(n.getName()); 
	}

	public static Variable lookupVarInBreakpoint(LocalVariable var,
			BreakPoint brp) {
		for (Variable bpVar : brp.getVars()) {
			if (CollectionUtils.existIn(bpVar.getScope(), VarScope.LOCAL, VarScope.UNKNOWN) 
					&& var.name().equals(bpVar.getName())) {
				return bpVar;
			}
		}
		if (!filterBreakpointVar) {
			return new Variable(var.name());
		}
		return null;
	}

	public static Variable lookupVarInBreakpoint(Field var, BreakPoint brp) {
		for (Variable bpVar : brp.getVars()) {
			if (CollectionUtils.existIn(bpVar.getScope(), VarScope.THIS,
					VarScope.UNKNOWN) && var.name().equals(bpVar.getName())) {
				return bpVar;
			}
		}
		if (!filterBreakpointVar) {
			return new Variable(var.name());
		}
		return null;
	}

}
