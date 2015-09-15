/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.variable;

import japa.parser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

import sav.strategies.dto.BreakPoint.Variable;

/**
 * @author LLT
 *
 */
public class VarNameAppendModeVisitor extends VarNameVisitor {
	private List<Variable> previousVars;
	private int lastLineInCurMethod;
	
	public VarNameAppendModeVisitor(VarNameCollectionMode collectionMode,
			List<Integer> lines) {
		super(collectionMode, lines);
		previousVars = new ArrayList<Variable>();
	}
	
	private void onEnterMethod(MethodDeclaration n) {
		lastLineInCurMethod = -1;
	}

	@Override
	public void visit(MethodDeclaration n, Boolean arg) {
		onEnterMethod(n);
		super.visit(n, arg);
	}

	@Override
	protected void add(int lineNumber, Variable var) {
		if (lastLineInCurMethod < 0) {
			previousVars.clear();
		}
		List<Variable> lineVars = getResult().get(lineNumber);
		if (lastLineInCurMethod != lineNumber) {
			/*
			 * add previous variables in function to variable list of the
			 * current line, if this function is call many times because more
			 * than 1 variable available at the current line, only add previous variables the first
			 * time
			 */
			lineVars.addAll(previousVars);
		}
		if (!lineVars.contains(var)) {
			lineVars.add(var);
			previousVars.add(var);
		}
		lastLineInCurMethod = lineNumber;
	}
}
