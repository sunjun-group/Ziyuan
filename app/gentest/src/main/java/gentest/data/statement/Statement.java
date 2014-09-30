/**
 * Copyright TODO
 */
package gentest.data.statement;



/**
 * @author LLT
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

	public abstract boolean hasOutputVar();
//	public boolean hasOutputVar() {
//		return outVarId != INVALID_VAR_ID;
//	}

	public RStatementKind getKind() {
		return kind;
	}
	
	public abstract void accept(StatementVisitor visitor) throws Throwable;

	public static enum RStatementKind {
		ASSIGNMENT,
		CONSTRUCTOR,
		METHOD_INVOKE,
		QUERY_METHOD_INVOKE, 
		EVALUATION_METHOD,
	}
}
