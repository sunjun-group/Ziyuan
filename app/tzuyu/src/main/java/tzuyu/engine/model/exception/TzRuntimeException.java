package tzuyu.engine.model.exception;

import java.io.PrintStream;

public class TzRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 8435042626559004241L;
	private ExceptionType type;
	
	public TzRuntimeException(ExceptionType type, String msg) {
		this(msg);
		this.type = type;
	}

	public TzRuntimeException(String details) {
		super(details);
	}

	public TzRuntimeException(Throwable cause) {
		super(cause);
	}

	public TzRuntimeException(String details, Throwable cause) {
		super(details, cause);
	}

	public void printStackTrace(PrintStream out) {
		out.println("-----------------TzuYu error stack trace-------------------");
		super.printStackTrace(out);
	}
}
