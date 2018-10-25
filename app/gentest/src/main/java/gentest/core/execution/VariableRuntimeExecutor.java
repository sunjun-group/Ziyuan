/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.execution;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import gentest.core.data.statement.RArrayAssignment;
import gentest.core.data.statement.RArrayConstructor;
import gentest.core.data.statement.RAssignment;
import gentest.core.data.statement.RConstructor;
import gentest.core.data.statement.REvaluationMethod;
import gentest.core.data.statement.Rmethod;
import gentest.core.data.statement.Statement;
import gentest.core.data.statement.StatementVisitor;
import gentest.core.data.variable.ISelectedVariable;
import sav.common.core.utils.Assert;
import sav.common.core.utils.CachePoolExecutionTimer;
import sav.common.core.utils.ExecutionTimer;
import sav.common.core.utils.SingleTimer;
import sav.common.core.utils.TextFormatUtils;

/**
 * @author LLT
 *
 */
public class VariableRuntimeExecutor implements StatementVisitor {
	private static Logger log = LoggerFactory.getLogger(VariableRuntimeExecutor.class);
	@Inject @Named("methodExecTimeout")
	static long methodExecTimeout;
	protected RuntimeData data;
	protected Boolean successful;
	public static CachePoolExecutionTimer executionTimer = (CachePoolExecutionTimer) ExecutionTimer
			.getCachePoolExecutionTimer(methodExecTimeout);
//	public static ExecutionTimer executionTimer = ExecutionTimer.getCountDownExecutionTimer(methodExecTimeout);
//	public static ExecutionTimer executionTimer = ExecutionTimer.getCachePoolExecutionTimer(methodExecTimeout);

	
	public VariableRuntimeExecutor() {
		data = new RuntimeData();
	}
	
	public VariableRuntimeExecutor(int firstVarId) {
		data = new RuntimeData(firstVarId);
	}
	
	public void reset() {
		reset(0);
	} 
	
	public void reset(int firstVarId) {
		data.reset();
		data.setFirstVarId(firstVarId);
		successful = true;
	} 
	
	public boolean start(ISelectedVariable receiver) {
		reset();
		if (receiver != null) {
			execute(receiver);
		}
		return successful;
	}

	public boolean execute(ISelectedVariable param) {
		return execute(param.getStmts());
	}
	
	public boolean execute(List<Statement> stmts) {
		for (Statement stmt : stmts) {
			execute(stmt);
		}
		return successful;
	}

	public boolean execute(Statement stmt) {
		try {
			log.debug("Execute stmt " + stmt.getKind());
			stmt.accept(this);
		} catch(Throwable ex) {
			log.debug(ex.getMessage());
			onFail();
		}
		return successful;
	}

	private void onFail() {
		successful = false;
	}
	
	protected void addExecData(int varId, Object value) {
		data.addExecData(varId, value);
	}
	
	protected Object getExecData(int var) {
		return data.getExecData(var);
	}
	
	/* visitor part */
	@Override
	public boolean visit(RAssignment stmt) {
		if (stmt.getValue() == null) {
			log.debug(String.format("var_%s=null", stmt.getOutVarId()));
		} else {
			log.debug(String.format("var_%s=%s", stmt.getOutVarId(), stmt.getValue()));
		}
		addExecData(stmt.getOutVarId(), stmt.getValue());
		return successful;
	}

	@Override
	public boolean visit(RConstructor stmt) {
		log.debug("Execute Constructor...");
		SingleTimer timer = SingleTimer.start(String.format("Execute Constructor [%s (%s params)]",
				stmt.getDeclaringClass().getName(), TextFormatUtils.printObj(stmt.getInputTypes())));
		List<Object> inputs = new ArrayList<Object>(stmt.getInVarIds().length);
		for (int var : stmt.getInVarIds()) {
			inputs.add(getExecData(var));
		}
		try {
			boolean success = newInstance(stmt, inputs);
			if (!success) {
				onFail();
			}
		} catch (Throwable e) {
			onFail();
		}
		log.debug(timer.getResult());
		return successful;
	}
	
	private boolean newInstance(RConstructor stmt, List<Object> inputs) throws Exception {
		Object newInstance = stmt.getConstructor().newInstance((Object[]) inputs.toArray());
		// update data
		for (int i = 0; i < stmt.getInVarIds().length; i++) {
			addExecData(stmt.getInVarIds()[i], inputs.get(i));
		}
		addExecData(stmt.getOutVarId(), newInstance);
		return successful;
	}

//	private boolean newInstance(final RConstructor stmt, final List<Object> inputs) {
//		boolean success = executionTimer.run(new Runnable() {
//			
//			@Override
//			public void run() {
//				Object newInstance;
//				try {
//					newInstance = stmt.getConstructor().newInstance(
//							(Object[]) inputs.toArray());
//					// update data
//					for (int i = 0; i < stmt.getInVarIds().length; i++) {
//						addExecData(stmt.getInVarIds()[i], inputs.get(i));
//					}
//					addExecData(stmt.getOutVarId(), newInstance);
//				} catch (Throwable e) {
//					onFail();
//				}
//			}
//		}, 2000l);
//		return success;
//	}

	class ReturnValue{
		boolean valid = true;
		Object returnedValue = null;
	}
	
	@Override
	public boolean visitRmethod(Rmethod stmt) {
		SingleTimer timer = SingleTimer.start(String.format("Execute Method [%s (%s params)]",
				stmt.getMethod().getName(), TextFormatUtils.printObj(stmt.getMethod().getParameterTypes())));
	
		final List<Object> inputs = new ArrayList<Object>(stmt.getInVarIds().length);
		for (int var : stmt.getInVarIds()) {
			inputs.add(getExecData(var));
		}
		
		final ReturnValue value = new ReturnValue();
		try {
			final Object obj = getExecData(stmt.getReceiverVarId());
			final Method method = stmt.getMethod();
			invokeMethodByExecutionTimer(inputs, value, obj, method);			
			if (isSuccessful()) {
				// update data
				for (int i = 0; i < stmt.getInVarIds().length; i++) {
					addExecData(stmt.getInVarIds()[i], inputs.get(i));
				}
				if (stmt.getOutVarId() != Statement.INVALID_VAR_ID) {
					addExecData(stmt.getOutVarId(), value.returnedValue);
				}
			}
		} catch (Throwable e) {
			log.debug(e.getMessage());
			onFail();
		}
		log.debug(timer.getResult());
		return successful;
	}
	
	private void invokeMethodByExecutionTimer(final List<Object> inputs, final ReturnValue value, final Object obj,
			final Method method) throws Exception {
		boolean notTimeout = executionTimer.run(new Runnable() {
			@Override
			public void run() {
				try {
					Object returnedValue = method.invoke(obj, (Object[]) inputs.toArray());
					value.returnedValue = returnedValue;
				} catch (Throwable e) {
					onFail();
				}
			}
		});
		if (!notTimeout) {
			log.debug("timeout!");
			onFail();
		}
	}
	
	@Override
	public boolean visit(REvaluationMethod stmt) {
		visitRmethod(stmt);
		successful = (Boolean) getExecData(stmt.getOutVarId());
		return successful;
	}

	@Override
	public boolean visit(RArrayConstructor arrayConstructor) {
		Class<?> arrayContentType = arrayConstructor.getContentType();
		Object array = Array.newInstance(arrayContentType, arrayConstructor.getSizes());
		
		addExecData(arrayConstructor.getOutVarId(), array);
		return true;
	}

	@Override
	public boolean visit(RArrayAssignment rArrayAssignment) {
		final Object array = getExecData(rArrayAssignment.getArrayVarID());
		final Object element = getExecData(rArrayAssignment.getLocalVariableID());

		int[] location = rArrayAssignment.getIndex();
		Object innerArray = array;
		for (int i = 0; i < location.length - 1; i++) {
			innerArray = Array.get(innerArray, location[i]);
		}
		Array.set(innerArray, location[location.length - 1], element);
		// No need to update exec data
		return true;
	}

	/*----------*/
	public boolean isSuccessful() {
		Assert.assertTrue(successful != null);
		return successful;
	}

}
