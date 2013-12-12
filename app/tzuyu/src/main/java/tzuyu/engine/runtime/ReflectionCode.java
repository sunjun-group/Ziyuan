package tzuyu.engine.runtime;

import java.lang.reflect.InvocationTargetException;

import tzuyu.engine.runtime.TzuYuSecurityManager;
import tzuyu.engine.utils.LogicUtils;

public abstract class ReflectionCode {

  /**
   * This code has been run already
   */
  private boolean runAlready;

  /**
   * Runs the reflection code that this object represents, but first, if
   * System.getSecurityManager() returns a TzuYuSecurityManager, this method
   * sets the security manager's status to ON. Before exiting, this method sets
   * the security manager's status to its status before this call.
   */
  public final void runReflectionCode()
      throws InstantiationException, IllegalAccessException,
      InvocationTargetException, NotCaughtIllegalStateException {

    // The following few lines attempt to find out if there is a
    // RandoopSecurityManager installed, and if so, record its status.
    TzuYuSecurityManager randoopsecurity = null;
    TzuYuSecurityManager.Status oldStatus = null;

    SecurityManager security = System.getSecurityManager();
    if (security != null && security instanceof TzuYuSecurityManager) {
      randoopsecurity = (TzuYuSecurityManager) security;
      oldStatus = randoopsecurity.status;
      randoopsecurity.status = TzuYuSecurityManager.Status.ON;
    }

    // At this point, this is the state of the method.
    assert LogicUtils.iff(security != null
        && security instanceof TzuYuSecurityManager, randoopsecurity != null
        && oldStatus != null);

    try {

      runReflectionCodeRaw();

    } finally {

      // Before exiting, restore the RandoopSecurityManager's status to
      // its original status, if such a manager was installed.
      if (randoopsecurity != null) {
        assert oldStatus != null;
        randoopsecurity.status = oldStatus;
      }
    }
  }

  /**
   * Executed the reflection code. All internal exceptions must be thrown as
   * NotCaughtIllegalStateException because everything else is caught.
   * 
   */
  protected abstract void runReflectionCodeRaw()
      throws InstantiationException, IllegalAccessException,
      InvocationTargetException, NotCaughtIllegalStateException;

  protected final void setRunAlready() {
    // called from inside runReflectionCode, so use
    // NotCaughtIllegalStateException
    if (runAlready)
      throw new NotCaughtIllegalStateException("cannot call this twice");
    runAlready = true;
  }

  public abstract Object getReturnVariable();

  public abstract Throwable getExceptionThrown();

  public final boolean hasRunAlready() {
    return runAlready;
  }

  /*
   * See comment in runReflectionCode
   */
  static final class NotCaughtIllegalStateException extends
      IllegalStateException {
    private static final long serialVersionUID = -4892218435517506705L;

    NotCaughtIllegalStateException(String msg) {
      super(msg);
    }
  }
}
