/**
 * Copyright TODO
 */
package gentest.core.data.statement;


import java.lang.reflect.Constructor;

/**
 * @author LLT
 * 
 */
public class RConstructor extends Statement {
	private final Constructor<?> constructor;

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

	public Class<?> getDeclaringClass() {
		return constructor.getDeclaringClass();
	}
	
	public Class<?>[] getInputTypes() {
		return constructor.getParameterTypes();
	}
	
	public Class<?> getOutputType() {
		return constructor.getDeclaringClass();
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