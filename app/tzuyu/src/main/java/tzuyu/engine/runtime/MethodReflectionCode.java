package tzuyu.engine.runtime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import tzuyu.engine.utils.Globals;
import tzuyu.engine.utils.ReflectionUtils;

public final class MethodReflectionCode extends ReflectionCode {

	private final Method method;
	private final Object receiver;
	private final Object[] inputs;
	private Object retval;
	private Throwable exceptionThrown;

	public MethodReflectionCode(Method method, Object receiver, Object[] inputs) {
		if (method == null)
			throw new IllegalArgumentException("method is null");
		if (inputs == null)
			throw new IllegalArgumentException("inputs is null");

		this.receiver = receiver;
		this.method = method;
		this.inputs = inputs;
		checkCompatability();
	}

	public Method getMethod() {
		return this.method;
	}

	public Object getReceiver() {
		return this.receiver;
	}

	public Object[] getInputs() {
		return this.inputs.clone();
	}

	private void checkCompatability() {
		if (!Globals.DEBUG) {
			return;
		}
		String error = ReflectionUtils.checkArgumentTypes(inputs,
				method.getParameterTypes(), method);
		if (error != null) {
			throw new IllegalArgumentException(error);
		}

		if (Modifier.isStatic(this.method.getModifiers())) {
			if (receiver != null) {
				throw new IllegalArgumentException(
						"receiver must be null for static method.");
			}
		} else {
			if (!ReflectionUtils.canBePassedAsArgument(receiver,
					method.getDeclaringClass())) {
				throw new IllegalArgumentException("method " + method
						+ "cannot be invoked on " + receiver);
			}
		}
	}

	@Override
	public String toString() {
		String ret = "Call to " + method + " receiver:" + receiver + " args:"
				+ Arrays.toString(inputs);
		if (!hasRunAlready()) {
			return ret + " not run yet";
		} else if (exceptionThrown == null) {
			return ret + "returned:" + retval;
		} else {
			return ret + " thew:" + exceptionThrown;
		}
	}

	@Override
	protected void runReflectionCodeRaw() throws InstantiationException,
			IllegalAccessException, InvocationTargetException,
			NotCaughtIllegalStateException {
		if (hasRunAlready()) {
			throw new NotCaughtIllegalStateException("cannot run this twice "
					+ this);
		}

		this.setRunAlready();

		if (!this.method.isAccessible()) {
			this.method.setAccessible(true);
		}

		try {
			assert this.method != null;
			this.retval = this.method.invoke(this.receiver, this.inputs);

			if (receiver == null && isInstanceMethod()) {
				throw new NotCaughtIllegalStateException(
						"receiver was null - expected NPE from call to: "
								+ method);
			}
		} catch (NullPointerException e) {
			this.exceptionThrown = e;
			throw e;
		} catch (InvocationTargetException e) {
			this.exceptionThrown = e.getCause();
			throw e;
		} finally {
			if (retval != null && exceptionThrown != null) {
				throw new NotCaughtIllegalStateException(
						"cannot have both retval and exception not null");
			}
		}
	}

	@Override
	public Object getReturnVariable() {
		if (!hasRunAlready())
			throw new IllegalStateException("run first, then check");
		if (receiver == null && retval != null && isInstanceMethod()) {
			throw new IllegalStateException(
					"receiver was null -expecteed NPE from cal to " + method);
		}
		return retval;
	}

	private boolean isInstanceMethod() {
		return !Modifier.isStatic(method.getModifiers());
	}

	@Override
	public Throwable getExceptionThrown() {
		if (!hasRunAlready())
			throw new IllegalStateException("run first, then ask");
		if (receiver == null
				&& !(exceptionThrown instanceof NullPointerException)) {
			throw new IllegalStateException(
					"receive was null - expeceted NPE from call to " + method);
		}
		return exceptionThrown;
	}

}
