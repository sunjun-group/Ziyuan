/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import learntest.core.commons.data.classinfo.TargetMethod;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;
import sav.strategies.dto.BreakPoint.Variable.VarScope;
import variable.FieldVar;
import variable.LocalVar;

/**
 * @author LLT
 *
 */
public class BreakpointCreator {
	private BreakpointCreator() {}
	
	public static BreakPoint createMethodEntryBkp(TargetMethod method) {
		/* collect general variables for breakpoints */
		List<Variable> generalVars = createMethodEntryVariables(method);
		/* collect breakpoints from method cfg */
		int lineNo = getMethodStartLine(method);
		BreakPoint bkp = new BreakPoint(method.getClassName(), lineNo);
		bkp.setVars(generalVars);
		return bkp;
	}

	private static int getMethodStartLine(TargetMethod method) {
		if (method.getCfg() == null) {
			return method.getLineNum();
		}
		return method.getCfg().getStartNode().getLine();
	}

	private static List<Variable> createMethodEntryVariables(TargetMethod method) {
		List<Variable> generalVars = new ArrayList<Variable>();
		/* create from method params */
		for (String paramName : method.getParams()) {
			Variable var = new Variable(paramName, paramName);
			generalVars.add(var);
		}
		/* create from class fields which are accessed in method */
		for (String fieldName : method.getAccessedFields()) {
			Variable var = new Variable(fieldName, fieldName, VarScope.THIS);
			generalVars.add(var);
		}
		return generalVars;
	}

	public static BreakPoint createMethodEntryBkp(TargetMethod method,
			Map<Integer, List<variable.Variable>> relevantVarMap) {
		/* collect general variables for breakpoints */
		List<Variable> allRelevantVars = toBkpVariables(relevantVarMap);
		/* collect breakpoints from method cfg */
		int lineNo = getMethodStartLine(method);
		BreakPoint bkp = new BreakPoint(method.getClassName(), lineNo);
		bkp.setVars(allRelevantVars);
		return bkp;
	}

	public static List<Variable> toBkpVariables(Map<Integer, List<variable.Variable>> relevantVarMap) {
		List<variable.Variable> relevantVars = new ArrayList<variable.Variable>();
		for (List<variable.Variable> relVars : relevantVarMap.values()) {
			CollectionUtils.addIfNotNullNotExist(relevantVars, relVars);
		}
		List<Variable> vars = new ArrayList<Variable>();
		for (variable.Variable relVar : relevantVars) {
			Variable var = null;
			if (relVar instanceof FieldVar) {
				var = new Variable(relVar.getName(), relVar.getName(), VarScope.THIS);
			} else if (relVar instanceof LocalVar) {
				var = new Variable(relVar.getName(), relVar.getName());
			}
			if (var != null) {
				relVar.setVarID(var.getId());
				vars.add(var);
			}
		}
		/*
		 * since equal variables in relevantVarMap might not the same object, we
		 * need this step to update back to the map
		 */
		for (List<variable.Variable> relVars : relevantVarMap.values()) {
			for (variable.Variable relVar : relVars) {
				for (variable.Variable hasIdVar : relevantVars) {
					if (hasIdVar.equals(relVar)) {
						relVar.setVarID(hasIdVar.getVarID());
						break;
					}
				}
			}
		}
		return vars;
	}
}
