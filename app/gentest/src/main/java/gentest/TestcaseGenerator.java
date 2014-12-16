/**
 * Copyright TODO
 */
package gentest;


import gentest.data.LocalVariable;
import gentest.data.MethodCall;
import gentest.data.Sequence;
import gentest.data.statement.RqueryMethod;
import gentest.data.variable.ISelectedVariable;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import sav.common.core.SavException;

/**
 * @author LLT
 */
public class TestcaseGenerator {
	private ParameterSelector parameterSelector;
	private RuntimeExecutor executor;
	private Sequence seq;
	
	public TestcaseGenerator() {
		executor = new RuntimeExecutor();
		parameterSelector = new ParameterSelector();
	}
	
	private void refresh(Sequence seq) {
		this.seq = seq;
		executor.reset(seq);
		parameterSelector.setSequence(seq);
	}
	
	/**
	 * For each method in init list,
	 * check if method receiver already exits and initialize if necessary,
	 * then prepare inputs for the method,
	 * and append the current sequence.
	 *  
	 */
	public Sequence generateSequence(List<MethodCall> methods)
			throws SavException {
		Sequence seq = new Sequence();
		refresh(seq);
		/* append sequence for each method */
		for (int i = 0; i < methods.size(); i++) {
			MethodCall method = methods.get(i);
			/* prepare method receiver */
			LocalVariable receiver = seq.getReceiver(method.getDeclaringType());
			if (receiver == null) {
				/* if the instance of method receiver still did not exist in the sequence,
				 * initialize one */
				ISelectedVariable param = parameterSelector.selectParam(
						method.getDeclaringType(), null, seq.getStmtsSize(),
						seq.getVarsSize());
				seq.appendReceiver(param, method.getDeclaringType());
				executor.executeReceiver(param);
			}
			/* select parameters for methods and append statements 
			 * which initializes those values into the sequence */
			List<ISelectedVariable> selectParams = selectParams(method);
			int[] inVars = new int[selectParams.size()];
			for (int j = 0; j < selectParams.size(); j++) {
				ISelectedVariable param = selectParams.get(j);
				inVars[j] = param.getReturnVarId();
			}
			RqueryMethod rmethod = new RqueryMethod(method, seq.getReceiver(
					method.getDeclaringType()).getVarId());
			rmethod.setInVarIds(inVars);
			seq.appendMethodExecStmts(rmethod, selectParams);
			if (!executor.execute(rmethod, selectParams)) {
				// stop append method whenever execution fail.
				return seq;
			}
		}
		return seq;
	}

	/**
	 * auto generate value for all needed parameters of method
	 */
	private List<ISelectedVariable> selectParams(MethodCall methodCall) throws SavException {
		Method method = methodCall.getMethod();
		method.getGenericParameterTypes();
		return selectParams(method.getParameterTypes(), method.getGenericParameterTypes());
	}

	private List<ISelectedVariable> selectParams(Class<?>[] paramTypes, Type[] types)
			throws SavException {
		List<ISelectedVariable> params = new ArrayList<ISelectedVariable>();
		int firstStmtIdx = seq.getStmtsSize();
		int firstVarIdx = seq.getVarsSize();
		for (int i = 0; i < paramTypes.length; i++) {
			Class<?> paramType = paramTypes[i];
			ISelectedVariable param = parameterSelector.selectParam(paramType, types[i],
					firstStmtIdx, firstVarIdx);
			params.add(param);
			firstStmtIdx += param.getStmts().size();
			firstVarIdx += param.getNewVariables().size();
		}
		return params;
	}
	
	public RuntimeExecutor getExecutor() {
		return executor;
	}
}
