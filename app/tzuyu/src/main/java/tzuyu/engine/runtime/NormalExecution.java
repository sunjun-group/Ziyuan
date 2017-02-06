package tzuyu.engine.runtime;

import tzuyu.engine.model.ExecutionOutcome;

public class NormalExecution implements ExecutionOutcome {

	/**
	 * The return value of one method call
	 */
	private final Object retval;
	private final long executionTime;
	/**
	 * The out reference parameters which this method call may change its
	 * values.
	 */
	private final Object[] outVals;

	public NormalExecution(Object val, Object[] outArgs, long time) {
		this.retval = val;
		this.outVals = outArgs;
		this.executionTime = time;
	}

	public long getExecutionTime() {
		return this.executionTime;
	}

	public Object getRetunValue() {
		return this.retval;
	}

	public Object getOutReferenceValue(int index) {
		if (index < 0 || index >= outVals.length) {
			throw new IllegalArgumentException("index out of bound");
		}
		return outVals[index];
	}

	public Object[] getOutValues() {
		return this.outVals;
	}

}
