package tzuyu.engine.runtime;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sav.common.core.iface.IPrintStream;
import tzuyu.engine.model.ExecutionOutcome;
import tzuyu.engine.model.StatementKind;

public class RArrayDeclaration extends StatementKind implements Serializable {

	private static final long serialVersionUID = 384299202378512055L;

	private final Class<?> elementType;
	private final int length;

	private List<Class<?>> inputTypesCached;
	private Class<?> outputTypeCached;
	private int hashCodeCached;
	private boolean hashCodeComputed = false;

	public RArrayDeclaration(Class<?> componentType, int size) {
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
	public ExecutionOutcome execute(Object[] inputVals, IPrintStream out) {
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
}
