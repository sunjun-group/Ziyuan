/**
 * Copyright TODO
 */
package gentest.data.variable;

import gentest.data.LocalVariable;
import gentest.data.statement.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LLT
 *
 */
public abstract class SelectedVariable implements ISelectedVariable {
	protected List<Statement> stmts;
	protected List<LocalVariable> newVariables;
	
	public SelectedVariable() {
		this(new ArrayList<Statement>(), new ArrayList<LocalVariable>());
	}
	
	public SelectedVariable(List<Statement> stmts, List<LocalVariable> vars) {
		this.stmts = stmts;
		this.newVariables = vars;
	}
	
	@Override
	public List<Statement> getStmts() {
		return stmts;
	}

	public List<LocalVariable> getNewVariables() {
		return newVariables;
	}

	public void setNewVariables(List<LocalVariable> newVariables) {
		this.newVariables = newVariables;
	}
	
}
