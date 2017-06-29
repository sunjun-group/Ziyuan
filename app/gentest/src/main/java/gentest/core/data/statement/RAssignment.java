/**
 * Copyright TODO
 */
package gentest.core.data.statement;

import sav.common.core.utils.StringUtils;

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
	public boolean accept(StatementVisitor visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public String toString() {
		return new StringBuilder().append(type.getSimpleName())
				.append(" ").append(String.valueOf(outVarId))
				.append(": ")
				.append(StringUtils.toStringNullToEmpty(value))
				.toString();
	}
}
