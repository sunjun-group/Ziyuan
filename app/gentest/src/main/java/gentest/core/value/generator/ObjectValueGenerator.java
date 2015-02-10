/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.generator;

import gentest.core.data.MethodCall;
import gentest.core.data.statement.RConstructor;
import gentest.core.data.statement.Rmethod;
import gentest.core.data.statement.Statement;
import gentest.core.data.type.IType;
import gentest.core.data.typeinitilizer.TypeInitializer;
import gentest.core.data.variable.GeneratedVariable;
import gentest.core.execution.VariableRuntimeExecutor;
import gentest.main.GentestConstants;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class ObjectValueGenerator extends ValueGenerator {
	private VariableRuntimeExecutor rtExecutor = new VariableRuntimeExecutor();
	
	public ObjectValueGenerator(IType type) {
		super(type);
	}
	
	@Override
	public boolean doAppendVariable(GeneratedVariable variable, int level)
			throws SavException {
		for (int i = 0; i < GentestConstants.OBJECT_VALUE_GENERATOR_MAX_TRY_SELECTING_CONSTRUCTOR; i++) {
			TypeInitializer initializer = loadInitializer(type);
			IType newType = type;
			if (!initializer.getType().equals(type.getRawType())) {
				newType = type.resolveSubType(initializer.getType());
			} 
			Object initializedStmt = initializer.getRandomConstructor();
			if (!appendConstructor(variable, level,	newType, initializedStmt)) {
				// if fail, retry
				continue;
			}
			// validate the generated variable by executing it
			rtExecutor.reset(variable.getFirstVarId());
			rtExecutor.start(null);
			if (rtExecutor.execute(variable)) {
				break;
			} else {
				variable.reset();
			}
		}
		if (variable.isEmpty()) {
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
			IType type, Object initializedStmt) throws SavException {
		if (initializedStmt instanceof Constructor<?>) {
			Constructor<?> constructor = (Constructor<?>) initializedStmt;
			RConstructor rconstructor = RConstructor.of(constructor);
			
			Type[] types = constructor.getGenericParameterTypes();
			int[] paramIds = new int[types.length];
			for (int i = 0; i < types.length; i++) {
				GeneratedVariable newVariable = appendVariable(variable, level + 1,
						this.type.resolveType(types[i]));
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
					this.type.resolveType(methodCall.getReceiverType()));
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
					level + 2, this.type.resolveType(paramType));
			paramIds[i] = newVar.getReturnVarId();
		}
		Rmethod rmethod = new Rmethod(method, scopeId);
		variable.append(rmethod, paramIds, addVariable);
	}
	
	/**
	 * return 
	 * constructor: if the class has it own visible constructor 
	 * 			or if not, the visible constructor of extended class will be returned
	 * method: means static method, if the class does not have visible constructor but static initialization method
	 * methodCall: if the class has a builder inside. 
	 */
	public TypeInitializer loadInitializer(IType itype) {
		Class<?> type = itype.getRawType();
		TypeInitializer initializer = getTypeMethodCallsStore()
				.loadConstructors(type);
		if (initializer == null) {
			// if type is an interface or abstract, look up for constructor of
			// its subclass instead.
			if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
				// try to search subclass
				Class<?> subClass = getSubTypesScanner().getRandomImplClzz(type);
				if (subClass != null) {
					return loadInitializer(itype.resolveType(subClass));
				}
			} 
			initializer = new TypeInitializer(type);
			loadConstructors(type, initializer);
			
			getTypeMethodCallsStore().storeConstructors(type, initializer);
			
			// if still cannot get constructor,
			// try to search subclass if it's not an abstract class
			if (!type.isInterface() && !Modifier.isAbstract(type.getModifiers())
					&& initializer.hasNoConstructor()) {
				Class<?> subClass = getSubTypesScanner().getRandomImplClzz(type);
				if (subClass != null) {
					loadInitializer(itype.resolveType(subClass));
				}
			}
		}
		return initializer;
	}
	
	private void loadConstructors(Class<?> type, TypeInitializer initializer) {
		List<Constructor<?>> mightCreateLoopList = new ArrayList<Constructor<?>>();
		try {
			/*
			 * try with the perfect one which is public constructor with no
			 * parameter
			 */
			Constructor<?> constructor = type.getConstructor();
			if (canBeCandidateForConstructor(constructor, type, mightCreateLoopList)) {
				initializer.addConstructor(constructor, false);
			}
		} catch (Exception e) {
			// do nothing, just keep trying.
		}
		for (Constructor<?> constructor : type.getConstructors()) {
			if (canBeCandidateForConstructor(constructor, type, mightCreateLoopList)) {
				initializer.addConstructor(constructor, true);
			}
		}
		
		/* try to find static method for initialization inside class */
		for (Method method : type.getMethods()) {
			if (Modifier.isStatic(method.getModifiers())
					&& Modifier.isPublic(method.getModifiers())) {
				if (method.getReturnType().equals(type)) {
					initializer.addStaticMethod(method);
				}
			}
		}
		
		/* try to find a builder inside class */
		Class<?>[] declaredClasses = type.getDeclaredClasses();
		if (declaredClasses != null) {
			for (Class<?> innerClazz : declaredClasses) {
				for (Method method : innerClazz.getMethods()) {
					if (method.getReturnType().equals(type)) {
						initializer.addBuilderMethodCall(MethodCall.of(method, innerClazz));
					}
				}
			}
		}
		if (initializer.hasNoConstructor()) {
			initializer.addConstructors(mightCreateLoopList);
		}
	}

	private boolean canBeCandidateForConstructor(Constructor<?> constructor,
			Class<?> type, List<Constructor<?>> mightCreateLoopList) {
		Class<?>[] parameterTypes = constructor.getParameterTypes();
		for (Class<?> paramType : parameterTypes) {
			if (type.equals(paramType) || paramType.isAssignableFrom(type)) {
				mightCreateLoopList.add(constructor);
				return false;
			}
		}
		return Modifier.isPublic(constructor.getModifiers());
	}

}
