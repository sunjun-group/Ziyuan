/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.reporter.assertion;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import tzuyu.engine.bool.FieldVar;
import tzuyu.engine.bool.utils.ConditionBuilder;
import tzuyu.engine.model.exception.TzRuntimeException;

/**
 * @author LLT
 * 
 */
public class MethodConditionBuilder extends ConditionBuilder {
	private IMethod method;

	public MethodConditionBuilder(IMethod method) {
		super();
		this.method = method;
	}
	
	@Override
	protected String getParameterName(FieldVar fieldVar) {
		try {
			return method.getParameterNames()[fieldVar.getArgIndex() - 1];
		} catch (JavaModelException e) {
			throw new TzRuntimeException(String.format(
					"cannot find var at index %i in the method %s",
					fieldVar.getArgIndex() - 1, method.getElementName()));
		}
	}
}
