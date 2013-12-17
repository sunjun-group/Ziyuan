package tzuyu.engine.runtime;

import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tzuyu.engine.TzConfiguration;
import tzuyu.engine.model.ExecutionOutcome;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.Statement;
import tzuyu.engine.model.StatementKind;
import tzuyu.engine.model.Variable;
import tzuyu.engine.utils.Globals;
import tzuyu.engine.utils.LogicUtils;
import tzuyu.engine.utils.PrimitiveTypes;
import tzuyu.engine.utils.ReflectionUtils;

public class RConstructor extends StatementKind implements Serializable {
	private static final long serialVersionUID = -155057976108691030L;

	private final Constructor<?> constructor;

	private List<Class<?>> inputTypesCached;
	private Class<?> outputTypeCached;

	private int hashCodeCached = 0;
	private boolean hashCodeComputed = false;

	private RConstructor(Constructor<?> ctor, TzConfiguration config) {
		super(config);
		if (ctor == null) {
			throw new IllegalArgumentException("input constructor is null");
		}
		this.constructor = ctor;
	}

	public Constructor<?> getConstructor() {
		return constructor;
	}

	public static RConstructor getCtor(Constructor<?> ctor, TzConfiguration config) {
		return new RConstructor(ctor, config);
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
	public ExecutionOutcome execute(Object[] inputVals, PrintStream out) {
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

	@Override
	public void appendCode(Variable newVar, List<Variable> inputVars,
			StringBuilder b) {
		assert inputVars.size() == this.getInputTypes().size();

		Class<?> declaringClass = constructor.getDeclaringClass();
		boolean isNonStaticMember = !Modifier.isStatic(declaringClass
				.getModifiers()) && declaringClass.isMemberClass();
		assert LogicUtils.implies(isNonStaticMember, inputVars.size() > 0);

		// Note on isNonStaticMember: if a class is a non-static member class,
		// the
		// runtime signature of the constructor will have an additional argument
		// (as the first argument) corresponding to the owning object. When
		// printing
		// it out as source code, we need to treat it as a special case: instead
		// of printing "new Foo(x,y.z)" we have to print "x.new Foo(y,z)".

		// TODO the last replace is ugly. There should be a method that does it.
		String declaringStr = ReflectionUtils.getCompilableName(declaringClass);

		b.append(declaringStr
				+ " "
				+ newVar.getName()
				+ " = "
				+ (isNonStaticMember ? inputVars.get(0) + "." : "")
				+ "new "
				+ (isNonStaticMember ? declaringClass.getSimpleName()
						: declaringStr) + "(");
		for (int i = (isNonStaticMember ? 1 : 0); i < inputVars.size(); i++) {
			if (i > (isNonStaticMember ? 1 : 0))
				b.append(", ");

			// We cast whenever the variable and input types are not identical.
			if (!inputVars.get(i).getType().equals(getInputTypes().get(i)))
				b.append("(" + getInputTypes().get(i).getCanonicalName() + ")");

			// In the short output format, statements like "int x = 3" are not
			// added
			// to a sequence; instead, the value (e.g. "3") is inserted directly
			// added as arguments to method calls.
			Statement stmt = inputVars.get(i).getDeclaringStatement();
			if (!longFormat
					&& Sequence.canUseShortFormat(stmt)) {
				b.append(PrimitiveTypes.toCodeString(((RAssignment) stmt
						.getAction().getAction()).getValue(), stringMaxLength));
			} else {
				b.append(inputVars.get(i).getName());
			}
		}
		b.append(");");
		b.append(Globals.lineSep);

	}
}
