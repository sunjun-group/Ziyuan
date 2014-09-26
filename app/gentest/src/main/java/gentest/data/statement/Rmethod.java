/**
 * Copyright TODO
 */
package gentest.data.statement;


import java.lang.reflect.Method;

/**
 * @author LLT
 *
 */
public class Rmethod extends Statement {
	private Method method;
	private int receiverVarId; // varId of declared class
	
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
	public void accept(StatementVisitor visitor) throws Throwable {
		visitor.visit(this);
	}

}
 