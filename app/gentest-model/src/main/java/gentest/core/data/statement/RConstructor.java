/**
 * Copyright TODO
 */
package gentest.core.data.statement;


import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import gentest.utils.SignatureUtils;

/**
 * @author LLT
 * 
 */
public class RConstructor extends Statement {
	private static final long serialVersionUID = -8022794511239861321L;
	private transient Constructor<?> constructor;
	private Class<?> declareClass; // lazy-set field
	private String signature; // lazy-set field

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
	
	public void fillMissingInfo() {
		if (declareClass == null) {
			declareClass = constructor.getDeclaringClass();
			signature = SignatureUtils.getSignature(constructor);
		} else if (constructor == null) {
			for (Constructor<?> constr : declareClass.getConstructors()) {
				if (signature.equals(SignatureUtils.getSignature(constr))) {
					constructor = constr;
					break;
				}
			}
		}
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
	
	@Override
	public String toString() {
		return constructor.toString();
	}
	
	public boolean isMemberNestedConstructor() {
		Class<?> declaringClass = getDeclaringClass();
		return declaringClass.getDeclaringClass() != null && !Modifier.isStatic(declaringClass.getModifiers());
	}
}