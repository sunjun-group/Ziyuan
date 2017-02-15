/**
 * Copyright TODO
 */
package gentest.core;


import gentest.core.data.IDataProvider;
import gentest.core.data.LocalVariable;
import gentest.core.data.MethodCall;
import gentest.core.data.Sequence;
import gentest.core.data.statement.RqueryMethod;
import gentest.core.data.type.IType;
import gentest.core.data.type.ITypeCreator;
import gentest.core.data.variable.ISelectedVariable;
import gentest.core.execution.RuntimeExecutor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	@Inject
	private ITypeCreator typeCreator;
	
	@Inject
	private IDataProvider<Sequence> sequenceProvider;
	
	/**
	 * For each method in init list,
	 * check if method receiver already exits and initialize if necessary,
	 * then prepare inputs for the method,
	 * and append the current sequence.
	 */
	public Sequence generateSequence(List<MethodCall> methods)
			throws SavException {
		Sequence seq = new Sequence();
		sequenceProvider.setData(seq);
		/* append sequence for each method */
		RqueryMethod rmethod = null;
		Map<Class<?>, IType> receiverTypes = new HashMap<Class<?>, IType>();
		executor.reset();
		for (int i = 0; i < methods.size(); i++) {
			ISelectedVariable receiverParam = null;
			MethodCall method = methods.get(i);
			IType receiverType = null;
			/* prepare method receiver if method is not static */
			if (method.requireReceiver()) {
				/* select itype for receiver */
				receiverType = receiverTypes.get(method.getReceiverType());
				if (receiverType == null) {
					receiverType = typeCreator.forClass(method.getReceiverType());
					receiverTypes.put(method.getReceiverType(), receiverType);
				}
				/* prepare method receiver if method is not static */
				LocalVariable receiver = seq.getReceiver(method.getReceiverType());
				if (receiver == null) {
					/* if the instance of method receiver still did not exist in the sequence,
					 * initialize one */
					receiverParam = parameterSelector
							.selectReceiver(receiverType, seq.getStmtsSize(),
									seq.getVarsSize());
					seq.appendReceiver(receiverParam, method.getReceiverType());
//					System.currentTimeMillis();
				} 
				rmethod = new RqueryMethod(method, seq.getReceiver(
						method.getReceiverType()).getVarId());
			} else {
				rmethod = new RqueryMethod(method);
			}
			
			/* select parameters for methods and append statements 
			 * which initializes those values into the sequence */
			List<ISelectedVariable> selectParams = selectParams(method, receiverType);
			int[] inVars = new int[selectParams.size()];
			for (int j = 0; j < selectParams.size(); j++) {
				ISelectedVariable param = selectParams.get(j);
				inVars[j] = param.getReturnVarId();
			}
			if (receiverParam != null) {
				executor.execute(receiverParam);
			}
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
	private List<ISelectedVariable> selectParams(MethodCall methodCall,
			IType receiverType) throws SavException {
		Method method = methodCall.getMethod();
		IType[] paramTypes = null;
		if (receiverType == null) {
			paramTypes = typeCreator.forType(method.getGenericParameterTypes());
		} else {
			paramTypes = receiverType.resolveType(method.getGenericParameterTypes());
		}
		
		return selectParams(paramTypes);
	}

	private List<ISelectedVariable> selectParams(IType[] paramTypes)
			throws SavException {
		List<ISelectedVariable> params = new ArrayList<ISelectedVariable>();
		int firstStmtIdx = getSequence().getStmtsSize();
		int firstVarIdx = getSequence().getVarsSize();
		for (int i = 0; i < paramTypes.length; i++) {
			IType paramType = paramTypes[i];
			ISelectedVariable param = parameterSelector.selectParam(paramType,
					firstStmtIdx, firstVarIdx);
			params.add(param);
			firstStmtIdx += param.getStmts().size();
			firstVarIdx += param.getNewVariables().size();
		}
		return params;
	}
	
	private Sequence getSequence() {
		return sequenceProvider.getData();
	}
	
	public RuntimeExecutor getExecutor() {
		return executor;
	}

	public void setExecutor(RuntimeExecutor executor) {
		this.executor = executor;
	}
	
}
