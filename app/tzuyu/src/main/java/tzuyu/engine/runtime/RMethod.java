package tzuyu.engine.runtime;

import java.io.Serializable;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tzuyu.engine.Tzuyu;
import tzuyu.engine.iface.IPrintStream;
import tzuyu.engine.model.ExecutionOutcome;
import tzuyu.engine.model.StatementKind;
import tzuyu.engine.utils.CollectionsExt;
import tzuyu.engine.utils.ReflectionUtils;

public class RMethod extends StatementKind implements Serializable {

	private static final long serialVersionUID = -4838901034344775147L;

	private final Method method;

	private List<Class<?>> inputTypesCached;
	private Class<?> outputTypeCached;

	private boolean hashCodeComputed = false;
	private int hashCodeCached = 0;
	private boolean isVoidComputed = false;
	private boolean isVoidCached = false;
	// null: have not checked yet, true/false: is static or not.  
	private Boolean isStatic = null; 
	private String[] params;

	private RMethod() {
		method = null;
	}

	private RMethod(Method prototype) {
		if (prototype == null) {
			throw new IllegalArgumentException("the method should not be null");
		}
		this.method = prototype;
	}

	public static RMethod getMethod(Method m) {
		return new RMethod(m);
	}

	public Method getMethod() {
		return method;
	}

	public boolean isVoid() {
		if (!isVoidComputed) {
			isVoidCached = void.class.equals(method.getReturnType());
			isVoidComputed = true;
		}
		return isVoidCached;
	}

	@Override
	public boolean isStatic() {
		if (isStatic == null) {
			isStatic = Modifier.isStatic(method.getModifiers());
		}
		return isStatic;
	}

	@Override
	public Class<?> getReturnType() {
		if (outputTypeCached == null) {
			outputTypeCached = method.getReturnType();
		}
		return outputTypeCached;
	}

	@Override
	public List<Class<?>> getInputTypes() {
		if (inputTypesCached == null) {
			Class<?>[] parameterTypes = method.getParameterTypes();
			inputTypesCached = new ArrayList<Class<?>>(parameterTypes.length
					+ (isStatic() ? 0 : 1));
			if (!isStatic())
				inputTypesCached.add(method.getDeclaringClass());
			for (int i = 0; i < parameterTypes.length; i++) {
				inputTypesCached.add(parameterTypes[i]);
			}
		}
		return inputTypesCached;
	}

	@Override
	public ExecutionOutcome execute(Object[] inputVals, IPrintStream out) {
		assert inputVals.length == getInputTypes().size();

		Object receiver = null;
		int paramsLength = getInputTypes().size();
		int paramsStartIndex = 0;

		if (!isStatic()) {
			receiver = inputVals[0];
			paramsLength--;
			paramsStartIndex = 1;
		}

		Object[] params = new Object[paramsLength];
		for (int i = 0; i < params.length; i++) {
			params[i] = inputVals[i + paramsStartIndex];
		}

		MethodReflectionCode code = new MethodReflectionCode(method, receiver,
				params);
		long startTime = System.currentTimeMillis();
		Throwable thrown = ReflectionExecutor.executeReflectionCode(code, out);
		long time = System.currentTimeMillis() - startTime;
		if (thrown == null)
			return new NormalExecution(code.getReturnVariable(), inputVals,
					time);
		else
			return new ExceptionExecution(thrown, 0);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof RMethod)) {
			return false;
		}

		RMethod other = (RMethod) o;

		return this.method.equals(other.method);
	}

	@Override
	public int hashCode() {
		if (!hashCodeComputed) {
			hashCodeComputed = true;
			hashCodeCached = this.method.hashCode();
		}
		return hashCodeCached;
	}

	@Override
	public boolean hasNoArguments() {
		// LLT: Why isStatic means hasArguments?
		return getInputTypes().size() <= 1 && (!isStatic());
	}

	@Override
	public String toParseableString() {
		return ReflectionUtils.getSignature(method, getParamNames());
	}
	
	public String[] getParamNames() {
		if (params == null) {
			params = Tzuyu.getParamNameDiscoverer().getParameterNames(method);
		}
		return params;
	}

	@Override
	public boolean hasReceiverParameter() {
		return !isStatic();
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}

	public String getTypeArguments() {
		TypeVariable<Method>[] typeParameters = method.getTypeParameters();
		if (typeParameters.length == 0)
			return "";
		StringBuilder b = new StringBuilder();
		Class<?>[] params = new Class<?>[typeParameters.length];
		b.append("<");
		for (int i = 0; i < typeParameters.length; i++) {
			if (i > 0) {
				b.append(",");
			}
			Type firstBound = typeParameters[i].getBounds().length == 0 ? Object.class
					: typeParameters[i].getBounds()[0];
			params[i] = getErasure(firstBound);
			b.append(getErasure(firstBound).getCanonicalName());
		}
		b.append(">");
		// if all are object, then don't bother
		if (CollectionsExt.findAll(Arrays.asList(params), Object.class).size() == params.length) {
			return "";
		}
		return b.toString();
	}

	private static Class<?> getErasure(Type t) {
		if (t instanceof Class<?>)
			return (Class<?>) t;
		if (t instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) t;
			return getErasure(pt.getRawType());
		}

		if (t instanceof TypeVariable<?>) {
			TypeVariable<?> tv = (TypeVariable<?>) t;
			Type[] bounds = tv.getBounds();
			Type firstBound = bounds.length == 0 ? Object.class : bounds[0];
			return getErasure(firstBound);
		}

		if (t instanceof GenericArrayType) {
			throw new UnsupportedOperationException(
					"erasure of arrays not implemented " + t);
		}

		if (t instanceof WildcardType) {
			throw new UnsupportedOperationException(
					"erasure of wildcards not implemented " + t);
		}

		throw new IllegalStateException("unexpected type " + t);
	}

}
