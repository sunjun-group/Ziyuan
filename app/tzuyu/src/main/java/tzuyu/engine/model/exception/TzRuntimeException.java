package tzuyu.engine.model.exception;

import tzuyu.engine.iface.IPrintStream;

public class TzRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 8435042626559004241L;
	private TzRtExceptionType type;
	
	public TzRuntimeException(String details, Object... params) {
		this(toString(details, params));
	}
	
	public TzRuntimeException(TzRtExceptionType type, String msg) {
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
	
	public TzRtExceptionType getType() {
		return type;
	}
	
	private static String toString(String details, Object... params) {
		if (params == null) {
			return details;
		}
		StringBuilder msg = new StringBuilder(details);
		for (Object param : params) {
			msg.append("\n").append(param.toString());
		}
		return msg.toString();
	}

	public void printStackTrace(IPrintStream out) {
		out.println("-----------------TzuYu error stack trace-------------------");
		out.println(super.getStackTrace());
	}
}
