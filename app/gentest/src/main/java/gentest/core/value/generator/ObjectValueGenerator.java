/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.generator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gentest.core.data.MethodCall;
import gentest.core.data.statement.RConstructor;
import gentest.core.data.statement.Rmethod;
import gentest.core.data.statement.Statement;
import gentest.core.data.type.IType;
import gentest.core.data.typeinitilizer.TypeInitializer;
import gentest.core.data.variable.GeneratedVariable;
import gentest.core.execution.VariableRuntimeExecutor;
import gentest.main.GentestConstants;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class ObjectValueGenerator extends ValueGenerator {
	private static Logger log = LoggerFactory.getLogger(ObjectValueGenerator.class);
	private VariableRuntimeExecutor rtExecutor = new VariableRuntimeExecutor();
	private IType selectedType;
	
	public ObjectValueGenerator(IType type) {
		super(type);
	}
	
	@Override
	public boolean doAppendVariable(GeneratedVariable variable, int level)
			throws SavException {
		long start = System.currentTimeMillis();
		long limit = 30 * 1000;
		int i = 0;
		for (; i < GentestConstants.OBJECT_VALUE_GENERATOR_MAX_TRY_SELECTING_CONSTRUCTOR; i++) {
			long now = System.currentTimeMillis();
			if (now > start +limit) {
				variable.reset();
				break;
			}
			TypeInitializer initializer = loadInitializer(type, 0);
			if (initializer == null || initializer.hasNoConstructor()) {
				variable.reset();
				continue;
			}
			selectedType = type;
			if (!initializer.getType().equals(type.getRawType())) {
				selectedType = type.resolveType(initializer.getType());
			} 
			Object initializedStmt = initializer.getRandomConstructor();
			if (!appendConstructor(variable, level,	initializedStmt)) {
				variable.reset();
				// if fail, retry
				continue;
			}
			// validate the generated variable by executing it
			rtExecutor.reset(variable.getFirstVarId());
			// rtExecutor.start(null);
			if (rtExecutor.execute(variable)) {
				break;
			} else {
				variable.reset();
			}
		}
		log.debug(String.format("Select constructor after %s trial [%s]", i + 1, variable.isEmpty() ? "unsuccessful" : "successful"));
		if (variable.isEmpty()) {
			selectedType = null;
			/* we have to assign null to the obj */
			assignNull(variable, type.getRawType());
			return false;
		}
		
		return true;
	}
	
	protected VariableRuntimeExecutor getExecutor() {
		return rtExecutor;
	}

	private boolean appendConstructor(GeneratedVariable variable, int level,
			Object initializedStmt) throws SavException {
		if (initializedStmt instanceof Constructor<?>) {
			Constructor<?> constructor = (Constructor<?>) initializedStmt;
			RConstructor rconstructor = RConstructor.of(constructor);
			
			Type[] types = constructor.getGenericParameterTypes();
			int[] paramIds = new int[types.length];
			for (int i = 0; i < types.length; i++) {
				IType paramResolvedType = selectedType.resolveType(types[i]);
				/* in case resolved type of param is the super type of receiver, 
				 * we try to choose a subtype for it. In the worse case that subtype is the same with receiver type,
				 * we better ignore it to avoid a loop call */
				int nextLevel = level + 1;
				if (paramResolvedType.getRawType().isAssignableFrom(constructor.getDeclaringClass())) {
					Class<?> paramSubType = getSubTypesScanner().getRandomImplClzz(paramResolvedType);
					if (paramSubType == null || 
							paramSubType.isAssignableFrom(constructor.getDeclaringClass())) {
						nextLevel += 2;
					}
					paramResolvedType = selectedType.resolveType(paramSubType);
				}
				GeneratedVariable newVariable = appendVariable(variable, nextLevel,
						paramResolvedType);
				paramIds[i] = newVariable.getReturnVarId();
			}
			variable.append(rconstructor, paramIds);
		} else if (initializedStmt instanceof Method) {
			// init by static method
			doAppendStaticMethods(variable, level,
					CollectionUtils.listOf((Method) initializedStmt));
		} else if (initializedStmt instanceof MethodCall) {
			// init by a builder
			MethodCall methodCall = (MethodCall) initializedStmt;
			GeneratedVariable newVar = appendVariable(variable, level + 1,
					selectedType.resolveType(methodCall.getReceiverType()));
			// call the method of builder to get the object for current type.
			doAppendMethods(variable, level,
					CollectionUtils.listOf(methodCall.getMethod()),
					newVar.getReturnVarId(), true);
		} else {
			return false;
		}
		return true;
	}
	

	protected final void doAppendStaticMethods(GeneratedVariable variable, int level,
			List<Method> methodcalls) throws SavException {
		doAppendMethods(variable, level, methodcalls, Statement.INVALID_VAR_ID,
				true);
	}
	
	protected void doAppendMethods(GeneratedVariable variable, int level, 
			List<Method> methodcalls, int scopeId, boolean addVariable) throws SavException {
		// generate value for method call
		for (Method method : methodcalls) {
			doAppendMethod(variable, level, scopeId, addVariable, method);
		}
	}

	protected void doAppendMethod(GeneratedVariable variable, int level,
			int scopeId, boolean addVariable, Method method)
			throws SavException {
		/* check generic types */
		Type[] genericParamTypes = method.getGenericParameterTypes();
		int[] paramIds = new int[genericParamTypes.length];
		for (int i = 0; i < paramIds.length; i++) {
			Type paramType = genericParamTypes[i];
			GeneratedVariable newVar = appendVariable(variable,
					level + 2, selectedType.resolveType(paramType));
			paramIds[i] = newVar.getReturnVarId();
		}
		Rmethod rmethod = new Rmethod(method, scopeId);
		variable.append(rmethod, paramIds, addVariable);
	}
	
	private static final int RESOLVE_LEVEL = 7; // to prevent stack overflow.
	public TypeInitializer loadInitializer(IType itype, int level) {
		if (level == RESOLVE_LEVEL) {
			return null;
		}
		Class<?> type = itype.getRawType();
		TypeInitializer initializer = getTypeInitializerStore().load(type);
		if (initializer == null || initializer.doesNotHaveGoodConstructor()) {
			// if type is an interface or abstract, look up for constructor of
			// its subclass instead.
			if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
				// try to search subclass
				Class<?> subClass = getSubTypesScanner().getRandomImplClzz(itype);
				if (subClass != null) {
					return loadInitializer(itype.resolveType(subClass), level + 1);
				}
			} 
			// if still cannot get constructor,
			// try to search subclass if it's not an abstract class
			if (!type.isInterface() && !Modifier.isAbstract(type.getModifiers())) {
				Class<?> subClass = getSubTypesScanner().getRandomImplClzz(itype);
				if (type.equals(subClass)) {
					/* 
					 * if the class do not have any subClass and has only bad constructor, we will accept it as
					 * last choice constructor.
					 * */
					if (initializer != null && !initializer.hasNoConstructor()) {
						return initializer;
					} 
					return null;
				}
				if (subClass != null) {
					IType subType = itype.resolveType(subClass);
					loadInitializer(subType, level + 1);
				}
			}
		}
		return initializer;
	}
	
}
