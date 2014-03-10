package tzuyu.engine.runtime;

import tzuyu.engine.iface.TzPrintStream;
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

import tzuyu.engine.TzConfiguration;
import tzuyu.engine.model.ExecutionOutcome;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.Statement;
import tzuyu.engine.model.StatementKind;
import tzuyu.engine.model.Variable;
import tzuyu.engine.utils.CollectionsExt;
import tzuyu.engine.utils.Globals;
import tzuyu.engine.utils.PrimitiveTypes;
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

	private RMethod(TzConfiguration config) {
		super(config);
		method = null;
	}

	private RMethod(Method prototype, TzConfiguration config) {
		super(config);
		if (prototype == null) {
			throw new IllegalArgumentException("the method should not be null");
		}
		this.method = prototype;
	}

	public static RMethod getMethod(Method m, TzConfiguration config) {
		return new RMethod(m, config);
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
	public ExecutionOutcome execute(Object[] inputVals, TzPrintStream out) {
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
		return ReflectionUtils.getSignature(method);
	}

	@Override
	public boolean hasReceiverParameter() {
		return !isStatic();
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}

	@Override
	public void appendCode(Variable newVar, List<Variable> inputVars,
			StringBuilder b) {
		if (!isVoid()) {
			b.append(ReflectionUtils.getCompilableName(this.method
					.getReturnType()));
			String cast = "";
			b.append(" " + newVar.getName() + " = " + cast);
		}
		String receiverString = isStatic() ? null : inputVars.get(0).getName();
		appendReceiverOrClassForStatics(receiverString, b);

		b.append(".");
		b.append(getTypeArguments());
		b.append(this.method.getName() + "(");

		int startIndex = (isStatic() ? 0 : 1);
		for (int i = startIndex; i < inputVars.size(); i++) {
			if (i > startIndex)
				b.append(", ");

			// CASTING.
			// We cast whenever the variable and input types are not identical.
			// We also cast if input type is a primitive, because Randoop uses
			// boxed primitives, and need to convert back to primitive.
			if (PrimitiveTypes.isPrimitive(getInputTypes().get(i))
					&& longFormat) {
				b.append("(" + getInputTypes().get(i).getName() + ")");
			} else if (!inputVars.get(i).getType()
					.equals(getInputTypes().get(i))) {
				b.append("(" + getInputTypes().get(i).getCanonicalName() + ")");
			}

			// In the short output format, statements like "int x = 3" are not
			// added to a sequence; instead,
			// the value (e.g. "3") is inserted directly added as arguments to
			// method calls.
			Statement stmt = inputVars.get(i).getDeclaringStatement();
			if (!longFormat
					&& Sequence.canUseShortFormat(stmt)) {
				Object val = ((RAssignment) stmt.getAction().getAction())
						.getValue();
				b.append(PrimitiveTypes.toCodeString(val, stringMaxLength));
			} else {
				b.append(inputVars.get(i).getName());
			}
		}

		b.append(");" + Globals.lineSep);

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

	private void appendReceiverOrClassForStatics(String receiverString,
			StringBuilder b) {
		if (isStatic()) {
			String s2 = this.method.getDeclaringClass().getName()
					.replace('$', '.');
			// TODO combine this with last if clause
			b.append(s2);
		} else {
			Class<?> expectedType = getInputTypes().get(0);
			String canonicalName = expectedType.getCanonicalName();
			boolean mustCast = canonicalName != null
					&& PrimitiveTypes
							.isBoxedPrimitiveTypeOrString(expectedType)
					&& !expectedType.equals(String.class);
			if (mustCast) {
				// this is a little paranoid but we need to cast primitives in
				// order to get them boxed.
				b.append("((" + canonicalName + ")" + receiverString + ")");
			} else {
				b.append(receiverString);
			}
		}
	}

}
