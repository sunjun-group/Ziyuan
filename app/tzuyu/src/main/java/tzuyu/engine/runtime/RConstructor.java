package tzuyu.engine.runtime;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tzuyu.engine.iface.IPrintStream;
import tzuyu.engine.model.ExecutionOutcome;
import tzuyu.engine.model.StatementKind;
import tzuyu.engine.utils.ReflectionUtils;

public class RConstructor extends StatementKind implements Serializable {
	private static final long serialVersionUID = -155057976108691030L;

	private final Constructor<?> constructor;

	private List<Class<?>> inputTypesCached;
	private Class<?> outputTypeCached;

	private int hashCodeCached = 0;
	private boolean hashCodeComputed = false;

	private RConstructor(Constructor<?> ctor) {
		if (ctor == null) {
			throw new IllegalArgumentException("input constructor is null");
		}
		this.constructor = ctor;
	}

	public Constructor<?> getConstructor() {
		return constructor;
	}

	public static RConstructor getCtor(Constructor<?> ctor) {
		return new RConstructor(ctor);
	}

	@Override
	public Class<?> getReturnType() {
		if (outputTypeCached == null) {
			outputTypeCached = constructor.getDeclaringClass();
		}
		return outputTypeCached;
	}

	@Override
	public List<Class<?>> getInputTypes() {
		if (inputTypesCached == null) {
			inputTypesCached = new ArrayList<Class<?>>(
					Arrays.asList(constructor.getParameterTypes()));
		}
		return inputTypesCached;
	}

	@Override
	public ExecutionOutcome execute(Object[] inputVals, IPrintStream out) {
		assert inputVals.length == getInputTypes().size();
		ConstructorReflectionCode code = new ConstructorReflectionCode(
				this.constructor, inputVals);

		long startTime = System.currentTimeMillis();
		Throwable thrown = ReflectionExecutor.executeReflectionCode(code, out);
		long time = System.currentTimeMillis() - startTime;

		if (thrown == null) {
			return new NormalExecution(code.getReturnVariable(), inputVals,
					time);
		} else {
			return new ExceptionExecution(thrown, 0);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof RConstructor)) {
			return false;
		}

		RConstructor other = (RConstructor) o;

		return this.constructor.equals(other.constructor);
	}

	@Override
	public int hashCode() {
		if (!hashCodeComputed) {
			hashCodeComputed = true;
			hashCodeCached = this.constructor.hashCode();
		}
		return hashCodeCached;
	}

	@Override
	public boolean hasNoArguments() {
		return getInputTypes().size() == 0;
	}

	@Override
	public boolean isConstructor() {
		return true;
	}

	@Override
	public String toParseableString() {
		return ReflectionUtils.getSignature(constructor);
	}

	@Override
	public boolean hasReceiverParameter() {
		return false;
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}
}
