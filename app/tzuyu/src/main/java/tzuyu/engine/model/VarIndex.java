package tzuyu.engine.model;

import sav.common.core.utils.ObjectUtils;

/**
 * Represents the two indices(statement index and argument index) of a variable.
 * 
 * @author Spencer Xiao
 * 
 */
public class VarIndex {

	/**
	 * The index of the statement which generates the variable.
	 */
	private int stmtIdx;
	/**
	 * The index of the parameter which the variable refers to. -1 represents
	 * the return value of the statement; non-negative values represent the
	 * normal indices of the parameters. Based on the assumption that the first
	 * parameter of instance method is the receiver and all other parameters are
	 * concatenated to the end of the receiver.
	 */
	private final int argIdx;

	public VarIndex(int stmtIndex, int argIndex) {
		this.stmtIdx = stmtIndex;
		this.argIdx = argIndex;
	}

	@Override
	public String toString() {
		return "statement index:" + stmtIdx + ", argument index:" + argIdx;
	}

	public int getArgIdx() {
		return argIdx;
	}

	public int getStmtIdx() {
		return stmtIdx;
	}
	
	public boolean isValueOfLastStmt() {
		return argIdx == Variable.VALUE_OF_LAST_STATEMENT;
	}
	
	// LLT
	public int compareTo(VarIndex o) {
		if (this == o) {
			return 0;
		}
		if (o == null) {
			return 1;
		}
		int stmtRe = ObjectUtils.compare(this.getStmtIdx(), o.getStmtIdx());
		if (stmtRe == 0) {
			return ObjectUtils.compare(this.getArgIdx(), o.getArgIdx());
		}
		return stmtRe;
	}

	public static VarIndex plus(VarIndex oldVarIdx, int preStmtNo) {
		return new VarIndex(oldVarIdx.getStmtIdx() + preStmtNo, oldVarIdx.getArgIdx());
	}
	
}
