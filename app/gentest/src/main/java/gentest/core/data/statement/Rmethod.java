/**
 * Copyright TODO
 */
package gentest.core.data.statement;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author LLT
 *
 */
public class Rmethod extends Statement {
	private Method method;
	private final int receiverVarId; // varId of declared class
	
	public Rmethod(Method staticMethod) {
		super(RStatementKind.METHOD_INVOKE);
		this.method = staticMethod;
		receiverVarId = INVALID_VAR_ID;
	}
	
	public Rmethod(Method method, int scopeId) {
		super(RStatementKind.METHOD_INVOKE);
		this.method = method;
		this.receiverVarId = scopeId;
	}
	
	public static Rmethod of(Method method, int scopeId) {
		return new Rmethod(method, scopeId);
	}

	public String getName() {
		return method.getName();
	}

	public int getReceiverVarId() {
		return receiverVarId;
	}

	public Class<?> getReturnType() {
		return method.getReturnType();
	}
	
	public Method getMethod() {
		return method;
	}
	
	@Override
	public boolean accept(StatementVisitor visitor) {
		return visitor.visitRmethod(this);
	}
	
	@Override
	public boolean hasOutputVar() {
		return outVarId != INVALID_VAR_ID;
	}
	
	public boolean hasReturnType() {
		return !getReturnType().equals(Void.TYPE);
	}
	
	public Class<?> getDeclaringType() {
		return method.getDeclaringClass();
	}

	public boolean isStatic() {
		return Modifier.isStatic(method.getModifiers());
	}
	
	@Override
	public String toString() {
		return method.toString();
	}
}
 