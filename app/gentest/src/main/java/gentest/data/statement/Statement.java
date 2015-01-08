/**
 * Copyright TODO
 */
package gentest.data.statement;

/**
 * @author LLT
 * This is a data class, it suppose to content data only,
 * not any specific business function.
 * If we have a need to have any abstract business function for any kind of statement,
 * just implement {@link StatementVisitor}
 * and using method {@link Statement#accept(StatementVisitor)} to navigate
 */
public abstract class Statement {
	public static final int INVALID_VAR_ID = -1;
	protected int outVarId = INVALID_VAR_ID;
	protected int[] inVarIds;
	private RStatementKind kind;
	
	public Statement(RStatementKind type) {
		this.kind = type;
	}

	public int getOutVarId() {
		return outVarId;
	}

	public int[] getInVarIds() {
		if (inVarIds == null) {
			return new int[0];
		}
		return inVarIds;
	}
	
	/**
	 * outVarId must be set for the execution
	 */
	public void setOutVarId(int outVarId) {
		this.outVarId = outVarId;
	}

	public void setInVarIds(int[] inVarIds) {
		this.inVarIds = inVarIds;
	}

	public boolean hasOutputVar() {
		return true;
	}

	public RStatementKind getKind() {
		return kind;
	}
	
	public abstract void accept(StatementVisitor visitor) throws Throwable;

	public static enum RStatementKind {
		ASSIGNMENT,
		ARRAY_ASSIGNMENT,
		CONSTRUCTOR,
		ARRAY_DECLARATION,
		ARRAY_CONSTRUCTOR,
		METHOD_INVOKE,
		QUERY_METHOD_INVOKE, 
		EVALUATION_METHOD,
	}
}
