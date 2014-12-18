/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.value.generator;

import gentest.commons.utils.MethodUtils;
import gentest.data.variable.GeneratedVariable;

import java.lang.reflect.Method;
import java.util.List;

import sav.common.core.SavException;


/**
 * @author LLT
 *
 */
public abstract class SpecificValueGenerator extends AbstractValueGenerator {
	private Class<?> implType;
	private List<Method> methodcalls;
	
	public SpecificValueGenerator(List<String> methodSigns, Class<?> implType) {
		this.implType = implType;
		methodcalls = MethodUtils.findMethods(implType, methodSigns);
	}
	
	public SpecificValueGenerator(Class<?> implType, List<Method> methods) {
		this.implType = implType;
		this.methodcalls = methods;
	}
	
	@Override
	public void doAppend(GeneratedVariable variable, int level, Class<?> type)
			throws SavException {
		getGenerator(ObjectValueGenerator.class).doAppend(variable, level, implType);
		int varId = variable.getLastVarId();
		variable.commitReturnVarIdIfNotExist();
		doAppendMethods(variable, level, varId, methodcalls);
	}

}
