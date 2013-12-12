package tzuyu.engine.runtime;

/**
 * @author Randoop
 */
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import tzuyu.engine.utils.ReflectionUtils;

public class ConstructorReflectionCode extends ReflectionCode {

  private final Constructor<?> constructor;
  private final Object[] inputs;
  private Object retval;
  private Throwable exceptionThrown;

  public ConstructorReflectionCode(Constructor<?> ctor, Object[] arguments) {
    if (ctor == null)
      throw new IllegalArgumentException("");
    if (arguments == null)
      throw new IllegalArgumentException("");
    this.constructor = ctor;
    this.inputs = arguments;
    checkRepetion();
  }

  private void checkRepetion() {
    String error = ReflectionUtils.checkArgumentTypes(inputs,
        constructor.getParameterTypes(), constructor);
    if (error != null)
      throw new IllegalArgumentException(error);
  }

  public Constructor<?> getConstructor() {
    return this.constructor;
  }

  public Object[] getInputs() {
    return this.inputs.clone();
  }

  @Override
  public String toString() {
    String ret = "Call " + constructor + " args: " + Arrays.toString(inputs);
    if (hasRunAlready())
      return ret + "not run yet";
    else if (exceptionThrown == null)
      return ret + " returned: " + retval;
    else
      return ret + " throw " + exceptionThrown;
  }

  @Override
  protected void runReflectionCodeRaw()
      throws InstantiationException, IllegalAccessException,
      InvocationTargetException {
    if (hasRunAlready())
      throw new NotCaughtIllegalStateException("cannot run twice");

    this.setRunAlready();

    if (!this.constructor.isAccessible()) {
      this.constructor.setAccessible(true);
    }

    try {
      this.retval = this.constructor.newInstance(this.inputs);
    } catch (InvocationTargetException e) {
      throw e;
    } finally {
      if (retval != null && exceptionThrown != null)
        throw new NotCaughtIllegalStateException(
            "cannot have both retval and exception not null");
    }

  }

  @Override
  public Object getReturnVariable() {
    if (!hasRunAlready())
      throw new IllegalStateException("run first, then ask");
    return retval;
  }

  @Override
  public Throwable getExceptionThrown() {
    if (!hasRunAlready())
      throw new IllegalStateException("run first, then ask");
    return exceptionThrown;
  }

}
