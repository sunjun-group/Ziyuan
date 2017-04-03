/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.cfg.bytecode;

import java.util.List;
import java.util.Set;

import cfgextractor.CFG;
import learntest.breakpoint.data.DecisionBkpsData;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;

/**
 * @author LLT
 *
 */
public class BreakpointCreator {
	
	private String className;
	private String methodName;
	private List<Variable> variables;
	
	public BreakpointCreator(String className, String methodName, List<Variable> variables, Set<Integer> returns) {
		this.className = className;
		this.methodName = methodName;
		this.variables = variables;
	}
	
	
	public DecisionBkpsData createBkpsFromCfg(CFG cfg) {
		cfg.getStartNode().getLineNumber();
		BreakPoint entry = new BreakPoint(className, lineNo, newVars)
	}
	
	private List<Variable> collectMethodAccessVariables(CFG cfg) {
		
	}
}
