/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.reporter.assertion;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import tzuyu.engine.model.Formula;
import tzuyu.engine.model.TzuYuAction;
import tzuyu.engine.model.dfa.DFA;
import tzuyu.engine.model.dfa.State;
import tzuyu.engine.model.dfa.Transition;
import tzuyu.engine.model.exception.TzException;
import tzuyu.engine.model.exception.TzExceptionType;
import tzuyu.engine.runtime.RMethod;
import tzuyu.engine.utils.Assert;
import tzuyu.engine.utils.CollectionUtils;
import tzuyu.plugin.core.exception.PluginException;
import tzuyu.plugin.core.utils.IResourceUtils;
import tzuyu.plugin.core.utils.MethodUtils;
import tzuyu.plugin.core.utils.SignatureParser;
import tzuyu.plugin.reporter.PluginLogger;

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
		Assert.assertNotNull(type, "Type of clazz ", clazz.getName(),
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
	
	public void writeAssertion() throws TzException {
		writeAssertion(type.getCompilationUnit());
	}
	
	private void writeAssertion(ICompilationUnit cu) throws TzException {
		try {

			Document document = new Document(cu.getSource());
			// Create working copy
			ICompilationUnit workingCopy;
			workingCopy = cu.getWorkingCopy(null);
			for (Entry<RMethod, List<Formula>> actCond : actionConditions
					.entrySet()) {
				TextEdit edit = writeAssertionToMethod(cu, actCond);
				edit.apply(document);
				// update of the compilation unit
				cu.getBuffer().setContents(document.get());
			}

			cu.save(null, true);

			// Destroy working copy
			workingCopy.discardWorkingCopy();
		} catch (JavaModelException e) {
			throwTzException(e);
		} catch (MalformedTreeException e) {
			throwTzException(e);
		} catch (BadLocationException e) {
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
			
			Assert.assertFail("Cannot find associated method of "
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
		for (Formula cond : value) {
			ConditionBoolVisitor visitor = new ConditionBoolVisitor(method);
			cond.accept(visitor);
			sb.append("\n\t\t").append("assert ").append(visitor.getResult()).append(";");
		}
		return sb.toString();
	}

	private void throwTzException(Exception e) throws TzException {
		PluginLogger.getLogger().logEx(e);
		throw new TzException(TzExceptionType.ASSERTION_WRITER);
	}
}
