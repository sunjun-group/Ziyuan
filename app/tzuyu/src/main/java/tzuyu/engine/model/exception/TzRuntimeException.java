package tzuyu.engine.model.exception;

import tzuyu.engine.iface.TzPrintStream;

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
	
	public ExceptionType getType() {
		return type;
	}

	public void printStackTrace(TzPrintStream out) {
		out.println("-----------------TzuYu error stack trace-------------------");
		out.println(super.getStackTrace());
	}
}
