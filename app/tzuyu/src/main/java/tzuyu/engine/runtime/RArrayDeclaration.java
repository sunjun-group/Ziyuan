package tzuyu.engine.runtime;

import tzuyu.engine.iface.TzPrintStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tzuyu.engine.TzConfiguration;
import tzuyu.engine.model.ExecutionOutcome;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.Statement;
import tzuyu.engine.model.StatementKind;
import tzuyu.engine.model.Variable;
import tzuyu.engine.utils.Globals;
import tzuyu.engine.utils.PrimitiveTypes;

public class RArrayDeclaration extends StatementKind implements Serializable {

	private static final long serialVersionUID = 384299202378512055L;

	private final Class<?> elementType;
	private final int length;

	private List<Class<?>> inputTypesCached;
	private Class<?> outputTypeCached;
	private int hashCodeCached;
	private boolean hashCodeComputed = false;

	public RArrayDeclaration(Class<?> componentType, int size,
			TzConfiguration config) {
		super(config);
		if (componentType == null) {
			throw new IllegalArgumentException("array element type is null");
		}
		if (size < 0) {
			throw new IllegalArgumentException("array size is negative: "
					+ size);
		}
		elementType = componentType;
		length = size;
	}

	public Class<?> getElementType() {
		return elementType;
	}

	public int getLength() {
		return length;
	}

	@Override
	public Class<?> getReturnType() {
		if (outputTypeCached == null) {
			outputTypeCached = Array.newInstance(elementType, 0).getClass();
		}
		return outputTypeCached;
	}

	@Override
	public List<Class<?>> getInputTypes() {
		if (inputTypesCached == null) {
			this.inputTypesCached = new ArrayList<Class<?>>(length);
			for (int i = 0; i < length; i++)
				inputTypesCached.add(elementType);
			inputTypesCached = Collections.unmodifiableList(inputTypesCached);
		}
		return Collections.unmodifiableList(this.inputTypesCached);
	}

	@Override
	public ExecutionOutcome execute(Object[] inputVals, TzPrintStream out) {
		if (inputVals.length > length)
			throw new IllegalArgumentException("Too many arguments:"
					+ inputVals.length + " capacity:" + length);
		long startTime = System.currentTimeMillis();
		assert inputVals.length == this.length;
		Object theArray = Array.newInstance(this.elementType, this.length);
		for (int i = 0; i < inputVals.length; i++)
			Array.set(theArray, i, inputVals[i]);
		long totalTime = System.currentTimeMillis() - startTime;

		return new NormalExecution(theArray, inputVals, totalTime);
	}

	@Override
	public int hashCode() {
		if (!hashCodeComputed) {
			hashCodeComputed = true;
			hashCodeCached = this.elementType.hashCode();
			hashCodeCached += this.length * 17;
		}
		return hashCodeCached;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof RArrayDeclaration)) {
			return false;
		}

		RArrayDeclaration otherArrayDecl = (RArrayDeclaration) o;
		if (!this.elementType.equals(otherArrayDecl.elementType)) {
			return false;
		}

		if (this.length != otherArrayDecl.length) {
			return false;
		}

		return true;
	}

	@Override
	public boolean hasNoArguments() {
		return length == 0;
	}

	@Override
	public String toParseableString() {
		return elementType.getName() + "[" + Integer.toString(length) + "]";
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
		if (inputVars.size() > length)
			throw new IllegalArgumentException("Too many arguments:"
					+ inputVars.size() + " capacity:" + length);
		String declaringClass = this.elementType.getCanonicalName();
		b.append(declaringClass + "[] " + newVar.getName() + " = new "
				+ declaringClass + "[] { ");
		for (int i = 0; i < inputVars.size(); i++) {
			if (i > 0)
				b.append(", ");

			// In the short output format, statements like "int x = 3" are not
			// added
			// to a sequence; instead, the value (e.g. "3") is inserted directly
			// added
			// as arguments to method calls.
			Statement stmt = inputVars.get(i).getDeclaringStatement();
			if (!longFormat && Sequence.canUseShortFormat(stmt)) {
				b.append(PrimitiveTypes.toCodeString(((RAssignment) stmt
						.getAction().getAction()).getValue(), stringMaxLength));
			} else {
				b.append(inputVars.get(i).getName());
			}
		}
		b.append("};");
		b.append(Globals.lineSep);
	}
}
