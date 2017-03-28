/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.builder;

import gentest.core.FixTraceTester;
import gentest.core.GentestListener;
import gentest.core.TestcaseGenerator;
import gentest.core.data.MethodCall;
import gentest.core.data.Sequence;
import gentest.core.data.statement.REvaluationMethod;
import gentest.core.data.statement.RqueryMethod;
import gentest.core.data.statement.Statement.RStatementKind;
import gentest.core.data.variable.ISelectedVariable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.utils.Assert;

/**
 * @author LLT
 * usage:
 * start builder with number of testcases,
 * .forClass to start generating testcases for a class
 * 		forClass can be called many times
 * .forMethod to add method of the class including in the test.
 * 		forMethod can be called many times,
 * 		the input method name/signature must exist in the predefined class. 
 * .generate to generate sequences of testcases
 * .evaluationMethod to start defining method in which the returned value of tested method
 * 			will be evaluated.
 * 		.param to define parameters of the evaluation method. 
 * 
 * After generating, sequences can be printed to file using TestsPrinter 
 * 
 */
public class FixTraceGentestBuilder extends GentestBuilder<FixTraceGentestBuilder> {
	public static final String RETURN_METHOD_PARAM_NAME = "return";
	private Map<Integer, String> aliasMethodMap;
	private List<EvaluationMethod> evalMethods;
	
	public FixTraceGentestBuilder(int numberOfTcs) {
		super(numberOfTcs);
		this.aliasMethodMap = new HashMap<Integer, String>();
		evalMethods = new ArrayList<EvaluationMethod>();
	}
	
	public FixTraceGentestBuilder method(String methodNameOrSign, String alias) {
		MethodCall methodCall = findAndAddTestingMethod(methodNameOrSign);
		methodCall.setAlias(alias);
		aliasMethodMap.put(methodCalls.size() - 1, alias);
		return this;
	}
	
	public EvaluationMethod evaluationMethod(Class<?> clazz,
			String evalMethodNameOrSign) {
		return new EvaluationMethod(clazz, evalMethodNameOrSign);
	}
	
	public EvaluationMethod evaluationMethod(Class<?> clazz,
			String evalMethodNameOrSign, String targetMethodAlias) {
		return new EvaluationMethod(clazz, evalMethodNameOrSign, targetMethodAlias);
	}

	public class EvaluationMethod {
		private Method method;
		private String targetAlias; 
		private List<Pair<String, String>> params;
		
		private EvaluationMethod(Class<?> clazz, String evalMethodNameOrSign) {
			method = findTestingMethod(clazz, evalMethodNameOrSign);
			if (method.getReturnType() != boolean.class) {
				throw new IllegalArgumentException(String.format(
						"Evaluation method (%s) must return boolean",
						evalMethodNameOrSign));
			}
			params = new ArrayList<Pair<String, String>>();
		}
		
		public EvaluationMethod(Class<?> clazz, String evalMethodNameOrSign,
				String targetMethodAlias) {
			this(clazz, evalMethodNameOrSign);
			this.targetAlias = targetMethodAlias;
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
		
		public FixTraceGentestBuilder paramAutofill() {
			Assert.assertTrue(targetAlias != null,
					"targetAlias must be specified to call paramAutofill");
			for (MethodCall methodCall : methodCalls) {
				if (targetAlias.equals(methodCall.getAlias())) {
					for (String param : methodCall.getParamNames()) {
						this.params.add(Pair.of(targetAlias, param));
					}
					this.params.add(Pair.of(targetAlias, RETURN_METHOD_PARAM_NAME));
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
	public Pair<List<Sequence>, List<Sequence>> doGenerate() throws SavException {
		FixTraceTester tester = new FixTraceTester(numberOfTcs, getPrjClassLoader());
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
		return tester.test(methodCalls);
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
	
}
