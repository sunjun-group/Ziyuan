/**
 * Copyright TODO
 */
package gentest.core.data;

import java.lang.reflect.Type;


/**
 * @author LLT
 * 
 */
public class LocalVariable {
	// varId actually is the variable index in its sequence
	private int varId;
	private Class<?> clazz;
	private Type type;
	
	public LocalVariable(int varId, Class<?> clazz, Type type) {
		
	}
	
	public LocalVariable(int varId, Class<?> clazz) {
		this.varId = varId;
		this.clazz = clazz;
	}

	public int getVarId() {
		return varId;
	}

	public void setVarId(int varId) {
		this.varId = varId;
	}

	public Class<?> getType() {
		return clazz;
	}

	@Override
	public int hashCode() {
		/* varId in a sequence is unique */
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

	@Override
	public String toString() {
		return "LocalVariable [varId=" + varId + ", clazz=" + clazz + "]";
	}
	
	
}
