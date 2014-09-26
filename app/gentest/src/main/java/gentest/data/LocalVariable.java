/**
 * Copyright TODO
 */
package gentest.data;

/**
 * @author LLT
 * 
 */
public class LocalVariable {
	private int declareStmt = 1; // always
	private int varId = 1;
	private Class<?> type;
	private boolean modified;
	
	public LocalVariable(int declareStmt, int varId, Class<?> type) {
		this.declareStmt = declareStmt;
		this.varId = varId;
		this.type = type;
	}

	public int getDeclareStmt() {
		return declareStmt;
	}
	
	public void update(int stmt, int varId) {
		this.declareStmt = stmt;
		this.varId = varId;
		modified = true;
	}

	public void setDeclareStmt(int declareStmt) {
		this.declareStmt = declareStmt;
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

	public boolean isModified() {
		return modified;
	}
}
