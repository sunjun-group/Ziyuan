package tzuyu.engine.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sav.common.core.utils.StringUtils;
import tzuyu.engine.model.exception.TzRuntimeException;
import tzuyu.engine.runtime.RAssignment;
import tzuyu.engine.utils.ArrayListSimpleList;
import tzuyu.engine.utils.ListOfLists;
import tzuyu.engine.utils.OneMoreElementList;
import tzuyu.engine.utils.Randomness;
import tzuyu.engine.utils.ReflectionUtils;
import tzuyu.engine.utils.ReflectionUtils.Match;
import tzuyu.engine.utils.SimpleList;

public class Sequence implements Serializable {

	private static final long serialVersionUID = 2179954648669642006L;

	private SimpleList<Statement> statements;

	/**
	 * The return type of the last statement
	 */
	private Class<?> returnType;
	private List<Variable> returnVar;

	private List<Class<?>> lastStmtTypes;
	private List<Variable> lastStmtVariables;

	private List<List<Variable>> outReferenceVars;

	private Variable receiver;

	private int savedNetSize;
	private int savedHashCode;

	private Sequence(SimpleList<Statement> stmts, int hashCode, int netSize) {
		this.statements = stmts;
		this.savedNetSize = netSize;
		this.savedHashCode = hashCode;
		this.returnVar = new ArrayList<Variable>();
		this.outReferenceVars = new ArrayList<List<Variable>>();
		computeLastStatementInfo();
	}

	private Sequence(SimpleList<Statement> stmts) {
		this(stmts, computeHashCode(stmts), computeNetSize(stmts));
	}

	public Sequence() {
		this(new ArrayListSimpleList<Statement>(), 0, 0);
	}

	public void setReceiver(Variable newReceiver) {
		if (newReceiver == null) {
			throw new IllegalArgumentException("new receiver cannot be null");
		}
		if (!this.equals(newReceiver.owner)) {
			throw new TzRuntimeException(
					"the new receiver's owner is not this sequence");
		}

		this.receiver = newReceiver;
	}

	public Variable getReceiver() {
		if (receiver == null) {
			throw new TzRuntimeException("receiver is not set before get");
		}

		return receiver;
	}

	private void computeLastStatementInfo() {

		lastStmtTypes = new ArrayList<Class<?>>();
		lastStmtVariables = new ArrayList<Variable>();

		if (this.statements.size() > 0) {
			Statement si = this.statements.get(statements.size() - 1);
			if (si.isVoidMethodCall()) {
				returnType = void.class;
			} else {
				returnType = si.getOutputType();
			}

			returnVar.add(new Variable(this, statements.size() - 1,
					Variable.VALUE_OF_LAST_STATEMENT));

			List<RelativeNegativeIndex> inputVars = si.getInputVars();
			List<Class<?>> inputTypes = si.getInputTypes();
			if (inputVars.size() != inputTypes.size()) {
				throw new TzRuntimeException("number of parameters does not match",
						inputVars, inputTypes);
			}

			List<Variable> vars = this.getInputs(statements.size() - 1);
			if (vars.size() != inputTypes.size()) {
				throw new RuntimeException(
						"number of parameters does not match");
			}
			List<Variable> outVars = new ArrayList<Variable>();
			for (int i = 0; i < vars.size(); i++) {
				Variable var = vars.get(i);
				lastStmtTypes.add(var.getType());
				lastStmtVariables.add(var);
				Variable idx = getVariableForInput(size() - 1, inputVars.get(i));
				outVars.add(idx);
			}

			outReferenceVars.add(outVars);
		}
	}

	public List<Variable> getInputs(int stmtIdx) {
		List<Variable> inputAsVariables = new ArrayList<Variable>();
		for (RelativeNegativeIndex idx : statements.get(stmtIdx).getInputVars()) {
			inputAsVariables.add(getVariableForInput(stmtIdx, idx));
		}
		return inputAsVariables;
	}

	private Variable getVariableForInput(int pos, RelativeNegativeIndex input) {
		int absoluteIndex = pos + input.stmtIdx;
		if (absoluteIndex < 0) {
			throw new IllegalArgumentException("invalid index "
					+ "(expecting non-negative):" + absoluteIndex);
		}
		return new Variable(this, absoluteIndex, input.argIdx);
	}

	public Statement getStatement(int index) {
		if (index < 0 || index >= statements.size()) {
			return null;
		}

		return statements.get(index);
	}

	public int size() {
		return statements.size();
	}

	/**
	 * Append a the statement<code>stmt</code> with input variables in
	 * <code>inputVars</code> to the end of this sequence. The precondition for
	 * this operation is that, all the sequences referenced in
	 * <code>inputVars</code> must be the same as this sequence and the
	 * <code>stmtIndex</code> of each variable is the absolute statement index
	 * of this sequence. NOTE THAT: this operation returns a new sequence which
	 * means it will not affect this sequence.
	 * 
	 * @param stmt
	 *            statement to be appended to the end of this sequence.
	 * @param inputVars
	 *            the input variables for the <code>stmt</code>.
	 * @return the newly created sequence that is equal to
	 *         <code>this+stmt</code>.
	 */
	public Sequence extend(TzuYuAction stmt, List<Variable> inputVars) {
		List<RelativeNegativeIndex> indexList = new ArrayList<RelativeNegativeIndex>(
				1);
		for (Variable var : inputVars) {
			indexList.add(getRelativeIndexForVariable(size(), var));
		}
		Statement newStmt = new Statement(stmt, indexList);
		int netSize = newStmt.isPrimitive() ? savedNetSize : (savedNetSize + 1);
		return new Sequence(new OneMoreElementList<Statement>(statements,
				newStmt), savedHashCode + newStmt.hashCode(), netSize);
	}
	
	public Sequence extend(Statement stmt) {
		int netSize = stmt.isPrimitive() ? savedNetSize : (savedNetSize + 1);
		return new Sequence(
				new OneMoreElementList<Statement>(statements, stmt),
				savedHashCode + stmt.hashCode(), netSize);
	}

	/**
	 * Calculate the relative negative index for the given variable. The
	 * relative negative index is calculated by -(
	 * <code>stmtIndex - v.stmtIndex</code>).
	 */
	private static RelativeNegativeIndex getRelativeIndexForVariable(
			int stmtIndex, Variable v) {
		if (v.getStmtIdx() >= stmtIndex) {
			throw new IllegalArgumentException(
					"the statement index out of range");
		}
		return new RelativeNegativeIndex(-(stmtIndex - v.getStmtIdx()),
				v.getArgIdx());
	}

	public List<Variable> getAllVariables() {
		List<Variable> retval = new ArrayList<Variable>();
		for (int index = 0; index < this.statements.size(); index++) {
			retval.add(new Variable(this, index));
		}
		return retval;
	}

	/**
	 * The hash code of a sequence is the sum of every statement's hash code.
	 * This seems good enough and it makes computing hash code of a
	 * concatenation of sequences faster (it's just the addition of each
	 * sequences's hash code). Otherwise, hash code computation used to be a hot
	 * spot.
	 * 
	 * @param statements
	 * @return
	 */
	private static int computeHashCode(SimpleList<Statement> statements) {
		int hashCode = 0;
		for (int i = 0; i < statements.size(); i++) {
			Statement s = statements.get(i);
			hashCode += s.hashCode();
		}
		return hashCode;
	}

	/**
	 * The number of statements that are not primitive assignment, i.e.
	 * "int var7 = 0"
	 * 
	 * @param statements
	 * @return
	 */
	private static int computeNetSize(SimpleList<Statement> statements) {
		int netSize = 0;
		for (int index = 0; index < statements.size(); index++) {
			Statement stmt = statements.get(index);
			if (stmt.isPrimitive()) {
				continue;
			}
			netSize++;
		}
		return netSize;
	}

	public final SimpleList<Statement> getStatementsWithInputs() {
		// The list is constructed unmodifiable so we can just return it.
		return this.statements;
	}

	public List<Variable> getLastStatementVariables() {
		return this.lastStmtVariables;
	}

	public List<Class<?>> getLastStatementTypes() {
		return this.lastStmtTypes;
	}

	public Class<?> getLastStmtType() {
		return returnType;
	}

	public Variable getLastStmtReturnVariable() {
		return returnVar.get(returnVar.size() - 1);
	}

	public boolean isActive(int declIndex) {
		return true;
	}

	public Variable randomVarOfTypeLastStatement(Class<?> clazz, Match match) {
		List<Variable> possibleVariables = valuesInLastStatement(clazz, match);
		if (possibleVariables.isEmpty())
			return null;
		return Randomness.randomMember(possibleVariables);
	}

	private List<Variable> valuesInLastStatement(Class<?> clazz, Match match) {
		if (clazz == null || match == null)
			throw new IllegalArgumentException("parameters cannot be null.");
		List<Variable> indices = new ArrayList<Variable>(
				lastStmtVariables.size());
		for (int index = 0; index < this.lastStmtVariables.size(); index++) {
			Variable var = this.lastStmtVariables.get(index);
			Statement s = statements.get(var.getStmtIdx());
			if (!s.isVoidMethodCall()
					&& varTypeMatches(s.getOutputType(), clazz, match)) {
				indices.add(var);
			}
		}

		for (int index = 0; index < this.returnVar.size(); index++) {
			Variable var = this.returnVar.get(index);
			Statement s = statements.get(var.getStmtIdx());
			if (!s.isVoidMethodCall()
					&& varTypeMatches(s.getOutputType(), clazz, match)) {
				indices.add(var);
			}
		}
		return indices;
	}

	private boolean varTypeMatches(Class<?> t, Class<?> clazz, Match match) {
		switch (match) {
		case COMPATIBLE_TYPE:
			return ReflectionUtils.canBeUsedAs(t, clazz);
		case EXACT_TYPE:
			return t.equals(clazz);
		default:
			return false;
		}
	}

	protected void checkIndex(int i) {
		if (i < 0 || i > size() - 1) {
			throw new IllegalArgumentException("invalid index");
		}
	}

	public static Sequence concatenate(List<Sequence> sequences) {
		List<SimpleList<Statement>> stmts = new ArrayList<SimpleList<Statement>>();
		int newHashCode = 0;
		int newNetSize = 0;
		for (Sequence c : sequences) {
			newHashCode += c.savedHashCode;
			newNetSize += c.savedNetSize;
			stmts.add(c.statements);
		}

		return new Sequence(new ListOfLists<Statement>(stmts), newHashCode,
				newNetSize);
	}

	/**
	 * Get the type comparable variable from this sequence. The returned
	 * sequence MUST be the prefix upon to the end of the statement that
	 * generates this variable, not the whole statements in this sequence.
	 * 
	 * @param type
	 * @return
	 */
	public List<Variable> getMatchedVariable(Class<?> type) {
		List<Variable> variables = new ArrayList<Variable>();
		for (int index = 0; index < statements.size(); index++) {
			Statement stmt = statements.get(index);

			// Handle the return variable first
			if (varTypeMatches(stmt.getOutputType(), type,
					Match.COMPATIBLE_TYPE)) {
				List<Statement> prefix = this.statements.subList(0, index + 1);
				Sequence newSeq = new Sequence(
						new ArrayListSimpleList<Statement>(
								new ArrayList<Statement>(prefix)));
				variables.add(new Variable(newSeq, index));
			}

			List<Class<?>> inputTypes = stmt.getInputTypes();
			// Handle the out reference variable
			for (int idx = 0; idx < inputTypes.size(); idx++) {
				Class<?> inputType = inputTypes.get(idx);
				if (varTypeMatches(inputType, type, Match.COMPATIBLE_TYPE)) {
					List<Statement> prefix = this.statements.subList(0,
							index + 1);
					Sequence newSeq = new Sequence(
							new ArrayListSimpleList<Statement>(
									new ArrayList<Statement>(prefix)));
					variables.add(new Variable(newSeq, index, idx));
					// TODO we could also choose the same input variable
					// as the stmt.
				}
			}
		}
		return variables;
	}

	public Variable getVariable(int i) {
		checkIndex(i);
		return new Variable(this, i);
	}

	public static Sequence create(StatementKind primitive) {
		return new Sequence().extend(TzuYuAction.fromStatmentKind(primitive),
				Collections.<Variable> emptyList());
	}

	public void updateReceiver() {
		Statement lastStatement = statements.get(size() - 1);
		if (lastStatement.statement.getAction().hasReceiverParameter()) {
			Variable newReceiver = new Variable(this, size() - 1,
					Variable.RECEIVER_OF_LAST_STATEMENT);
			this.receiver = newReceiver;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof Sequence)) {
			return false;
		}

		Sequence obj = (Sequence) o;
		return this.statements.equals(obj.statements);

	}

	/**
	 * Get the variable sequence for the parameter at index
	 * <code>varIndex</code> of the statement corresponding to the query action
	 * at index <code>reverseQueryIndex</code> in reverse order. The variable
	 * sequence is an independent sequence that generated the parameter value in
	 * the statement at index <code>reverseStmtIndex</code> in reverse order.
	 */
	public Variable getVariableSequence(int reverseQueryIndex, int varIndex) {
		/* TODO LLT: for supporting static methods
		 * but still get problem with this solution because
		 * sometimes the statements is duplicated in the sequence 
		 * (see the mail) 
		 *  */
//		int stmtIndex = this.size() - 1;
//		int curRevQueryIdx = -1;
//		Statement stmt = null;
//		for (; stmtIndex > 0; stmtIndex --) {
//			stmt = statements.get(stmtIndex);
//			if (stmt.getAction().getAction() instanceof RMethod) {
//				if (++ curRevQueryIdx == reverseQueryIndex) {
//					break;
//				}
//			}
//		}
//		Assert.assertNotNull(stmt, "Cannot find statement in the squence");
		int stmtIndex = this.size() - 1;

		for (int index = 0; index < reverseQueryIndex; index++) {
			Statement statement = statements.get(stmtIndex);
			RelativeNegativeIndex relativeIndex = statement.getInputVars().get(
					0);
			stmtIndex = stmtIndex + relativeIndex.stmtIdx;
		}

		Statement stmt = statements.get(stmtIndex);
		// For the first parameter which could be the receiver for instance
		// method
		// or the real first parameter for class method.
		if (varIndex == 0) {
			RelativeNegativeIndex relVarIdx = stmt.getInputVars().get(varIndex);

			int startIdx = 0;
			int endIdx = stmtIndex + relVarIdx.stmtIdx;

			List<Statement> stmts = statements.subList(startIdx, endIdx + 1);
			Sequence newSeq = new Sequence(new ArrayListSimpleList<Statement>(
					new ArrayList<Statement>(stmts)));
			return new Variable(newSeq, newSeq.size() - 1, relVarIdx.argIdx);
		} else {
			RelativeNegativeIndex preVarIdx = stmt.getInputVars().get(
					varIndex - 1);
			RelativeNegativeIndex relVarIdx = stmt.getInputVars().get(varIndex);

	 		// NOTES: Here we increase the stmtIndex of the previous statement by 1
			// to get the starting address of the next statement. This is guaranteed 
			// that the referenced variables are the output of the last statement in 
			// its parameters sequence. Even when the variable referenced are in the 
			// middle of the original parameter sequence, but the original sequence 
			// was chopped off before passing to this sequence. Thus, increasing 1 
			// will not result in the mix of the remaining statements in the original 
			// sequence with the whole of the next parameter sequence.
			int startIdx = stmtIndex + preVarIdx.stmtIdx + 1;
			int endIdx = stmtIndex + relVarIdx.stmtIdx;
			List<Statement> stmts = statements.subList(startIdx, endIdx + 1);
			Sequence newSeq = new Sequence(new ArrayListSimpleList<Statement>(
					new ArrayList<Statement>(stmts)));
			return new Variable(newSeq, newSeq.size() - 1, relVarIdx.argIdx);
		}
	}
	
	//LLT: for debugging
	@Override
	public String toString() {
		if (statements == null || statements.size() == 0) {
			return StringUtils.EMPTY;
		}
		List<Statement> stmts = statements.toJDKList();
		StringBuilder sb = new StringBuilder();
		for (Statement stmt : stmts) {
			sb.append(stmt.toString()).append("\n");
		}
		return sb.toString();
	}

	public static boolean canUseShortFormat(Statement stmt) {
		return (stmt.getAction().getAction() instanceof RAssignment)
				&& ((RAssignment) stmt.getAction().getAction()).getValue() != null;

	}
}