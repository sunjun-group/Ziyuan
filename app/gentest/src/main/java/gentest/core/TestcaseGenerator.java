/**
 * Copyright TODO
 */
package gentest.core;


import gentest.core.data.LocalVariable;
import gentest.core.data.MethodCall;
import gentest.core.data.Sequence;
import gentest.core.data.statement.RqueryMethod;
import gentest.core.data.variable.ISelectedVariable;
import gentest.core.execution.RuntimeExecutor;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import sav.common.core.SavException;

import com.google.inject.Inject;

/**
 * @author LLT
 */
public class TestcaseGenerator {
	@Inject
	private ParameterSelector parameterSelector;
	
	@Inject
	private RuntimeExecutor executor;
	private Sequence seq;
	
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
		RqueryMethod rmethod = null;
		ISelectedVariable receiverParam = null;
		for (int i = 0; i < methods.size(); i++) {
			MethodCall method = methods.get(i);
			/* prepare method receiver if method is not static */
			if (method.requireReceiver()) {
				/* prepare method receiver if method is not static */
				LocalVariable receiver = seq.getReceiver(method.getReceiverType());
				if (receiver == null) {
					/* if the instance of method receiver still did not exist in the sequence,
					 * initialize one */
					receiverParam = parameterSelector.selectReceiver(
							method.getReceiverType(), null, seq.getStmtsSize(),
							seq.getVarsSize());
					seq.appendReceiver(receiverParam, method.getReceiverType());
				}
				rmethod = new RqueryMethod(method, seq.getReceiver(
						method.getReceiverType()).getVarId());
			} else {
				rmethod = new RqueryMethod(method);
			}
			
			/* select parameters for methods and append statements 
			 * which initializes those values into the sequence */
			List<ISelectedVariable> selectParams = selectParams(method);
			int[] inVars = new int[selectParams.size()];
			for (int j = 0; j < selectParams.size(); j++) {
				ISelectedVariable param = selectParams.get(j);
				inVars[j] = param.getReturnVarId();
			}
			executor.start(receiverParam);
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

	public void setExecutor(RuntimeExecutor executor) {
		this.executor = executor;
	}
	
}
