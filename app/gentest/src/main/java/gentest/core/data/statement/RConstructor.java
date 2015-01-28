/**
 * Copyright TODO
 */
package gentest.core.data.statement;


import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

/**
 * @author LLT
 * 
 */
public class RConstructor extends Statement {
	private final Constructor<?> constructor;

	private List<Class<?>> inputTypesCached;
	private Class<?> outputTypeCached;

	public RConstructor(Constructor<?> ctor) {
		super(RStatementKind.CONSTRUCTOR);
		if (ctor == null) {
			throw new IllegalArgumentException("input constructor is null");
		}
		this.constructor = ctor;
	}

	public Constructor<?> getConstructor() {
		return constructor;
	}

	public static RConstructor of(Constructor<?> ctor) {
		return new RConstructor(ctor);
	}

	public String getName() {
		return constructor.getDeclaringClass().getSimpleName();
	}
	
	public List<Class<?>> getInputTypes() {
		if (inputTypesCached == null) {
			inputTypesCached = Arrays.asList(constructor.getParameterTypes());
		}
		return inputTypesCached;
	}
	
	public Class<?> getOutputType() {
		if (outputTypeCached == null) {
			outputTypeCached = constructor.getDeclaringClass();
		}
		return outputTypeCached;
	}

	@Override
	public boolean accept(StatementVisitor visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public boolean hasOutputVar() {
		return true;
	}
}