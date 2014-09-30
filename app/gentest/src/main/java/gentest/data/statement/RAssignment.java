/**
 * Copyright TODO
 */
package gentest.data.statement;


/**
 * @author LLT
 * assign a value directly to the variable
 * (for primitive types, String, Enum only, not for object type)
 */
public class RAssignment extends Statement {
	private final Class<?> type;
	private Object value;
	
	public RAssignment(Class<?> type, Object value) {
		super(RStatementKind.ASSIGNMENT);
		this.type = type;
		this.value = value;
	}

	public static RAssignment assignmentFor(Class<?> t, Object v) {
		return new RAssignment(t, v);
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Class<?> getType() {
		return type;
	}
	
	@Override
	public boolean hasOutputVar() {
		return true;
	}
	
	@Override
	public int[] getInVarIds() {
		throw new IllegalArgumentException();
	}

	@Override
	public void accept(StatementVisitor visitor) throws Throwable {
		visitor.visit(this);
	}
}
