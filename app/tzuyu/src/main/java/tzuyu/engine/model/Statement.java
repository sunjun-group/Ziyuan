package tzuyu.engine.model;

import java.util.List;

import tzuyu.engine.model.exception.TzRuntimeException;

/**
 * A wrapper for a guarded statement with input variables.
 * 
 * @author Spencer Xiao
 * 
 */
public class Statement {

	public final TzuYuAction statement;
	private final List<RelativeNegativeIndex> inputVars;

	public Statement(TzuYuAction stmt, List<RelativeNegativeIndex> indexList) {
		if (!checkParameterCompatability(stmt, indexList)) {
			throw new TzRuntimeException("parameter types do not match",
					stmt.getInputTypes(), indexList);
		}
		this.statement = stmt;
		this.inputVars = indexList;
	}

	public List<RelativeNegativeIndex> getInputVars() {
		return this.inputVars;
	}

	private boolean checkParameterCompatability(TzuYuAction stmt,
			List<RelativeNegativeIndex> params) {
		if (stmt.getInputTypes().size() != params.size()) {
			return false;
		}
		for (int i = 0; i < params.size(); i++) {
			// check type compatibility
		}
		return true;
	}

	public TzuYuAction getAction() {
		return statement;
	}

	public Class<?> getOutputType() {
		return statement.getOutputType();
	}

	public boolean isVoidMethodCall() {
		return statement.getOutputType().equals(void.class);
	}

	public boolean isPrimitive() {
		return statement.isPrimitive();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof Statement)) {
			return false;
		}

		Statement obj = (Statement) o;

		return this.inputVars.equals(obj.inputVars);
	}

	@Override
	public int hashCode() {
		return inputVars.hashCode();
	}

	public List<Class<?>> getInputTypes() {
		return statement.getInputTypes();
	}
	
	public StatementKind getOrgStatement() {
		return statement.getAction();
	}

	@Override
	public String toString() {
		return statement.toString();
	}
}
