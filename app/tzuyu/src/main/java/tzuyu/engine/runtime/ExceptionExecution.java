package tzuyu.engine.runtime;

import tzuyu.engine.model.ExecutionOutcome;

public class ExceptionExecution implements ExecutionOutcome {

	private final Throwable exception;
	private final long executionTime;

	public ExceptionExecution(Throwable throwable, long time) {
		this.exception = throwable;
		this.executionTime = time;
	}

	public long getExecutionTime() {
		return this.executionTime;
	}

	public Throwable getException() {
		return this.exception;
	}

}
