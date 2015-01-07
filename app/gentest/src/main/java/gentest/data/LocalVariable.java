/**
 * Copyright TODO
 */
package gentest.data;

import gentest.data.statement.Statement;

import java.util.List;

/**
 * @author LLT
 * 
 */
public class LocalVariable {
	// varId actually is the variable index in its sequence
	private int varId;
	private Class<?> type;
	private List<Statement> initializedStmt;
	
	public LocalVariable(int varId, Class<?> type) {
		this.varId = varId;
		this.type = type;
	}

	public int getVarId() {
		return varId;
	}

	public void setVarId(int varId) {
		this.varId = varId;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		/* we assume that varId in a sequence is unique */
		return varId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocalVariable other = (LocalVariable) obj;
		if (varId != other.varId)
			return false;
		return true;
	}

	
}
