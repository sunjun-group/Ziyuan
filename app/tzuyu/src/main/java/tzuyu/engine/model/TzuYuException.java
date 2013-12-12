package tzuyu.engine.model;

import java.io.PrintStream;

public class TzuYuException extends RuntimeException {

  private static final long serialVersionUID = 8435042626559004241L;

  public TzuYuException(String details) {
    super(details);
  }

  public TzuYuException(Throwable cause) {
    super(cause);
  }

  public TzuYuException(String details, Throwable cause) {
    super(details, cause);
  }

  public void printStackTrace(PrintStream out) {
    out.println("-----------------TzuYu error stack trace-------------------");
    super.printStackTrace(out);
  }
}
