package tzuyu.engine.model;

import java.io.Serializable;
import java.util.List;

public class Variable implements Comparable<Variable>, Serializable {
	private static final long serialVersionUID = 3401683653584869789L;
	
	public static final int VALUE_OF_LAST_STATEMENT = -1;
	public static final int RECEIVER_OF_LAST_STATEMENT = 0;
	public static final int PARAMETERS_OF_THE_LAST_STATEMENT = 1;
	
	/**
	 * The sequence that creates this variable, the variable is created or last
	 * modified by the last statement in the sequence.
	 */
	public final Sequence owner;
	/**
	 * The index of the variable in the last statement of sequence. -1 means the
	 * return value of the last statement; 0 is the receiver of the last
	 * statement if it is an instance method; 1 to other positive index are the
	 * parameters of the last statement
	 */
	private VarIndex varIdx;

	private Class<?> type = null;
	
	public Variable(Sequence seq, int stmtIdx, int varIdx) {
		if (seq == null) {
			throw new IllegalArgumentException("missing owner");
		}

		if (stmtIdx < 0 || stmtIdx > seq.size() - 1) {
			throw new IllegalArgumentException(
					"index falls out [0,owner.size()-1]:" + stmtIdx);
		}
		this.owner = seq;
		this.varIdx = new VarIndex(stmtIdx, varIdx);
	}

	public Variable(Sequence sequence, int index) {
		this(sequence, index, -1);
	}

	public Variable(Sequence seq, VarIndex varIdx) {
		this(seq, varIdx.getStmtIdx(), varIdx.getArgIdx());
	}

	public Class<?> getType() {
		if (type != null) {
			return type;
		}

		Statement stmt = owner.getStatement(varIdx.getStmtIdx());
		if (varIdx.isValueOfLastStmt()) {
			type = stmt.getOutputType();
		} else {
			List<Class<?>> types = stmt.getInputTypes();
			type = types.get(varIdx.getArgIdx());
		}
		return type;
	}

	public int getDeclIndex() {
		return varIdx.getStmtIdx();
	}

	public Statement getDeclaringStatement() {
		return owner.getStatement(varIdx.getStmtIdx());
	}

	public int compareTo(Variable o) {
		if (o == null)
			throw new IllegalArgumentException();
		if (o.owner != this.owner)
			throw new IllegalArgumentException();
		return this.varIdx.compareTo(o.getVarIndex());
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Variable)) {
			return false;
		}

		Variable other = (Variable) o;

		return this.owner.equals(other.owner) && getStmtIdx()
				== other.getStmtIdx()
				&& getArgIdx() == other.getArgIdx();
	}

	@Override
	public int hashCode() {
		return this.getStmtIdx() * 31 + this.owner.hashCode() * 19 + this.getArgIdx();
	}

	@Override
	public String toString() {
		return getType().getSimpleName() + "(" + getStmtIdx() + "," + getArgIdx() + ")";
	}

	public String getName() {
		return "var" + Integer.toString(getStmtIdx());
	}

	public int getStmtIdx() {
		return varIdx.getStmtIdx();
	}
	
	public Sequence getOwner() {
		return owner;
	}
	
	public int getArgIdx() {
		return varIdx.getArgIdx();
	}
	
	public VarIndex getVarIndex() {
		return varIdx;
	}
}
