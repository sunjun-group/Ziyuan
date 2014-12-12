/**
 * Copyright TODO
 */
package gentest;

import gentest.data.Sequence;
import gentest.data.statement.RArrayAssignment;
import gentest.data.statement.RArrayConstructor;
import gentest.data.statement.RAssignment;
import gentest.data.statement.RConstructor;
import gentest.data.statement.REvaluationMethod;
import gentest.data.statement.Rmethod;
import gentest.data.statement.Statement;
import gentest.data.statement.StatementVisitor;
import gentest.data.variable.ISelectedVariable;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ClassUtils;

import junit.framework.AssertionFailedError;
import sav.common.core.utils.Assert;


/**
 * @author LLT
 * A runtimeExecutor of a {@link Sequence}. 
 * to execute a {@link Sequence} during the generation
 * and run whenever a new method added to the {@link Sequence}
 */
public class RuntimeExecutor implements StatementVisitor {
	private RuntimeData data;
	private Sequence sequence;
	private Boolean successful;
	
	public RuntimeExecutor() {
		data = new RuntimeData();
	}

	public void reset(Sequence sequence) {
		this.sequence = sequence;
		successful = null;
		data.reset();
	}
	
	public boolean executeReceiver(ISelectedVariable param) {
		if (successful == null) {
			successful = true;
		}

		return execute(param);
	}

	private boolean execute(ISelectedVariable param) {
		for (Statement stmt : param.getStmts()) {
			try {
				stmt.accept(this);
			} catch(Throwable ex) {
				//TODO LOG
				System.err.println(ex);
				successful = false;
				return successful;
			}
		}
		return successful;
	}
	
	public boolean execute(Rmethod rmethod, List<ISelectedVariable> selectParams) {
		for (ISelectedVariable param : selectParams) {
			if (!execute(param)) {
				return successful;
			}
		}
		try {
			rmethod.accept(this);
		} catch (Throwable e) {
			successful = false;
		}
		return successful;
	}
	
	/* visitor part */
	@Override
	public void visit(RAssignment stmt) throws Throwable {
		data.addExecData(stmt.getOutVarId(), stmt.getValue());
	}
	
	@Override
	public void visit(RConstructor stmt) throws IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		List<Object> inputs = new ArrayList<Object>(stmt.getInVarIds().length);
		for (int var : stmt.getInVarIds()) {
			inputs.add(data.getExecData(var));
		}
		Object newInstance = stmt.getConstructor().newInstance(
				(Object[]) inputs.toArray());
		// update data
		for (int i = 0; i < stmt.getInVarIds().length; i++) {
			data.addExecData(stmt.getInVarIds()[i], inputs.get(i));
		}
		data.addExecData(stmt.getOutVarId(), newInstance);
	}
	
	@Override
	public void visitRmethod(Rmethod stmt) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		List<Object> inputs = new ArrayList<Object>(stmt.getInVarIds().length);
		for (int var : stmt.getInVarIds()) {
			inputs.add(data.getExecData(var));
		}
		Object returnedValue = stmt.getMethod().invoke(
				data.getExecData(stmt.getReceiverVarId()),
				(Object[]) inputs.toArray());
		// update data
		for (int i = 0; i < stmt.getInVarIds().length; i++) {
			data.addExecData(stmt.getInVarIds()[i], inputs.get(i));
		}
		if (stmt.getOutVarId() != Statement.INVALID_VAR_ID) {
			data.addExecData(stmt.getOutVarId(), returnedValue);
		}
	}
	
	@Override
	public void visit(REvaluationMethod stmt) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		visitRmethod(stmt);
		if (!Boolean.TRUE.equals(data.getExecData(stmt.getOutVarId()))) {
			throw new AssertionFailedError("Assert true fail");
		}
	}

	@Override
	public void visit(RArrayConstructor arrayConstructor) {
		Class<?> arrayContentType = arrayConstructor.getContentType();
		Object array = Array.newInstance(arrayContentType, arrayConstructor.getSizes());
		
		data.addExecData(arrayConstructor.getOutVarId(), array);
	}

	@Override
	public void visit(RArrayAssignment rArrayAssignment) {
		final Object array = data.getExecData(rArrayAssignment.getArrayVarID());
		final Object element = data.getExecData(rArrayAssignment.getLocalVariableID());

		int[] location = rArrayAssignment.getIndex();
		Object innerArray = array;
		for (int i = 0; i < location.length - 1; i++) {
			innerArray = Array.get(innerArray, location[i]);
		}
		Array.set(innerArray, location[location.length - 1], element);
		// No need to update exec data
	}

	/*----------*/
	public boolean isSuccessful() {
		Assert.assertTrue(successful != null);
		return successful;
	} 
	
	public Sequence getSequence() {
		return sequence;
	}
}
