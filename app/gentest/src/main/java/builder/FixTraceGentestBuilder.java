/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package builder;

import gentest.FixTraceTester;
import gentest.GentestListener;
import gentest.TestcaseGenerator;
import gentest.data.MethodCall;
import gentest.data.Sequence;
import gentest.data.statement.REvaluationMethod;
import gentest.data.statement.RqueryMethod;
import gentest.data.statement.Statement.RStatementKind;
import gentest.data.variable.ISelectedVariable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.utils.SignatureUtils;

/**
 * @author LLT
 *
 */
public class FixTraceGentestBuilder extends GentestBuilder {
	public static final String RETURN_METHOD_PARAM_NAME = "return";
	private int numberOfTcs;
	private Class<?> clazz;
	private List<Method> testingMethods;
	private Map<Integer, String> aliasMethodMap;
	private List<EvaluationMethod> evalMethods;
	
	public FixTraceGentestBuilder(int numberOfTcs) {
		this.aliasMethodMap = new HashMap<Integer, String>();
		testingMethods = new ArrayList<Method>();
		evalMethods = new ArrayList<EvaluationMethod>();
		this.numberOfTcs = numberOfTcs;
	}
	
	public FixTraceGentestBuilder forClass(Class<?> clazz) {
		this.clazz = clazz;
		return this;
	}
	
	public FixTraceGentestBuilder method(String methodNameOrSign, String alias) {
		findAndAddTestingMethod(methodNameOrSign);
		aliasMethodMap.put(testingMethods.size() - 1, alias);
		return this;
	}
	
	public FixTraceGentestBuilder method(String methodNameOrSign) {
		findAndAddTestingMethod(methodNameOrSign);
		return this;
	}

	private Method findAndAddTestingMethod(String methodNameOrSign) {
		Method testingMethod = findTestingMethod(clazz, methodNameOrSign);
		testingMethods.add(testingMethod);
		return testingMethod;
	}
	
	private static Method findTestingMethod(Class<?> clazz, String methodNameOrSign) {
		if (clazz != null) {
			/* try to find if input is method name */
			for (Method method : clazz.getMethods()) {
				if (method.getName().equals(methodNameOrSign)) {
					return method;
				}
			}
			/* try to find if input is method signature */
			for (Method method : clazz.getMethods()) {
				if (SignatureUtils.getSignature(method).equals(methodNameOrSign)) {
					return method;
				}
			}
			/* cannot find class */
			throw new IllegalArgumentException(String.format("cannot find method %s in class %s", methodNameOrSign
					, clazz.getName()));
		}
		/* class not yet declared */
		throw new IllegalArgumentException(
				String.format(
						"The class for method %s is not set. Expect forClass() is called before method(String methodNameOrSign)",
						methodNameOrSign));
	}
	
	public EvaluationMethod evaluationMethod(Class<?> clazz,
			String methodNameOrSign) {
		return new EvaluationMethod(clazz, methodNameOrSign);
	}

	public class EvaluationMethod {
		private Method method;
		private List<Pair<String, String>> params;
		
		private EvaluationMethod(Class<?> clazz, String methodNameOrSign) {
			method = findTestingMethod(clazz, methodNameOrSign);
			if (method.getReturnType() != boolean.class) {
				throw new IllegalArgumentException(String.format(
						"Evaluation method (%s) must return boolean",
						methodNameOrSign));
			}
			params = new ArrayList<Pair<String, String>>();
		}
		
		/**
		 * a parameter for evaluation method must have format: 
		 * [methodAlias].[parameterName]
		 * for example: the evaluationMethod gets 3 parameter:
		 * argument x of method foo() alias f, argument x of method bar() alias b,
		 * and result of method bar(),
		 * The parameters for that evaluation method will be:
		 * "f.x", "b.x", "b.return" 
		 */
		public FixTraceGentestBuilder param(String... params) {
			if (params != null) {
				for (String param : params) {
					this.params.add(toPairParam(param));
				}
			}
			evalMethods.add(this);
			return FixTraceGentestBuilder.this;
		}

		private Pair<String, String> toPairParam(String param) {
			String[] splitStrs = StringUtils.split(param, ".");
			/* check if alias is already defined */
			if (splitStrs.length != 2) {
				throw new IllegalArgumentException("Expect parameter for evaluation" +
						"method with format [methodAlias].[parameterName], get: " + param);
			}
			if (!aliasMethodMap.values().contains(splitStrs[0])) {
				throw new IllegalArgumentException(
						String.format("Method with alias %s not defined!", splitStrs[1]));
			}
			return Pair.of(splitStrs[0], splitStrs[1]);
		}
	}
	
	/**
	 * Pair<passTcs, failTcs>
	 */
	@Override
	public Pair<List<Sequence>, List<Sequence>> generate() throws SavException {
		FixTraceTester tester = new FixTraceTester(numberOfTcs);
		tester.setListener(new GentestListener() {
			
			@Override
			public void onFinishGenerateSeq(TestcaseGenerator tcGenerator,
					Sequence seq) {
				if (!tcGenerator.getExecutor().isSuccessful()) {
					return;
				}
				/* append evaluation methods: only for pass sequences */
				List<RqueryMethod> rqueryMethods = seq.<RqueryMethod>getStatementByType(
						RStatementKind.QUERY_METHOD_INVOKE);
				for (EvaluationMethod evalMethod : evalMethods) {
					REvaluationMethod rmethod = new REvaluationMethod(evalMethod.method);
					int[]inVarIds = new int[evalMethod.params.size()];
					/* collect parameter for the evalMethod */
					for (int i = 0; i < evalMethod.params.size(); i++) {
						Pair<String, String> param = evalMethod.params.get(i);
						RqueryMethod methodOfAlias = getMethodByAlias(rqueryMethods, param.a);
						if (RETURN_METHOD_PARAM_NAME.equals(param.b)) {
							inVarIds[i] = methodOfAlias.getOutVarId();
						} else {
							inVarIds[i] = methodOfAlias.getVarIdsByParamName(param.b);
						}
					}
					rmethod.setInVarIds(inVarIds);
					rmethod.setOutVarId(seq.getVarsSize());
					seq.append(rmethod);
					/* execute the sequence with new method call */
					tcGenerator.getExecutor().execute(rmethod,
							new ArrayList<ISelectedVariable>());
					if (!tcGenerator.getExecutor().isSuccessful()) {
						break;
					}
				}
			}
		});
		return tester.test(initMethodCalls());
	}

	private RqueryMethod getMethodByAlias(List<RqueryMethod> rqueryMethods,
			String alias) {
		for (RqueryMethod method : rqueryMethods) {
			if (alias.equals(method.getQueryMethod().getAlias())) {
				return method;
			}
		}
		throw new RuntimeException(String.format(
				"Cannot find method with alias: %s in the rqueryMethod", alias));
	}

	private List<MethodCall> initMethodCalls() {
		List<MethodCall> methodCalls = new ArrayList<MethodCall>(
				testingMethods.size());
		for (int i = 0; i < testingMethods.size(); i++) {
			Method method = testingMethods.get(i);
			MethodCall methodCall = MethodCall.of(method);
			methodCall.setAlias(aliasMethodMap.get(i));
			methodCalls.add(methodCall);
		}
		return methodCalls;
	}
}
