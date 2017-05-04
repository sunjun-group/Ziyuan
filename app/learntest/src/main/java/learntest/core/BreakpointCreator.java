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

import learntest.core.commons.data.testtarget.TargetMethod;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;
import sav.strategies.dto.BreakPoint.Variable.VarScope;

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
		int lineNo = method.getCfg().getStartNode().getLine();
		BreakPoint bkp = new BreakPoint(method.getTargetClazz().getClassName(), lineNo);
		bkp.setVars(generalVars);
		return bkp;
	}

	private static List<Variable> createMethodEntryVariables(TargetMethod method) {
		List<Variable> generalVars = new ArrayList<Variable>();
		/* create from method params */
		for (String paramName : method.getParams()) {
			Variable var = new Variable("", paramName);
			generalVars.add(var);
		}
		/* create from class fields which are accessed in method */
		for (String fieldName : method.getAccessedFields()) {
			Variable var = new Variable("", fieldName, VarScope.THIS);
			generalVars.add(var);
		}
		return generalVars;
	}
}
