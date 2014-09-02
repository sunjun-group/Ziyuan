/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.reporter.assertion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import sav.common.core.utils.Assert;
import sav.common.core.utils.CollectionUtils;
import tzuyu.engine.bool.utils.FormulaUtils;
import tzuyu.engine.bool.utils.Simplifier;
import tzuyu.engine.model.Formula;
import tzuyu.engine.model.TzuYuAction;
import tzuyu.engine.model.dfa.DFA;
import tzuyu.engine.model.dfa.State;
import tzuyu.engine.model.dfa.Transition;
import tzuyu.engine.model.exception.TzException;
import tzuyu.engine.model.exception.TzExceptionType;
import tzuyu.engine.runtime.RMethod;
import tzuyu.plugin.commons.utils.IResourceUtils;
import tzuyu.plugin.commons.utils.MethodUtils;
import tzuyu.plugin.tester.reporter.PluginLogger;

/**
 * @author LLT
 *
 */
public class AssertionWriter {
	private Map<RMethod, List<Formula>> actionConditions;
	private IType type;
	
	public AssertionWriter(DFA dfa, Class<?> clazz, IJavaProject project) {
		initAcceptedActions(dfa);
		type = IResourceUtils.getIType(project, clazz);
		Assert.notNull(type, "Type of clazz ", clazz.getName(),
				"in the project ", project.getElementName(), "cannot be null!!");
	}

	private void initAcceptedActions(DFA dfa) {
		actionConditions = new HashMap<RMethod, List<Formula>>();
		List<State> acceptStates = new ArrayList<State>();
		for (Integer idx : dfa.getAcceptingStates()) {
			acceptStates.add(dfa.getAllStates().get(idx));
		}
		
		for (State state : acceptStates) {
			for (Transition transition : state.trans) {
				TzuYuAction action = (TzuYuAction) transition.action;
				boolean trueFormula = (action.getGuard() == Formula.TRUE);
				if (dfa.getAcceptingStates().contains(transition.target)
						&& !trueFormula	
						&& action.getAction() instanceof RMethod) {
					List<Formula> conditions = CollectionUtils
							.getListInitIfEmpty(actionConditions,
									(RMethod) action.getAction()); 
					conditions.add(action.getGuard());
				}
			}
		}
	}
	
	public void writeAssertion(IProgressMonitor monitor) throws TzException {
		ICompilationUnit cu = type.getCompilationUnit();
		if (cu != null) {
			writeAssertion(cu, monitor);
		}
	}
	
	private void writeAssertion(ICompilationUnit cu, IProgressMonitor monitor)
			throws TzException {
		try {
			ICompilationUnit workingCopy = cu.getWorkingCopy(monitor);
			// Create working copy
			for (Entry<RMethod, List<Formula>> actCond : actionConditions
					.entrySet()) {
				TextEdit edit = writeAssertionToMethod(cu, actCond);
				workingCopy.applyTextEdit(edit, monitor);
				workingCopy.reconcile(ICompilationUnit.NO_AST, false, null, monitor);
				// Commit changes
				workingCopy.commitWorkingCopy(false, monitor);
			}
		    
		    // Destroy working copy
		    workingCopy.discardWorkingCopy();
		} catch (JavaModelException e) {
			throwTzException(e);
		} catch (MalformedTreeException e) {
			throwTzException(e);
		}
	}
	
	private TextEdit writeAssertionToMethod(ICompilationUnit cu,
			Entry<RMethod, List<Formula>> actCond) throws TzException {
		try {
			IMethod method = MethodUtils.findMethod(type, actCond.getKey().getMethod());
			if (method != null) {
				ISourceRange sourceRange = method.getSourceRange();
				int firstStmtOffset = method.getSource().indexOf("{");
				
				// Get text edits
				return new InsertEdit(sourceRange.getOffset()
						+ firstStmtOffset + 1, getAssertStmts(method, actCond.getValue()));
			}
			
			Assert.fail("Cannot find associated method of "
					+ actCond.getKey().getMethod().getName() + " in the class "
					+ cu.getElementName());
		} catch (JavaModelException e) {
			throwTzException(e);
		}
		return null; //never reach.
	}


	private String getAssertStmts(IMethod method, List<Formula> value) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n\t\t").append("// Tzuyu Auto-generated assertion");
		Formula condition = value.get(0);
		for (int i = 1; i < value.size(); i++) {
			condition = FormulaUtils.or(value.get(i), condition);
		}
		condition = Simplifier.simplify(condition);
		MethodConditionBuilder visitor = new MethodConditionBuilder(method);
		condition.accept(visitor);
		sb.append("\n\t\t").append("assert ").append(visitor.getResult()).append(";");
		
		return sb.toString();
	}

	private void throwTzException(Exception e) throws TzException {
		PluginLogger.getLogger().logEx(e);
		throw new TzException(TzExceptionType.ASSERTION_WRITER);
	}
}
