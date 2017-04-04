/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.cfg.bytecode;

import java.util.ArrayList;
import java.util.List;

import cfgextractor.CFG;
import learntest.breakpoint.data.DecisionBkpsData;
import learntest.main.model.MethodInfo;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;

/**
 * @author LLT
 *
 */
public class BreakpointExtractor {
	private MethodInfo methodInfo;
	private CFG cfg;
	private List<Variable> variables;
	
	public BreakpointExtractor(MethodInfo targetMethodInfo,
			CFG methodCfg, List<Variable> variables) {
		this.methodInfo = targetMethodInfo;
		this.variables = variables;
	}
	
	
	public DecisionBkpsData createBkpsFromCfg() {
		DecisionBkpsData bkpsData = new DecisionBkpsData();
		cfg.getStartNode().getLineNumber();
		/* class entry breakpoint */
		bkpsData.addBreakpoint(newBreakpoint(cfg.getStartNode().getLineNumber(), variables));
		return bkpsData;
	}
	
	private BreakPoint newBreakpoint(int lineNo, List<Variable> vars) {
		BreakPoint bkp = new BreakPoint(methodInfo.getClassName(), lineNo);
		bkp.addVars(vars);
		return bkp;
	}
	
	private List<Variable> collectMethodAccessVariables(CFG cfg) {
		List<Variable> vars = new ArrayList<Variable>();
		return vars;
	}
}
